package sdk.search.cached;

import sdk.objects.AbstractObject;
import sdk.objects.AbstractObjectUri;
import sdk.search.SearchRequest;
import sdk.search.SearchService;
import sdk.search.cache.SearchCache;

import java.util.Collection;

public class CachedSearchService<U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final SearchService<U, T> searchService;
    private final SearchCache<U, T> searchCache;

    public CachedSearchService(SearchService<U, T> searchService) {
        this.searchService = searchService;
        this.searchCache = new SearchCache<>(sr -> searchService.search(null, sr));
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
    public Collection<U> typeAheadSearch(String tenantId, String field, String prefix) {
        return searchService.typeAheadSearch(tenantId, field, prefix);
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
