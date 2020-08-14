package search;

import dao.ExtractObjectsResult;
import dao.UriGenerator;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import search.inmemory.InMemorySearchService;

import java.util.*;
import java.util.stream.Collectors;

public class ExtractObjectsByIteratorTest {

    private final static String testTenantId = "testTenantId";

    @Test
    public void iteratorTest() {
        int uriLength = 4;
        int documentCount = 32;
        InMemorySearchService<DocumentUri, Document> searchService = new InMemorySearchService<>(
                new UriGenerator(uriLength)
        );
        Set<DocumentUri> documentUris = new HashSet<>();
        for (int i = 0; i < documentCount; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute" + i, "value" + i);
            Document document = new Document(new DocumentUri(testTenantId), attributes);
            documentUris.add(searchService.addObjectToIndex(testTenantId, document));
        }
        ExtractObjectsResult<Document> extractResult = searchService.extractObjectsByIterator(testTenantId, null, 8);
        Set<DocumentUri> extractedUris = extractResult.getObjects().stream().map(Document::getUri).distinct().collect(Collectors.toSet());
        while (extractResult.isHasNext()) {
            extractResult = searchService.extractObjectsByIterator(testTenantId, extractResult.getCursorId(), 8);
            extractedUris.addAll(extractResult.getObjects().stream().map(Document::getUri).collect(Collectors.toSet()));
        }
        Assert.assertEquals(documentCount, extractedUris.size());
        Assert.assertEquals(documentUris, extractedUris);
    }

}
