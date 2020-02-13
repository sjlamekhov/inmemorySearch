package search.changesCollecting;

import networking.Message;
import objects.Document;
import objects.DocumentUri;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import search.AbstractSearchServiceTest;
import search.SearchService;
import search.inmemory.InMemorySearchService;
import search.withChangesCollecting.ChangesCollectingSearchService;

import java.util.*;

import static utils.QueueUtils.extractFromQueue;

public class ChangesCollectingSearchServiceTest extends AbstractSearchServiceTest {

    private static LinkedList<Message> changes = new LinkedList<>();

    @Override
    protected SearchService<DocumentUri, Document> getSearchService() {
        return new ChangesCollectingSearchService<>(new InMemorySearchService<>(), changes);
    }

    @Test
    public void createdChange() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document document = new Document(documentUri, attributes);

        searchService.addObjectToIndex(document);
        List<Message> extractedMessages = extractFromQueue(changes);
        Assert.assertEquals(1, extractedMessages.size());
        Assert.assertEquals(Message.MessageType.CREATE, extractedMessages.iterator().next().getMessageType());
        Assert.assertEquals(document, extractedMessages.iterator().next().getAbstractObject());

        searchService.removeObjectFromIndex(document);
        extractedMessages = extractFromQueue(changes);
        Assert.assertEquals(1, extractedMessages.size());
        Assert.assertEquals(Message.MessageType.DELETE, extractedMessages.iterator().next().getMessageType());
        Assert.assertEquals(document, extractedMessages.iterator().next().getAbstractObject());
    }

    @After
    public void after() {
        super.after();
        changes.clear();
    }

}
