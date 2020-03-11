package sharding;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;
import sharding.client.SearchClient;

import java.util.*;

public class ShardingService<U extends AbstractObjectUri, T extends AbstractObject> {

    private final SearchClient searchClient;

    public ShardingService(SearchClient searchClient) {
        this.searchClient = searchClient;
    }

    public Map<SearchRequest, Collection<U>> executeShardedRequest(String tenantId, SearchRequest searchRequest) {
        return executeShardedRequests(tenantId, Collections.singleton(searchRequest));
    }

    //TODO: add multithread mode
    public Map<SearchRequest, Collection<U>> executeShardedRequests(String tenantId, Collection<SearchRequest> searchRequests) {
        Map<SearchRequest, Collection<U>> result = new HashMap<>();
        for (SearchRequest searchRequest : searchRequests) {
            result.put(searchRequest, searchClient.executeSearchRequest(tenantId, searchRequest));
        }
        return result;
    }

}
