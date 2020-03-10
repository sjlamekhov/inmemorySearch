package sharding;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ShardingService<U extends AbstractObjectUri, T extends AbstractObject> {

    private final List<String> clusterNodes;
    private final SearchClient searchClient;

    public ShardingService(List<String> clusterNodes, SearchClient searchClient) {
        this.clusterNodes = clusterNodes;
        this.searchClient = searchClient;
    }

    public abstract Map<SearchRequest, Collection<U>> executeShardedRequest(SearchRequest searchRequest);

    public abstract Map<SearchRequest, Collection<U>> executeShardedRequests(Collection<SearchRequest> searchRequest);

    public abstract void removeObjectFromIndex(T object);
}