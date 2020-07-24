package dao;

import objects.AbstractObject;

import java.util.List;

public class ExtractObjectsResult <T extends AbstractObject> {

    private final String cursorId;
    private final boolean hasNext;
    private final List<T> objects;

    public ExtractObjectsResult(String cursorId, boolean hasNext, List<T> objects) {
        this.cursorId = cursorId;
        this.hasNext = hasNext;
        this.objects = objects;
    }

    public String getCursorId() {
        return cursorId;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public List<T> getObjects() {
        return objects;
    }
}
