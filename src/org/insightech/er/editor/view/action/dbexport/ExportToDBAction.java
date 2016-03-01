package org.insightech.er.editor.view.action.dbexport;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RetargetAction;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.dbexport.ExportDBSettingDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportErrorDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToDBDialog;

public class ExportToDBAction extends AbstractBaseAction {

    public static final String ID = ExportToDBAction.class.getName();

    private final Validator validator;

    public ExportToDBAction(final ERDiagramEditor editor) {
        super(ID, ResourceString.getResourceString("action.title.export.db"), editor);

        validator = new Validator();
    }

    @Override
    public void execute(final Event event) {
        final ERDiagram diagram = getDiagram();

        final List<ValidateResult> errorList = validator.validate(diagram);

        if (!errorList.isEmpty()) {
            final ExportErrorDialog dialog = new ExportErrorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), errorList);
            dialog.open();
            return;
        }

        final ExportDBSettingDialog dialog = new ExportDBSettingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

        if (dialog.open() == IDialogConstants.OK_ID) {
            final String ddl = dialog.getDdl();

            final DBSetting dbSetting = dialog.getDbSetting();

            final ExportToDBDialog exportToDBDialog = new ExportToDBDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram, dbSetting, ddl);

            exportToDBDialog.open();
        }
    }

    public static class ExportToDBRetargetAction extends RetargetAction {

        public ExportToDBRetargetAction() {
            super(ID, ResourceString.getResourceString("action.title.export.db"));

            setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.EXPORT_TO_DB));
            setToolTipText(getText());
        }

    }
}
