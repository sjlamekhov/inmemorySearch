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
    public void allRequest() {
        final int numberOfObjects = 16;
        Set<DocumentUri> uris = new HashSet<>();
        for (int i = 0; i < numberOfObjects; i++) {
            DocumentUri documentUri = new DocumentUri(tenantId);
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute", "value" + i);
            Document document = new Document(documentUri, attributes);
            searchService.addObjectToIndex(document);
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
        DocumentUri documentUri1 = new DocumentUri(tenantId);
        Map<String, String> attributes1 = new HashMap<>();
        attributes1.put("attribute", "value1");
        Document document1 = new Document(documentUri1, attributes1);
        searchService.addObjectToIndex(document1);

        DocumentUri documentUri2 = new DocumentUri(tenantId);
        Map<String, String> attributes2 = new HashMap<>();
        attributes2.put("attribute", "value2");
        Document document2 = new Document(documentUri2, attributes2);
        searchService.addObjectToIndex(document2);

        DocumentUri documentUri3 = new DocumentUri(tenantId);
        Map<String, String> attributes3 = new HashMap<>();
        attributes3.put("attribute", "value3");
        Document document3 = new Document(documentUri3, attributes3);
        searchService.addObjectToIndex(document3);

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
        searchService.addObjectToIndex(document1);

        DocumentUri documentUri2 = new DocumentUri(tenantId);
        Map<String, String> attributes2 = new HashMap<>();
        attributes2.put("attribute", "value2");
        Document document2 = new Document(documentUri2, attributes2);
        searchService.addObjectToIndex(document2);

        DocumentUri documentUri3 = new DocumentUri(tenantId);
        Map<String, String> attributes3 = new HashMap<>();
        attributes3.put("attribute", "value3");
        Document document3 = new Document(documentUri3, attributes3);
        searchService.addObjectToIndex(document3);

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
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document document = new Document(documentUri, attributes);
        searchService.addObjectToIndex(document);

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
        searchService.addObjectToIndex(document);

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
        searchService.addObjectToIndex(toAdd);
        searchService.addObjectToIndex(toAddAndRemoveFromIndex);

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(toAdd.getUri(), toAddAndRemoveFromIndex.getUri())));

        searchService.removeObjectFromIndex(toAddAndRemoveFromIndex);

        searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(toAdd.getUri()));
    }

}
