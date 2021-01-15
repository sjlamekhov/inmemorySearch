package search;

import dao.ExtractObjectsResult;
import dao.UriGenerator;
import dump.consumers.AbstractObjectConsumer;
import dump.consumers.InMemoryConsumer;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import search.cached.ApplianceChecker;
import search.inmemory.InMemorySearchService;
import search.request.SearchRequest;

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

    @Test
    public void iteratorWithQueryTest() {
        int uriLength = 4;
        int documentCount = 32;
        ApplianceChecker applianceChecker = new ApplianceChecker();
        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute1")
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("value1")
                .build();
        InMemorySearchService<DocumentUri, Document> searchService = new InMemorySearchService<>(
                new UriGenerator(uriLength)
        );
        Set<DocumentUri> documentUris = new HashSet<>();
        for (int i = 0; i < documentCount; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute" + (i % 2), "value" + (i % 2));
            Document document = new Document(new DocumentUri(testTenantId), attributes);
            DocumentUri fromResponse = searchService.addObjectToIndex(testTenantId, document);
            if (applianceChecker.test(searchRequest, document)) {
                documentUris.add(fromResponse);
            }
        }

        List<Document> extractedUris = new ArrayList<>();
        InMemoryConsumer<Document> inMemoryConsumer = new InMemoryConsumer<>(extractedUris);
        searchService.extractObjectsByIterator(
                testTenantId, searchRequest,
                null, documentCount,
                inMemoryConsumer);

        Assert.assertEquals(documentUris.size(), extractedUris.size());
        Assert.assertEquals(documentUris, extractedUris.stream().map(Document::getUri).collect(Collectors.toSet()));
    }

}
