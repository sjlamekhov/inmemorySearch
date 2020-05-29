package search;


import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SearchService<U extends AbstractObjectUri, T extends AbstractObject> {

    T getObjectByUri(U uri);
    void addObjectToIndex(T object);
    void removeObjectFromIndex(T object);
    Map<List<String>, Collection<U>> searchNearestDocuments(T object);
    Collection<U> search(String tenantId, SearchRequest searchRequest);
    long count(String tenantId, SearchRequest searchRequest);
    void dropIndexes(String tenantId);
    void close();
}
