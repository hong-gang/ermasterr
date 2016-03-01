package org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class NoTableNameRule extends TableRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(final ERTable table) {
        if (table.getPhysicalName() == null || table.getPhysicalName().trim().equals("")) {
            final ValidateResult validateResult = new ValidateResult();
            validateResult.setMessage(ResourceString.getResourceString("error.validate.no.table.name"));
            validateResult.setLocation(table.getLogicalName());
            validateResult.setSeverity(IMarker.SEVERITY_WARNING);
            validateResult.setObject(table);

            addError(validateResult);
        }

        return true;
    }
}
