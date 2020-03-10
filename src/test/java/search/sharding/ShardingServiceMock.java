package search.sharding;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import objects.DocumentUri;
import search.request.SearchRequest;
import sharding.ShardingService;

import java.util.*;

public class ShardingServiceMock<U extends AbstractObjectUri, T extends AbstractObject> extends ShardingService<U, T>  {

    private final Map<SearchRequest, Set<U>> searchRequestAndDocuments;

    public ShardingServiceMock() {
        super(Collections.emptyList(), null);
        this.searchRequestAndDocuments = new HashMap<>();
    }

    public void addSearchRequestAndResult(SearchRequest searchRequest, T object) {
        if (null == object) {
            return;
        }
        searchRequestAndDocuments
                .computeIfAbsent(searchRequest, i -> new HashSet<>())
                .add((U) object.getUri());
    }

    @Override
    public Map<SearchRequest, Collection<U>> executeShardedRequest(SearchRequest searchRequest) {
        return executeShardedRequests(Collections.singleton(searchRequest));
    }

    @Override
    public Map<SearchRequest, Collection<U>> executeShardedRequests(Collection<SearchRequest> searchRequests) {
        Map<SearchRequest, Collection<U>> result = new HashMap<>();
        for (SearchRequest searchRequest : searchRequests) {
            result.put(searchRequest, searchRequestAndDocuments.getOrDefault(searchRequest, Collections.emptySet()));
        }
        return result;
    }

    @Override
    public void removeObjectFromIndex(T object) {
        searchRequestAndDocuments.values().forEach(i -> i.remove(object.getUri()));
    }

    public void dropIndexes() {
        searchRequestAndDocuments.clear();
    }

}
