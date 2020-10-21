package platform.dump;

import dao.UriGenerator;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import search.inmemory.InMemorySearchService;
import utils.TestUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DumpServiceTest {

    private final static String testTenantId = "testTenantId";
    private final static int uriLength = 4;
    private final static int documentCount = 32;

    @Test
    public void test() throws InterruptedException {
        InMemorySearchService<DocumentUri, Document> searchService = new InMemorySearchService<>(
                new UriGenerator(uriLength)
        );
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(24);
        executor.initialize();
        DumpService<DocumentUri, Document> dumpService = new DumpService<>(executor, searchService);

        //prepare data
        Set<DocumentUri> documentUris = new HashSet<>();
        for (int i = 0; i < documentCount; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute" + i, "value" + i);
            Document document = new Document(new DocumentUri(testTenantId), attributes);
            documentUris.add(searchService.addObjectToIndex(testTenantId, document));
        }
        // dump of entities
        List<Document> accumulator = new ArrayList<>();
        Consumer<Document> consumer = accumulator::add;
        DumpContext dumpContext = dumpService.addAndStartNewTask(testTenantId, -1, consumer);
        Assert.assertNotNull(dumpContext);
        String dumpProcessId = dumpContext.getDumpProcessId();

        TestUtils.waitFor(() -> {
            return dumpService.getContextByDumpProcessId(dumpProcessId).isFinished();
        }, 1024);
        Assert.assertEquals(documentCount, accumulator.size());
        Assert.assertTrue(accumulator.stream()
                .map(Document::getUri).collect(Collectors.toSet())
                .containsAll(documentUris));

    }

}