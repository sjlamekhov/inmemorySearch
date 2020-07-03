package search.sharding;

import objects.Document;
import objects.DocumentUri;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import search.AbstractSearchServiceTest;
import search.ConditionType;
import search.SearchService;
import search.inmemory.InMemorySearchService;
import search.request.SearchRequest;
import search.sharded.ShardedSearchService;

import java.util.*;

public class ShardingSearchServiceTest extends AbstractSearchServiceTest {

    private ShardingServiceMock<DocumentUri, Document> shardingServiceMock;
    private InMemorySearchService<DocumentUri, Document> localSearchService;

    @Override
    protected SearchService<DocumentUri, Document> getSearchService() {
        localSearchService = new InMemorySearchService<>();
        shardingServiceMock = new ShardingServiceMock<>();
        return new ShardedSearchService<>(localSearchService, shardingServiceMock);
    }

    @Test
    public void shardingAndLocalTest() {
        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("value")
                .build();

        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document localDocument = new Document(new DocumentUri("localDocumentUri", tenantId), attributes);
        Document forShardingDocument = new Document(new DocumentUri("forShardingDocumentUri", tenantId), attributes);
        Assert.assertNotEquals(localDocument.getUri(), forShardingDocument.getUri());

        localSearchService.addObjectToIndex(tenantId, localDocument);
        shardingServiceMock.addSearchRequestAndResult(searchRequest, forShardingDocument);

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(localDocument.getUri(), forShardingDocument.getUri())));
    }

    @Test
    public void shardingOnlyTest() {
        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("value")
                .build();

        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document forShardingDocument = new Document(new DocumentUri(tenantId), attributes);

        shardingServiceMock.addSearchRequestAndResult(searchRequest, forShardingDocument);

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Collections.singletonList(forShardingDocument.getUri())));
    }

    @After
    @Override
    public void after() {
        super.after();
        shardingServiceMock.dropIndexes();
    }

}
