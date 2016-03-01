package org.insightech.er.db.impl.mysql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerEclipseBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.util.Format;

public class MySQLTableImportManager extends ImportFromDBManagerEclipseBase {

    private final Map<String, MySQLTableProperties> tablePropertiesMap;

    public MySQLTableImportManager() {
        tablePropertiesMap = new HashMap<String, MySQLTableProperties>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getViewDefinitionSQL(final String schema) {
        if (schema != null) {
            return "SELECT view_definition FROM information_schema.views WHERE table_schema = ? AND table_name = ?";

        } else {
            return "SELECT view_definition FROM information_schema.views WHERE table_name = ?";

        }
    }

    @Override
    protected List<Index> getIndexes(final ERTable table, final DatabaseMetaData metaData, final List<PrimaryKeyData> primaryKeys) throws SQLException {

        final List<Index> indexes = super.getIndexes(table, metaData, primaryKeys);

        for (final Iterator<Index> iter = indexes.iterator(); iter.hasNext();) {
            final Index index = iter.next();

            if ("PRIMARY".equalsIgnoreCase(index.getName())) {
                iter.remove();
            }
        }

        return indexes;
    }

    @Override
    protected String getConstraintName(final PrimaryKeyData data) {
        return null;
    }

    @Override
    protected void cacheOtherColumnData(final String tableName, final String schema, final ColumnData columnData) throws SQLException {
        final String tableNameWithSchema = dbSetting.getTableNameWithSchema(tableName, schema);

        final SqlType sqlType = SqlType.valueOfId(columnData.type);

        if (sqlType != null && sqlType.doesNeedArgs()) {
            final String restrictType = getRestrictType(tableNameWithSchema, columnData);

            final Pattern p = Pattern.compile(columnData.type.toLowerCase() + "\\((.*)\\)");
            final Matcher m = p.matcher(restrictType);

            if (m.matches()) {
                columnData.enumData = m.group(1);
            }

        } else if (columnData.type.equals("year")) {
            final String restrictType = getRestrictType(tableNameWithSchema, columnData);
            columnData.type = restrictType;

        } else if (columnData.type.toUpperCase().indexOf(" UNSIGNED") != -1) {
            final String restrictType = getRestrictType(tableNameWithSchema, columnData);
            columnData.type = restrictType;

        }
    }

    private String getRestrictType(final String tableNameWithSchema, final ColumnData columnData) throws SQLException {
        String type = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement("SHOW COLUMNS FROM `" + tableNameWithSchema + "` LIKE ?");

            ps.setString(1, columnData.columnName);
            rs = ps.executeQuery();

            if (rs.next()) {
                type = rs.getString("Type");
            }

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }

        return type;
    }

    @Override
    protected ColumnData createColumnData(final ResultSet columnSet) throws SQLException {
        final ColumnData columnData = super.createColumnData(columnSet);
        final String type = columnData.type.toLowerCase();

        if (type.startsWith("float")) {
            // there are two case.
            // 1. float unsigned zerofill --> 0,0
            // 2. float(12,0) --> 12,0
            // So, I change 12,0 --> 0,0
            if (columnData.size == 12 && columnData.decimalDegits == 0) {
                columnData.size = 0;
            }

        } else if (type.startsWith("bigint")) {
            // there are two case.
            // 1. bigint(20) unsigned zerofill --> 20,0
            // 2. bigint(19) --> 19,0
            // So, I change 19,0 --> 20,0
            if (columnData.size == 19) {
                columnData.size = 20;
            }

        } else if (type.startsWith("mediumint")) {
            // there are two case.
            // 1. mediumint(8) unsigned zerofill --> 8,0
            // 2. mediumint(7) --> 7,0
            // So, I change 7,0 --> 8,0
            if (columnData.size == 7) {
                columnData.size = 8;
            }

        }

        return columnData;
    }

    @Override
    protected void cacheColumnData(final List<DBObject> dbObjectList, final ProgressMonitor monitor) throws SQLException, InterruptedException {
        super.cacheColumnData(dbObjectList, monitor);

        PreparedStatement ps = null;
        ResultSet rs = null;

        final String sql = "SELECT T.TABLE_NAME, T.TABLE_COLLATION, C.COLUMN_NAME, " + "C.CHARACTER_SET_NAME, C.COLLATION_NAME, C.COLUMN_TYPE " + "FROM INFORMATION_SCHEMA.TABLES T, INFORMATION_SCHEMA.COLUMNS C " + "WHERE T.TABLE_SCHEMA = ? AND T.TABLE_NAME = C.TABLE_NAME";

        try {
            ps = con.prepareStatement(sql);

            ps.setString(1, dbSetting.getDatabase());
            rs = ps.executeQuery();

            while (rs.next()) {
                final String tableName = rs.getString("TABLE_NAME");
                final String columnName = rs.getString("COLUMN_NAME");
                final String tableCollation = Format.null2blank(rs.getString("TABLE_COLLATION"));
                String tableCharacterSet = "";
                final String characterSet = Format.null2blank(rs.getString("CHARACTER_SET_NAME"));
                final String collation = Format.null2blank(rs.getString("COLLATION_NAME"));

                // String type = Format.null2blank(rs.getString("COLUMN_TYPE"));

                if (!tableCollation.isEmpty()) {
                    final int index = tableCollation.indexOf("_");
                    if (index != -1) {
                        tableCharacterSet = tableCollation.substring(0, index);
                    }
                }

                MySQLTableProperties tableProperties = tablePropertiesMap.get(tableName);
                if (tableProperties == null) {
                    tableProperties = new MySQLTableProperties();

                    tableProperties.setCharacterSet(tableCharacterSet);
                    tableProperties.setCollation(tableCollation);

                    tablePropertiesMap.put(tableName, tableProperties);
                }

                final Map<String, ColumnData> columnDataMap = columnDataCache.get(tableName);
                final ColumnData columnData = columnDataMap.get(columnName);

                if (columnData != null) {
                    if (!tableCharacterSet.equals(characterSet)) {
                        columnData.characterSet = characterSet;
                    }

                    if (!tableCollation.equals(collation)) {
                        columnData.collation = collation;
                    }

                    if (collation.endsWith("_bin")) {
                        columnData.isBinary = true;
                    }
                }
            }

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }

    }

    @Override
    protected void setTableViewProperties(final String tableName, final TableViewProperties tableViewProperties) {
        super.setTableViewProperties(tableName, tableViewProperties);

        final MySQLTableProperties tableProperties = (MySQLTableProperties) tableViewProperties;

        final MySQLTableProperties cachedTableProperties = tablePropertiesMap.get(tableName);

        if (cachedTableProperties != null) {
            tableProperties.setCharacterSet(cachedTableProperties.getCharacterSet());
            tableProperties.setCollation(cachedTableProperties.getCollation());
        }
    }

}
