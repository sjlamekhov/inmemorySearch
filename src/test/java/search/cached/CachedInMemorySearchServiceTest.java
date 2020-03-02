package search.cached;

import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import search.ConditionType;
import search.request.SearchRequest;
import search.SearchService;
import search.AbstractSearchServiceTest;
import search.inmemory.InMemorySearchService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CachedInMemorySearchServiceTest extends AbstractSearchServiceTest {

    private SearchCache<DocumentUri, Document> searchCache;

    @Override
    protected SearchService<DocumentUri, Document> getSearchService() {
        CachedSearchService<DocumentUri, Document> cachedSearchService = new CachedSearchService<>(new InMemorySearchService<>());
        searchCache = cachedSearchService.getSearchCache();
        return cachedSearchService;
    }

    //cache related tests
    @Test
    public void testCacheSize() {
        searchCache.dropCache();
        Assert.assertTrue(searchCache.getCachedRequests().isEmpty());

        DocumentUri documentUri1 = new DocumentUri(tenantId);
        Map<String, String> attributes1 = new HashMap<>();
        attributes1.put("attribute", "value");
        Document document1 = new Document(documentUri1, attributes1);
        searchService.addObjectToIndex(document1);

        Assert.assertTrue(searchCache.getCachedRequests().isEmpty());

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value")
                .setConditionType(ConditionType.EQ)
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertTrue(!searchResult.isEmpty());
        Assert.assertFalse(searchCache.getCachedRequests().isEmpty());
        Assert.assertFalse(searchCache.getCachedRequests().isEmpty());
    }

    @Test
    public void testNotAppliableRequest() {
        SearchRequest notAppliableSearchRequest = SearchRequest.Builder.newInstance()
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("VALUE1")
                .setAttributeToSearch("attribute")
                .and(SearchRequest.Builder.newInstance()
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("VALUE2")
                        .setAttributeToSearch("attribute")
                        .build())
                .build();
        searchCache.dropCache();
        Assert.assertTrue(searchCache.getCachedRequests().isEmpty());
        Assert.assertTrue(searchService.search(tenantId, notAppliableSearchRequest).isEmpty());
        //cache is still empty, not appliable request don't go to cache
        Assert.assertTrue(searchCache.getCachedRequests().isEmpty());
    }

    @Test
    public void testExpiration() {
        searchCache.dropCache();
        Assert.assertTrue(searchCache.getCachedRequests().isEmpty());
        for (int i = 0; i < searchCache.MAX_CACHE_SIZE; i++) {
            searchService.search(tenantId, SearchRequest.Builder.newInstance()
                    .setConditionType(ConditionType.EQ)
                    .setValueToSearch("value" + i)
                    .setAttributeToSearch("attribute")
                    .build());
        }
        //maximum size of cache is reached
        Set<SearchRequest> searchRequestBeforeRebuild = searchCache.getCachedRequests();
        Assert.assertEquals(searchCache.MAX_CACHE_SIZE, searchRequestBeforeRebuild.size());
        //one more request should trigger cache rebuild
        searchService.search(tenantId, SearchRequest.Builder.newInstance()
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("VALUE")
                .setAttributeToSearch("attribute")
                .build());
        Set<SearchRequest> searchRequestAfterRebuild = searchCache.getCachedRequests();
        Assert.assertTrue(searchCache.MAX_CACHE_SIZE >= searchRequestAfterRebuild.size());
        Assert.assertFalse(searchRequestAfterRebuild.equals(searchRequestBeforeRebuild));
    }

}
