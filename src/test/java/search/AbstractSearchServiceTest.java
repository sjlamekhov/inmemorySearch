package search;

import objects.Document;
import objects.DocumentUri;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import search.ConditionType;
import search.SearchRequest;
import search.SearchService;


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
    public void eqIndexedAttribute() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document document = new Document(documentUri, attributes);

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value")
                .setConditionType(ConditionType.EQ)
                .build();

        searchService.addObjectToIndex(document);
        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUri));
        Assert.assertEquals(1, searchService.count(tenantId, searchRequest));

        searchService.removeObjectFromIndex(document);
        searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(0, searchResult.size());
        Assert.assertEquals(0, searchService.count(tenantId, searchRequest));
    }

    @Test
    public void neqIndexedAttribute() {
        DocumentUri documentUri1 = new DocumentUri(tenantId);
        Map<String, String> attributes1 = new HashMap<>();
        attributes1.put("attribute", "value1");
        Document document1 = new Document(documentUri1, attributes1);
        searchService.addObjectToIndex(document1);

        DocumentUri documentUri2 = new DocumentUri(tenantId);
        Map<String, String> attributes2 = new HashMap<>();
        attributes2.put("attribute", "value12");
        Document document2 = new Document(documentUri2, attributes2);
        searchService.addObjectToIndex(document2);

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value")
                .setConditionType(ConditionType.NE)
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUri1, documentUri2)));
        Assert.assertEquals(2, searchService.count(tenantId, searchRequest));

        final SearchRequest searchRequest1 = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value1")
                .setConditionType(ConditionType.NE)
                .build();

        Collection<DocumentUri> searchResult1 = searchService.search(tenantId, searchRequest1);
        Assert.assertEquals(1, searchResult1.size());
        Assert.assertTrue(searchResult1.contains(documentUri2));
        Assert.assertEquals(1, searchService.count(tenantId, searchRequest1));
    }

    @Test
    public void gtIndexedAttribute() {
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

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value")
                .setConditionType(ConditionType.GT)
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUri1, documentUri2)));
        Assert.assertEquals(2, searchService.count(tenantId, searchRequest));

        final SearchRequest searchRequest1 = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value1")
                .setConditionType(ConditionType.GT)
                .build();

        Collection<DocumentUri> searchResult1 = searchService.search(tenantId, searchRequest1);
        Assert.assertEquals(1, searchResult1.size());
        Assert.assertTrue(searchResult1.contains(documentUri2));
        Assert.assertEquals(1, searchService.count(tenantId, searchRequest1));
    }

    @Test
    public void ltIndexedAttribute() {
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

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("valuevalue")
                .setConditionType(ConditionType.LT)
                .build();

        Collection<DocumentUri> searchResult = searchService.search(tenantId, searchRequest);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUri1, documentUri2)));
        Assert.assertEquals(2, searchService.count(tenantId, searchRequest));

        final SearchRequest searchRequest1 = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value2")
                .setConditionType(ConditionType.LT)
                .build();

        Collection<DocumentUri> searchResult1 = searchService.search(tenantId, searchRequest1);
        Assert.assertEquals(1, searchResult1.size());
        Assert.assertTrue(searchResult1.contains(documentUri1));
        Assert.assertEquals(1, searchService.count(tenantId, searchRequest1));
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
    public void typeAheadSearch() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document document = new Document(documentUri, attributes);
        searchService.addObjectToIndex(document);

        Collection<DocumentUri> searchResult = searchService.typeAheadSearch(tenantId, "attribute", "value");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUri));

        searchResult = searchService.typeAheadSearch(tenantId, "attribute", "valueNotExisting");
        Assert.assertEquals(0, searchResult.size());
    }

    @Test
    public void typeAheadSearchAndRemoveFromIndex() {

        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "valueLong");
        Document document = new Document(documentUri, attributes);
        searchService.addObjectToIndex(document);

        DocumentUri documentUriForDeletion = new DocumentUri(tenantId);
        Map<String, String> attributesForDeletion = new HashMap<>();
        attributesForDeletion.put("attribute", "value");
        Document documentForDeletion = new Document(documentUriForDeletion, attributesForDeletion);
        searchService.addObjectToIndex(documentForDeletion);

        Collection<DocumentUri> searchResult = searchService.typeAheadSearch(tenantId, "attribute", "value");
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUriForDeletion, documentUri)));

        searchService.removeObjectFromIndex(documentForDeletion);

        searchResult = searchService.typeAheadSearch(tenantId, "attribute", "valuel");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUri));
    }

}
