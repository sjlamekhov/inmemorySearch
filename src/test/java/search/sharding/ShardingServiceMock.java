package search.sharding;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import objects.DocumentUri;
import search.request.SearchRequest;
import sharding.ShardingService;

import java.util.*;

public class ShardingServiceMock<U extends AbstractObjectUri, T extends AbstractObject> extends ShardingService<U, T>  {

    private final Map<SearchRequest, Set<DocumentUri>> searchRequestAndDocuments;

    public ShardingServiceMock() {
        super(Collections.emptyList(), null);
        this.searchRequestAndDocuments = new HashMap<>();
    }

    public void addSearchRequestAndResult(SearchRequest searchRequest, Document document) {
        if (null == document) {
            return;
        }
        searchRequestAndDocuments
                .computeIfAbsent(searchRequest, i -> new HashSet<>())
                .add(document.getUri());
    }

    @Override
    public Map<SearchRequest, Collection<U>> executeShardedRequest(SearchRequest searchRequest) {
        return Collections.emptyMap();
    }

    @Override
    public Map<SearchRequest, Collection<U>> executeShardedRequests(Collection<SearchRequest> searchRequest) {
        return Collections.emptyMap();
    }

    @Override
    public void removeObjectFromIndex(T object) {
    }

    public void dropIndexes() {
        searchRequestAndDocuments.clear();
    }

}
