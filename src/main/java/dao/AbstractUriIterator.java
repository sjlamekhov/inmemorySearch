package dao;

import objects.AbstractObjectUri;

import java.util.Iterator;

public abstract class AbstractUriIterator <U extends AbstractObjectUri> implements Iterator<U> {

    protected final String tenantId;
    protected final int length;
    protected final long maxValue;
    protected long currentValue = 0;
    protected String cursorId;

    public AbstractUriIterator(String tenantId, int length) {
        this.tenantId = tenantId;
        this.length = length;
        this.maxValue = calculateMaxHexValueByLength(length);
    }

    private long calculateMaxHexValueByLength(int length) {
        long result = 0;
        while (length-- > 0) {
            result <<= 4;
            result += 0xF;
        }
        return result;
    }

    @Override
    public boolean hasNext() {
        return currentValue <= maxValue;
    }

    public String getCursorId() {
        return cursorId;
    }

    public AbstractUriIterator<U> setCursorId(String cursorId) {
        this.cursorId = cursorId;
        return this;
    }
}
