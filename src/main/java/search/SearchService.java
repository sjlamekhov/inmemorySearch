package search;

import dao.ExtractObjectsResult;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.*;

public interface SearchService<U extends AbstractObjectUri, T extends AbstractObject> {

    T getObjectByUri(U uri);
    U addObjectToIndex(String tenantId, T object);
    void removeObjectFromIndex(T object);
    Map<Set<String>, Collection<U>> searchNearestDocuments(T object);
    Collection<U> search(String tenantId, SearchRequest searchRequest);
    long count(String tenantId, SearchRequest searchRequest);
    default ExtractObjectsResult<T> extractObjectsByIterator(String tenantId, String cursorId, int maxSize) {
        return new ExtractObjectsResult<>("", false, Collections.emptyList());
    }
    void dropIndexes(String tenantId);
    void close();
}
