package search.sharded;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.SearchService;
import search.request.SearchRequest;
import sharding.ShardingService;

import java.util.Collection;

public class ShardedSearchService <U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final SearchService<U, T> searchService;
    private final ShardingService<U> shardingService;

    public ShardedSearchService(SearchService<U, T> searchService, ShardingService<U> shardingService) {
        this.searchService = searchService;
        this.shardingService = shardingService;
    }

    @Override
    public void addObjectToIndex(T object) {
        searchService.addObjectToIndex(object);
    }

    @Override
    public void removeObjectFromIndex(T object) {
        searchService.removeObjectFromIndex(object);
    }

    //TODO: decide what to do with it
    @Override
    public Collection<U> typeAheadSearch(String tenantId, String field, String prefix) {
        return searchService.typeAheadSearch(tenantId, field, prefix);
    }

    //TODO: design
    @Override
    public Collection<U> search(String tenantId, SearchRequest searchRequest) {
        //collect responses for every sub request locally
        //collect responses for every sub request from sharding nodes
        //combine and return
        return null;
    }

    //TODO: decide what to do with it
    @Override
    public long count(String tenantId, SearchRequest searchRequest) {
        return 0;
    }

    @Override
    public void dropIndexes(String tenantId) {
        searchService.dropIndexes(tenantId);
    }

    @Override
    public void close() {
        searchService.close();
    }
}
