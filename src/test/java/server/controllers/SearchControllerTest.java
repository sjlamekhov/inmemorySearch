package server.controllers;

import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import search.ConditionType;
import search.request.SearchRequest;
import search.request.SearchRequestConverter;
import search.request.SearchRequestLimitations;
import search.request.SearchRequestStringConverter;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {server.Application.class})
public class SearchControllerTest {

    @Autowired
    private SearchController searchController;

    @Autowired
    private SearchRequestLimitations searchRequestLimitations;

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

    @Test
    public void sizeTest() {
        limitationsTest(
                generateRequestOfSize(searchRequestLimitations.getMaxSize()),
                generateRequestOfSize(searchRequestLimitations.getMaxSize() + 1)
        );
    }

    @Test
    public void depthTest() {
        limitationsTest(
                generateRequestOfDepth(searchRequestLimitations.getMaxDepth()),
                generateRequestOfDepth(searchRequestLimitations.getMaxDepth() + 1)
        );
    }

    private void limitationsTest(SearchRequest goodRequest, SearchRequest tooBigRequest) {
        String convertedRequest = searchRequestConverter.convertToString(goodRequest);
        searchController.countDocuments(tenantId, convertedRequest);

        convertedRequest = searchRequestConverter.convertToString(tooBigRequest);
        try {
            searchController.countDocuments(tenantId, convertedRequest);
            Assert.fail();
        } catch (ResponseStatusException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertEquals("Search request does not pass depth or size check", e.getReason());
        }
    }

    private static SearchRequest generateRequestOfDepth(int depth) {
        if (0 >= depth) {
            return null;
        }
        SearchRequest inner = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute0")
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("value0")
                .build();
        for (int i = 1; i < depth; i++) {
            inner = SearchRequest.Builder.newInstance()
                    .setAttributeToSearch("attribute" + i)
                    .setConditionType(ConditionType.EQ)
                    .setValueToSearch("value" + i)
                    .and(
                            Collections.singleton(inner)
                    )
                    .build();
        }
        return inner;
    }

    private static SearchRequest generateRequestOfSize(int depth) {
        if (0 >= depth) {
            return null;
        }
        List<SearchRequest> and = new ArrayList<>();
        for (int i = 1; i < depth; i++) {
            and.add(SearchRequest.Builder.newInstance()
                    .setAttributeToSearch("attribute" + i)
                    .setConditionType(ConditionType.EQ)
                    .setValueToSearch("value" + i)
                    .build());
        }
        return SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute0")
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("value0")
                .and(and)
                .build();
    }

}