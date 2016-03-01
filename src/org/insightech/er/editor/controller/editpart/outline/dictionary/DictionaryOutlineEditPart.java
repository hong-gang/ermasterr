package org.insightech.er.editor.controller.editpart.outline.dictionary;

import java.util.Collections;
import java.util.List;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.settings.Settings;

public class DictionaryOutlineEditPart extends AbstractOutlineEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        final Dictionary dictionary = (Dictionary) getModel();
        final List<Word> list = dictionary.getWordList();

        if (getDiagram().getDiagramContents().getSettings().getViewOrderBy() == Settings.VIEW_MODE_LOGICAL) {
            Collections.sort(list, Word.LOGICAL_NAME_COMPARATOR);

        } else {
            Collections.sort(list, Word.PHYSICAL_NAME_COMPARATOR);

        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refreshOutlineVisuals() {
        setWidgetText(ResourceString.getResourceString("label.dictionary") + " (" + getModelChildren().size() + ")");
        setWidgetImage(ERDiagramActivator.getImage(ImageKey.DICTIONARY));
    }

}
