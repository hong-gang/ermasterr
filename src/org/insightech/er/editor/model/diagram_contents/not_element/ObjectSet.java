package org.insightech.er.editor.model.diagram_contents.not_element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.AbstractObjectModel;
import org.insightech.er.editor.model.ObjectListModel;

public abstract class ObjectSet<T extends AbstractObjectModel> extends AbstractModel implements ObjectListModel, Iterable<T> {

    private static final long serialVersionUID = 1L;

    private List<T> objectList;

    public ObjectSet() {
        this.objectList = new ArrayList<T>();
    }

    public void sort() {
        Collections.sort(this.objectList);
    }

    public void clear() {
        this.objectList.clear();
    }

    public void addObject(final T object) {
        this.objectList.add(object);
    }

    public int remove(final T object) {
        final int index = this.objectList.indexOf(object);
        this.objectList.remove(index);

        return index;
    }

    public boolean contains(final String name) {
        for (final T object : objectList) {
            if (name.equalsIgnoreCase(object.getName())) {
                return true;
            }
        }

        return false;
    }

    public T get(final String name) {
        for (final T object : objectList) {
            if (name.equalsIgnoreCase(object.getName())) {
                return object;
            }
        }

        return null;
    }

    public List<T> getObjectList() {
        return this.objectList;
    }

    @Override
    public Iterator<T> iterator() {
        return this.objectList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ObjectSet<T> clone() {
        final ObjectSet<T> objectSet = (ObjectSet<T>) super.clone();
        final List<T> newObjectList = new ArrayList<T>();

        for (final T object : this.objectList) {
            final T newObject = (T) object.clone();
            newObjectList.add(newObject);
        }

        objectSet.objectList = newObjectList;

        return objectSet;
    }

    @Override
    public String getObjectType() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "";
    }

}
