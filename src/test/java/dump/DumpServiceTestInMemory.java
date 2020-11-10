package dump;

import dao.UriGenerator;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import dump.consumers.InMemoryConsumer;
import search.inmemory.InMemorySearchService;
import utils.TestUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DumpServiceTestInMemory {

    private final static String testTenantId = "testTenantId";
    private final static int uriLength = 4;
    private final static int documentCount = 32;

    private InMemorySearchService<DocumentUri, Document> searchService;
    private ThreadPoolTaskExecutor executor;
    private DumpService<DocumentUri, Document> dumpService;

    @Before
    public void beforeTest() {
        searchService = new InMemorySearchService<>(
                new UriGenerator(uriLength)
        );

        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(24);
        executor.initialize();

        dumpService = new DumpService<>(executor, searchService);
    }

    @Test
    public void testLimitedNumber() throws InterruptedException {
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
        DumpContext dumpContext = dumpService.addAndStartNewTask(testTenantId, documentCount / 2, new InMemoryConsumer<>(accumulator));
        Assert.assertNotNull(dumpContext);
        String dumpProcessId = dumpContext.getDumpProcessId();

        TestUtils.waitFor(() -> dumpService.getContextByDumpProcessId(dumpProcessId).isFinished(), 1024);
        Assert.assertEquals(documentCount / 2, accumulator.size());
        Assert.assertTrue(documentUris.containsAll(accumulator.stream()
                .map(Document::getUri).collect(Collectors.toSet())));
    }

    @Test
    public void testUnLimitedNumber() throws InterruptedException {
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
        DumpContext dumpContext = dumpService.addAndStartNewTask(testTenantId, -1, new InMemoryConsumer<>(accumulator));
        Assert.assertNotNull(dumpContext);
        String dumpProcessId = dumpContext.getDumpProcessId();

        TestUtils.waitFor(() -> dumpService.getContextByDumpProcessId(dumpProcessId).isFinished(), 1024);
        Assert.assertEquals(documentCount, accumulator.size());
        Assert.assertTrue(accumulator.stream()
                .map(Document::getUri).collect(Collectors.toSet())
                .containsAll(documentUris));
    }

}