package search.sharded;

import dump.consumers.AbstractObjectConsumer;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.SearchService;
import search.optimizer.SearchRequestOptimizer;
import search.optimizer.TransparentRequestOptimizer;
import search.request.SearchRequest;
import sharding.ShardingService;

import java.util.*;

import static search.SearchServiceUtils.collectAllSearchRequests;
import static search.SearchServiceUtils.combinedResult;

public class ShardedSearchService <U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final SearchService<U, T> searchService;
    private final ShardingService<U, T> shardingService;
    private final SearchRequestOptimizer searchRequestOptimizer;

    public ShardedSearchService(SearchService<U, T> searchService, ShardingService<U, T> shardingService) {
        this.searchService = searchService;
        this.shardingService = shardingService;
        this.searchRequestOptimizer = new TransparentRequestOptimizer();
    }

    @Override
    public T getObjectByUri(U uri) {
        T localResult = searchService.getObjectByUri(uri);
        if (null != localResult) {
            return localResult;
        }
        return shardingService.getObjectSharded(uri);
    }

    @Override
    public U addObjectToIndex(String tenantId, T object) {
        return searchService.addObjectToIndex(tenantId, object);
    }

    @Override
    public void removeObjectFromIndex(T object) {
        searchService.removeObjectFromIndex(object);
        shardingService.removeObjectFromIndex(object);
    }

    @Override
    public Map<Set<String>, Collection<U>> searchNearestDocuments(T object) {

        return searchService.searchNearestDocuments(object);
    }

    @Override
    public Collection<U> search(String tenantId, SearchRequest searchRequest) {
        if (null == searchRequestOptimizer.optimize(searchRequest)) {
            return Collections.emptySet();
        }
        Collection<SearchRequest> searchRequestsToFetch = collectAllSearchRequests(searchRequest);
        Map<SearchRequest, Collection<U>> fromSharding = shardingService.executeShardedRequests(tenantId, searchRequestsToFetch);
        Map<SearchRequest, Collection<U>> fromCurrentMachine = new HashMap<>();
        for (SearchRequest searchRequestPart : searchRequestsToFetch) {
            fromCurrentMachine.put(searchRequestPart, searchService.search(tenantId, searchRequestPart));
        }
        return combinedResult(searchRequest, fromSharding, fromCurrentMachine);
    }

    @Override
    public long count(String tenantId, SearchRequest searchRequest) {
        return search(tenantId, searchRequest).size();
    }

    @Override
    public void extractObjectsByIterator(String tenantId, SearchRequest searchRequest, String cursorId, int maxSize, AbstractObjectConsumer consumer) {
        searchService.extractObjectsByIterator(tenantId, searchRequest, cursorId, maxSize, consumer);
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