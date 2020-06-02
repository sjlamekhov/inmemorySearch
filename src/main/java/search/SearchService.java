package search;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface SearchService<U extends AbstractObjectUri, T extends AbstractObject> {

    T getObjectByUri(U uri);
    void addObjectToIndex(T object);
    void removeObjectFromIndex(T object);
    Map<Set<String>, Collection<U>> searchNearestDocuments(T object);
    Collection<U> search(String tenantId, SearchRequest searchRequest);
    long count(String tenantId, SearchRequest searchRequest);
    void dropIndexes(String tenantId);
    void close();
}
