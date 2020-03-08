package search.sharding;

import objects.Document;
import objects.DocumentUri;
import org.junit.After;
import search.AbstractSearchServiceTest;
import search.SearchService;
import search.inmemory.InMemorySearchService;
import search.sharded.ShardedSearchService;

public class ShardingSearchServiceTest extends AbstractSearchServiceTest {

    private ShardingServiceMock<DocumentUri, Document> shardingServiceMock;

    @Override
    protected SearchService<DocumentUri, Document> getSearchService() {
        shardingServiceMock = new ShardingServiceMock<>();
        return new ShardedSearchService<>(new InMemorySearchService<>(),
                shardingServiceMock);
    }

    @After
    @Override
    public void after() {
        super.after();
        shardingServiceMock.dropIndexes();
    }

}
