package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.util.POIUtils;

public class AllIndicesSheetGenerator extends IndexSheetGenerator {

    @Override
    public void generate(final ProgressMonitor monitor, final HSSFWorkbook workbook, final int sheetNo, final boolean useLogicalNameAsSheetName, final Map<String, Integer> sheetNameMap, final Map<String, ObjectModel> sheetObjectMap, final ERDiagram diagram, final Map<String, LoopDefinition> loopDefinitionMap) throws InterruptedException {
        clear();

        final LoopDefinition loopDefinition = loopDefinitionMap.get(getTemplateSheetName());

        final HSSFSheet newSheet = createNewSheet(workbook, sheetNo, loopDefinition.sheetName, sheetNameMap);

        final String sheetName = workbook.getSheetName(workbook.getSheetIndex(newSheet));

        sheetObjectMap.put(sheetName, diagram.getDiagramContents().getIndexSet());

        final HSSFSheet oldSheet = workbook.getSheetAt(sheetNo);

        boolean first = true;

        for (final ERTable table : diagram.getDiagramContents().getContents().getTableSet()) {

            if (diagram.getCurrentCategory() != null && !diagram.getCurrentCategory().contains(table)) {
                continue;
            }

            for (final Index index : table.getIndexes()) {
                monitor.subTaskWithCounter(sheetName + " - " + table.getName() + " - " + index.getName());

                if (first) {
                    first = false;

                } else {
                    POIUtils.copyRow(oldSheet, newSheet, loopDefinition.startLine - 1, oldSheet.getLastRowNum(), newSheet.getLastRowNum() + loopDefinition.spaceLine + 1);
                }

                setIndexData(workbook, newSheet, index);

                newSheet.setRowBreak(newSheet.getLastRowNum() + loopDefinition.spaceLine);

                monitor.worked(1);
            }
        }

        if (first) {
            for (int i = loopDefinition.startLine - 1; i <= newSheet.getLastRowNum(); i++) {
                final HSSFRow row = newSheet.getRow(i);
                if (row != null) {
                    newSheet.removeRow(row);
                }
            }
        }
    }

    @Override
    public String getTemplateSheetName() {
        return "all_indices_template";
    }

}
