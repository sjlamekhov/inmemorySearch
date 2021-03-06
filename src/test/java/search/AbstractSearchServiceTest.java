package search;

import objects.Document;
import objects.DocumentUri;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import search.request.SearchRequest;

import java.util.*;

public abstract class AbstractSearchServiceTest {

    protected final String tenantId = "testTenantId";
    protected SearchService<DocumentUri, Document> searchService;

    protected abstract SearchService<DocumentUri, Document> getSearchService();

    public AbstractSearchServiceTest() {
        this.searchService = getSearchService();
    }

    @Before
    public void before() {}

    @After
    public void after() {
        searchService.dropIndexes(tenantId);
    }

    @Test
    public void getByUriTest() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute1", "attributeValue1");
        attributes.put("attribute2", "attributeValue2");
        Document document = new Document(new DocumentUri(tenantId), attributes);

        DocumentUri documentUri = searchService.addObjectToIndex(tenantId, document);
        Document fetched = searchService.getObjectByUri(documentUri);
        Assert.assertNotNull(fetched);
        Assert.assertEquals(documentUri, fetched.getUri());
        Assert.assertEquals(attributes, fetched.getAttributes());

        searchService.removeObjectFromIndex(document);
        fetched = searchService.getObjectByUri(documentUri);
        Assert.assertNull(fetched);
    }

    @Test
    public void allRequest() {
        final int numberOfObjects = 16;
        Set<DocumentUri> uris = new HashSet<>();
        for (int i = 0; i < numberOfObjects; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute", "value" + i);
            Document document = new Document(new DocumentUri(tenantId), attributes);
            DocumentUri documentUri = searchService.addObjectToIndex(tenantId, document);
            uris.add(documentUri);
        }

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setConditionType(ConditionType.ALL)
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(numberOfObjects, searchResult.size());
        Assert.assertEquals(uris, searchResult);
        Assert.assertEquals(numberOfObjects, searchService.count(tenantId, searchRequest));
    }

    @Test
    public void orEqRequest() {
        Map<String, String> attributes1 = new HashMap<>();
        attributes1.put("attribute", "value1");
        Document document1 = new Document(new DocumentUri(tenantId), attributes1);
        DocumentUri documentUri1 = searchService.addObjectToIndex(tenantId, document1);

        Map<String, String> attributes2 = new HashMap<>();
        attributes2.put("attribute", "value2");
        Document document2 = new Document(new DocumentUri(tenantId), attributes2);
        DocumentUri documentUri2 = searchService.addObjectToIndex(tenantId, document2);

        Map<String, String> attributes3 = new HashMap<>();
        attributes3.put("attribute", "value3");
        Document document3 = new Document(new DocumentUri(tenantId), attributes3);
        DocumentUri documentUri3 = searchService.addObjectToIndex(tenantId, document3);

        final SearchRequest searchRequestInner = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value1")
                .setConditionType(ConditionType.EQ)
                .build();
        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value2")
                .setConditionType(ConditionType.EQ)
                .or(searchRequestInner)
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUri1, documentUri2)));
        Assert.assertEquals(2, searchService.count(tenantId, searchRequest));
    }

    @Test
    public void andLtGtRequest() {
        DocumentUri documentUri1 = new DocumentUri(tenantId);
        Map<String, String> attributes1 = new HashMap<>();
        attributes1.put("attribute", "value1");
        Document document1 = new Document(documentUri1, attributes1);
        searchService.addObjectToIndex(tenantId, document1);

        DocumentUri documentUri2 = new DocumentUri(tenantId);
        Map<String, String> attributes2 = new HashMap<>();
        attributes2.put("attribute", "value2");
        Document document2 = new Document(documentUri2, attributes2);
        searchService.addObjectToIndex(tenantId, document2);

        DocumentUri documentUri3 = new DocumentUri(tenantId);
        Map<String, String> attributes3 = new HashMap<>();
        attributes3.put("attribute", "value3");
        Document document3 = new Document(documentUri3, attributes3);
        searchService.addObjectToIndex(tenantId, document3);

        final SearchRequest searchRequestInner = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value1")
                .setConditionType(ConditionType.EQ)
                .build();
        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value3")
                .setConditionType(ConditionType.EQ)
                .and(searchRequestInner)
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(0, searchResult.size());
        Assert.assertEquals(0, searchService.count(tenantId, searchRequest));
    }

    @Test
    public void stwithSearch() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document document = new Document(new DocumentUri(tenantId), attributes);
        DocumentUri documentUri = searchService.addObjectToIndex(tenantId, document);

        Collection<DocumentUri> searchResult = searchService.search(tenantId, SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.STWITH)
                .setValueToSearch("value")
                .build());
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUri));

        searchResult = searchService.search(tenantId, SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.STWITH)
                .setValueToSearch("valueNotExisting")
                .build());
        Assert.assertEquals(0, searchResult.size());
    }

    @Test
    public void eqAndRemoveFromIndex() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueToCompare");
        attributes.put("attributeOther", "otherValue");
        Document document = new Document(documentUri, attributes);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "valueToCompare");
        attributesForDeletion.put("attributeOther", "veryOtherValue");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);

        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("valueToCompare")
                .build();
        testWithRemove(document, documentForDeletion, searchRequest);
    }

    @Test
    public void neqAndRemoveFromIndex() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueToCompare1");
        Document document = new Document(documentUri, attributes);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "valueToCompare2");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);

        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.NE)
                .setValueToSearch("valueToCompare")
                .build();
        testWithRemove(document, documentForDeletion, searchRequest);
    }

    @Test
    public void gtAndRemoveFromIndex() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueToCompare1");
        Document document = new Document(documentUri, attributes);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "valueToCompare2");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);

        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.GT)
                .setValueToSearch("valueToCompare")
                .build();
        testWithRemove(document, documentForDeletion, searchRequest);
    }

    @Test
    public void ltAndRemoveFromIndex() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueToCompare1");
        Document document = new Document(documentUri, attributes);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "valueToCompare2");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);

        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.LT)
                .setValueToSearch("valueToCompare3")
                .build();
        testWithRemove(document, documentForDeletion, searchRequest);
    }

    @Test
    public void lengthIncompatibleRequest() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueWithSomeLengthButWhoCares");
        Document document = new Document(documentUri, attributes);
        searchService.addObjectToIndex(tenantId, document);

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.LENGTH)
                .setValueToSearch("17")
                .and(SearchRequest.Builder.newInstance()
                                .setAttributeToSearch("attribute")
                                .setConditionType(ConditionType.LENGTH)
                                .setValueToSearch("18")
                                .build())
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertTrue(searchResult.isEmpty());
    }

    @Test
    public void stwithAndRemoveFromIndex() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueLong");
        Document document = new Document(documentUri, attributes);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "value");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);

        SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.STWITH)
                .setValueToSearch("value")
                .build();
        testWithRemove(document, documentForDeletion, searchRequest);
    }

    @Test
    public void lengthAndRemoveRequest() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueWithLength17");
        Document document = new Document(documentUri, attributes);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "vvlueWithLength17");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.LENGTH)
                .setValueToSearch("17")
                .build();

        testWithRemove(document, documentForDeletion, searchRequest);
    }

    @Test
    public void containsAndRemoveRequest() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "someAttributeValue1");
        Document document = new Document(documentUri, attributes);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "someAttributeValue2");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.CONTAINS)
                .setValueToSearch("Value")
                .build();

        testWithRemove(document, documentForDeletion, searchRequest);
    }

    private void testWithRemove(Document toAdd, Document toAddAndRemoveFromIndex, SearchRequest searchRequest) {
        searchService.addObjectToIndex(tenantId, toAdd);
        searchService.addObjectToIndex(tenantId, toAddAndRemoveFromIndex);

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(toAdd.getUri(), toAddAndRemoveFromIndex.getUri())));

        searchService.removeObjectFromIndex(toAddAndRemoveFromIndex);

        searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(toAdd.getUri()));
    }

    @Test
    public void editDistanceTest() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document document = new Document(new DocumentUri(tenantId), attributes);
        DocumentUri documentUri = searchService.addObjectToIndex(tenantId, document);

        Collection<DocumentUri> result = searchService.search(tenantId, SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.EDIIT_DIST3)
                .setValueToSearch("value")
                .build());
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(documentUri));

        result = searchService.search(tenantId, SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.EDIIT_DIST3)
                .setValueToSearch("valua")
                .build());
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(documentUri));

        result = searchService.search(tenantId, SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.EDIIT_DIST3)
                .setValueToSearch("valu")
                .build());
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void distanceTest() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute0", "value0");
        attributes.put("attribute1", "value1");
        attributes.put("attribute2", "value2");
        Document document = new Document(new DocumentUri(tenantId), attributes);
        DocumentUri documentUri = searchService.addObjectToIndex(tenantId, document);

        Map<String, String> attributesForQuery = new HashMap<>();
        attributesForQuery.put("attribute0", "value0");
        Document documentForQuery = new Document(null, attributesForQuery);
        Map<Set<String>, Collection<DocumentUri>> response = searchService
                .searchNearestDocuments(documentForQuery);
        Assert.assertEquals(1, response.size());
        Assert.assertEquals(1, response.keySet().iterator().next().size());
        Assert.assertTrue(response.keySet().iterator().next().contains("attribute0"));
        Assert.assertEquals(1, response.values().iterator().next().size());
        Assert.assertTrue(response.values().iterator().next().contains(documentUri));

        attributesForQuery.clear();
        attributesForQuery.put("attribute0", "value0");
        attributesForQuery.put("attribute1", "value1");
        documentForQuery = new Document(documentUri, attributesForQuery);
        response = searchService
                .searchNearestDocuments(documentForQuery);
        Assert.assertEquals(3, response.size());
        Assert.assertTrue(response.keySet().stream()
                .anyMatch(i -> i.size() == 1 && i.contains("attribute0")));
        Assert.assertTrue(response.keySet().stream()
                .anyMatch(i -> i.size() == 1 && i.contains("attribute1")));
        Assert.assertTrue(response.keySet().stream()
                .anyMatch(i -> i.size() == 2 && i.containsAll(Arrays.asList("attribute0", "attribute1"))));
        Assert.assertTrue(response.values().stream()
                .allMatch(i -> i.size() == 1 && i.contains(documentUri)));
    }

}
