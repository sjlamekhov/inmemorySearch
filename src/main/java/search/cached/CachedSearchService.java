package search.cached;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.optimizer.SearchRequestOptimizer;
import search.request.SearchRequest;
import search.SearchService;

import java.util.Collection;

public class CachedSearchService<U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final SearchService<U, T> searchService;
    private final SearchCache<U, T> searchCache;

    public CachedSearchService(SearchService<U, T> searchService) {
        this.searchService = searchService;
        this.searchCache = new SearchCache<>(sr -> searchService.search(null, sr));
    }

    public CachedSearchService(SearchService<U, T> searchService, SearchRequestOptimizer searchRequestOptimizer) {
        this.searchService = searchService;
        this.searchCache = new SearchCache<>(sr -> searchService.search(null, sr), searchRequestOptimizer);
    }

    @Override
    public T getObjectByUri(U uri) {
        return searchService.getObjectByUri(uri);
    }

    @Override
    public void addObjectToIndex(T object) {
        searchCache.addToIndex(object);
        searchService.addObjectToIndex(object);
    }

    @Override
    public void removeObjectFromIndex(T object) {
        searchCache.removeObjectFromIndex(object);
        searchService.removeObjectFromIndex(object);
    }

    @Override
    public Collection<U> search(String tenantId, SearchRequest searchRequest) {
        return searchCache.getCached(searchRequest);
    }

    @Override
    public long count(String tenantId, SearchRequest searchRequest) {
        return searchCache.getCached(searchRequest).size();
    }

    @Override
    public void dropIndexes(String tenantId) {
        searchCache.dropCache();
        searchService.dropIndexes(tenantId);
    }

    @Override
    public void close() {
        searchService.close();
    }

    public SearchCache<U, T> getSearchCache() {
        return searchCache;
    }
}
