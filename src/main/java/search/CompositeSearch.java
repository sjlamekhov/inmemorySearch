package search;

import dump.consumers.AbstractObjectConsumer;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.*;

public class CompositeSearch<U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final Map<String, SearchService<U, T>> searchMapping;

    public CompositeSearch() {
        this.searchMapping = new HashMap<>();
    }

    public void addService(String tenantId, SearchService<U, T> searchService) {
        if (searchMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("SearchService for %s tenant is already registered", tenantId));
        }
        searchMapping.put(tenantId, searchService);
    }

    @Override
    public T getObjectByUri(U uri) {
        Objects.requireNonNull(uri);
        String tenantId = uri.getTenantId();
        return getSearchServiceInternal(tenantId).getObjectByUri(uri);
    }

    @Override
    public U addObjectToIndex(String tenantId, T object) {
        return getSearchServiceInternal(tenantId).addObjectToIndex(tenantId, object);
    }

    @Override
    public void removeObjectFromIndex(T object) {
        Objects.requireNonNull(object.getUri());
        String tenantId = object.getUri().getTenantId();
        getSearchServiceInternal(tenantId).removeObjectFromIndex(object);
    }

    @Override
    public Map<Set<String>, Collection<U>> searchNearestDocuments(T object) {
        Objects.requireNonNull(object.getUri());
        String tenantId = object.getUri().getTenantId();
        return getSearchServiceInternal(tenantId).searchNearestDocuments(object);
    }

    @Override
    public Collection<U> search(String tenantId, SearchRequest searchRequest) {
        return getSearchServiceInternal(tenantId).search(tenantId, searchRequest);
    }

    @Override
    public long count(String tenantId, SearchRequest searchRequest) {
        return getSearchServiceInternal(tenantId).count(tenantId, searchRequest);
    }

    @Override
    public void extractObjectsByIterator(String tenantId, SearchRequest searchRequest, String cursorId, int maxSize, AbstractObjectConsumer consumer) {
        getSearchServiceInternal(tenantId).extractObjectsByIterator(tenantId, searchRequest, cursorId, maxSize, consumer);
    }

    @Override
    public void dropIndexes(String tenantId) {
        getSearchServiceInternal(tenantId).dropIndexes(tenantId);
    }

    @Override
    public void close() {
        searchMapping.values().forEach(SearchService::close);
    }

    private SearchService<U, T> getSearchServiceInternal(String tenantId) {
        if (!searchMapping.containsKey(tenantId)) {
            throw new RuntimeException(String.format("SearchService for tenant %s is not registered", tenantId));
        }
        SearchService<U, T> searchService = searchMapping.get(tenantId);
        return searchService;
    }
}
