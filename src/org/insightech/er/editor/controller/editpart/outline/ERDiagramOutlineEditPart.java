package org.insightech.er.editor.controller.editpart.outline;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;

public class ERDiagramOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final List<AbstractModel> modelChildren = new ArrayList<AbstractModel>();
        final ERDiagram diagram = (ERDiagram) getModel();
        final DiagramContents diagramContents = diagram.getDiagramContents();

        modelChildren.add(diagramContents.getGroups());
        modelChildren.add(diagramContents.getDictionary());
        modelChildren.add(diagramContents.getContents().getTableSet());
        modelChildren.add(diagramContents.getContents().getViewSet());
        modelChildren.add(diagramContents.getTriggerSet());
        modelChildren.add(diagramContents.getSequenceSet());
        modelChildren.add(diagramContents.getIndexSet());
        modelChildren.add(diagramContents.getTablespaceSet());

        return modelChildren;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals("refreshOutline")) {
            refreshOutline();

        } else if (event.getPropertyName().equals("refreshChildren")) {
            refreshOutline();

        } else if (event.getPropertyName().equals("refresh")) {
            refreshOutline();

        } else if (event.getPropertyName().equals("refreshVisuals")) {
            refreshOutline();

        } else if (event.getPropertyName().equals("refreshSettings")) {
            refreshOutline();

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {}
}
