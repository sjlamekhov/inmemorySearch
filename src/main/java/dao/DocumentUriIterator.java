package dao;

import objects.DocumentUri;

public class DocumentUriIterator extends AbstractUriIterator<DocumentUri> {

    public DocumentUriIterator(String tenantId, int length) {
        super(tenantId, length);
    }

    @Override
    public DocumentUri next() {
        return new DocumentUri(Long.toHexString(currentValue++), tenantId);
    }

}
