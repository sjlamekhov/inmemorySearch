package search.optimizer;

import org.junit.Assert;
import org.junit.Test;
import search.ConditionType;
import search.request.SearchRequest;

public class PlainSearchRequestOptimizerTest {

    private SearchRequestOptimizer searchRequestOptimizer = new PlainSearchRequestOptimizer();

    @Test
    public void testAppliableEQ() {
        SearchRequest appliableSearchRequest = SearchRequest.Builder.newInstance()
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("VALUE1")
                .setAttributeToSearch("attribute1")
                .and(SearchRequest.Builder.newInstance()
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("VALUE2")
                        .setAttributeToSearch("attribute2")
                        .build())
                .build();
        Assert.assertEquals(appliableSearchRequest, searchRequestOptimizer.optimize(appliableSearchRequest));
    }

    @Test
    public void testNotAppliableEQ() {
        SearchRequest notAppliableSearchRequest = SearchRequest.Builder.newInstance()
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("VALUE1")
                .setAttributeToSearch("attribute")
                .and(SearchRequest.Builder.newInstance()
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("VALUE2")
                        .setAttributeToSearch("attribute")
                        .build())
                .build();
        Assert.assertNull(searchRequestOptimizer.optimize(notAppliableSearchRequest));
    }

    @Test
    public void testAppliableGTLT() {
        SearchRequest notAppliableSearchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.GT)
                .setValueToSearch("ABC")
                .and(SearchRequest.Builder.newInstance()
                        .setAttributeToSearch("attribute")
                        .setConditionType(ConditionType.LT)
                        .setValueToSearch("XYZ")
                        .build())
                .build();
        Assert.assertNotNull(searchRequestOptimizer.optimize(notAppliableSearchRequest));
    }

    @Test
    public void testNotAppliableGTLT() {
        SearchRequest notAppliableSearchRequest = SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute")
                .setConditionType(ConditionType.LT)
                .setValueToSearch("ABC")
                .and(SearchRequest.Builder.newInstance()
                        .setAttributeToSearch("attribute")
                        .setConditionType(ConditionType.GT)
                        .setValueToSearch("XYZ")
                        .build())
                .build();
        Assert.assertNull(searchRequestOptimizer.optimize(notAppliableSearchRequest));
    }

}