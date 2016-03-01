package org.insightech.er.editor.controller.editpart.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.note.NoteDirectEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.view.editmanager.NoteCellEditor;
import org.insightech.er.editor.view.editmanager.NoteEditManager;
import org.insightech.er.editor.view.editmanager.NoteEditorLocator;
import org.insightech.er.editor.view.figure.NoteFigure;

public class NoteEditPart extends NodeElementEditPart implements IResizable {

    private NoteEditManager editManager = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        final NoteFigure noteFigure = new NoteFigure();

        changeFont(noteFigure);

        return noteFigure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeElementComponentEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NoteDirectEditPolicy());

        super.createEditPolicies();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRefreshVisuals() {
        final Note note = (Note) getModel();

        final NoteFigure figure = (NoteFigure) getFigure();

        figure.setText(note.getText(), note.getColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRequest(final Request request) {
        if (request.getType().equals(RequestConstants.REQ_DIRECT_EDIT) || request.getType().equals(RequestConstants.REQ_OPEN)) {
            performDirectEdit();
        }
    }

    private void performDirectEdit() {
        if (editManager == null) {
            editManager = new NoteEditManager(this, NoteCellEditor.class, new NoteEditorLocator(getFigure()));
        }

        editManager.show();
    }

    @Override
    protected void performRequestOpen() {}
}
