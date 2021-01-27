package server.controllers;

import dump.DumpContext;
import dump.consumers.AbstractObjectConsumer;
import dump.consumers.InMemoryConsumer;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import search.SearchService;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {server.Application.class})
public class DumpControllersTest {

    @Autowired
    private DumpController dumpController;

    @Autowired
    private SearchService<DocumentUri, Document> searchService;

    private static final String tenantId = "testTenant";

    @Test
    public void startDumpingTest() throws InterruptedException {
        List<Document> documentsToIndexAndDump = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute", "value" + i);
            Document document = new Document(new DocumentUri(tenantId), attributes);
            documentsToIndexAndDump.add(document);
            searchService.addObjectToIndex(tenantId, document);
        }
        final DumpContext dumpContext = dumpController.startDumping(tenantId,
                -1,
                "inmemoryconsumer",
                null, null);

        TestUtils.waitFor(() -> dumpController.getContextById(dumpContext.getDumpProcessId()).isFinished());
        DumpContext finishedDumpContext = dumpController.getContextById(dumpContext.getDumpProcessId());
        Assert.assertTrue(finishedDumpContext.isFinished());
        AbstractObjectConsumer consumer =  finishedDumpContext.getObjectConsumer();
        Assert.assertTrue(consumer instanceof InMemoryConsumer);
        InMemoryConsumer<Document> inMemoryConsumer = (InMemoryConsumer) consumer;
        Assert.assertEquals(32, inMemoryConsumer.getBuffer().size());
        Assert.assertEquals(
                documentsToIndexAndDump.stream().map(Document::getUri).collect(Collectors.toSet()),
                inMemoryConsumer.getBuffer().stream().map(Document::getUri).collect(Collectors.toSet()));
    }



}