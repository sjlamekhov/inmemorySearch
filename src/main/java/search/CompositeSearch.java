package search;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public void addObjectToIndex(T object) {
        Objects.requireNonNull(object.getUri());
        String tenantId = object.getUri().getTenantId();
        getSearchServiceInternal(tenantId).addObjectToIndex(object);
    }

    @Override
    public void removeObjectFromIndex(T object) {
        Objects.requireNonNull(object.getUri());
        String tenantId = object.getUri().getTenantId();
        getSearchServiceInternal(tenantId).removeObjectFromIndex(object);
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
