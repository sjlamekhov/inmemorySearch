package server.controllers;

import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import search.ConditionType;
import search.request.SearchRequest;
import search.request.SearchRequestConverter;
import search.request.SearchRequestStringConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {server.Application.class})
public class SearchControllerTest {

    @Autowired
    private SearchController searchController;

    private static final String tenantId = "testTenant";
    private SearchRequestConverter searchRequestConverter = new SearchRequestStringConverter();

    @Test
    public void indexAndSearchTest() {
        DocumentUri documentUri = new DocumentUri(tenantId);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attribute", "value");
        Document document = new Document(documentUri, attributes);

        final SearchRequest searchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setValueToSearch("value")
                .setConditionType(ConditionType.EQ)
                .build();
        String searchRequestConverted = searchRequestConverter.convertToString(searchRequest);
        Assert.assertNotNull(searchRequestConverted);

        searchController.indexDocument(tenantId, document);
        Collection<DocumentUri> result = searchController.searchDocument(tenantId, searchRequestConverted);
        Assert.assertEquals(1, result.size());

        SearchController.CountResponse countResponse = searchController.countDocuments(tenantId, searchRequestConverted);
        Assert.assertNotNull(countResponse);
        Assert.assertEquals(1, countResponse.getCount());

        searchController.removeDocumentFromIndex(tenantId, document.getUri().getId());
        result = searchController.searchDocument(tenantId, searchRequestConverted);
        Assert.assertTrue(result.isEmpty());

        countResponse = searchController.countDocuments(tenantId, searchRequestConverted);
        Assert.assertNotNull(countResponse);
        Assert.assertEquals(0, countResponse.getCount());
    }

}