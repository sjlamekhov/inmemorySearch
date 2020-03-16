package search;


import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.Collection;

public interface SearchService<U extends AbstractObjectUri, T extends AbstractObject> {

    void addObjectToIndex(T object);
    void removeObjectFromIndex(T object);
    Collection<U> search(String tenantId, SearchRequest searchRequest);
    long count(String tenantId, SearchRequest searchRequest);
    void dropIndexes(String tenantId);
    void close();
}
