package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;

public class ERDiagramMatchHeightAction extends MatchHeightAction {

    public ERDiagramMatchHeightAction(final IWorkbenchPart part) {
        super(part);
        setImageDescriptor(ERDiagramActivator.getImageDescriptor(ImageKey.MATCH_HEIGHT));
        setDisabledImageDescriptor(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List getSelectedObjects() {
        final List objects = new ArrayList(super.getSelectedObjects());
        boolean first = true;

        for (final Iterator iter = objects.iterator(); iter.hasNext();) {
            final Object object = iter.next();

            if (!(object instanceof EditPart)) {
                iter.remove();

            } else {
                final EditPart editPart = (EditPart) object;
                if (editPart instanceof NormalColumnEditPart) {
                    iter.remove();

                } else {
                    if (first) {
                        editPart.setSelected(2);
                        first = false;
                    }
                }
            }
        }

        return objects;
    }

}
