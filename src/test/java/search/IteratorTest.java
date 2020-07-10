package search;

import dao.DocumentUriIterator;
import dao.UriGenerator;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import search.inmemory.InMemorySearchService;

import java.util.*;
import java.util.stream.Collectors;

public class IteratorTest {

    private final static String testTenantId = "testTenantId";

    @Test
    public void iteratorTest() {
        int uriLength = 4;
        int documentCount = 32;
        SearchService<DocumentUri, Document> searchService = new InMemorySearchService<>(
                new UriGenerator(uriLength)
        );
        Set<DocumentUri> documentUris = new HashSet<>();
        for (int i = 0; i < documentCount; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute" + i, "value" + i);
            Document document = new Document(new DocumentUri(testTenantId), attributes);
            documentUris.add(searchService.addObjectToIndex(testTenantId, document));
        }
        DocumentUriIterator documentUriIterator = (DocumentUriIterator) searchService.getIterator(testTenantId, null);
        Assert.assertNotNull(documentUriIterator.getCursorId());
        List<Document> foundDocuments = new ArrayList<>();
        while (documentUriIterator.hasNext()) {
            DocumentUri documentUri = documentUriIterator.next();
            Document document = searchService.getObjectByUri(documentUri);
            if (null != document) {
                foundDocuments.add(document);
            }
        }
        Assert.assertEquals(documentCount, foundDocuments.size());
        Assert.assertEquals(documentUris, foundDocuments.stream().map(Document::getUri).collect(Collectors.toSet()));
    }

}
