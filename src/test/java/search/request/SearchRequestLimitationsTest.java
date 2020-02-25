package search.request;

import org.junit.Assert;
import org.junit.Test;
import search.ConditionType;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SearchRequestLimitationsTest {

    private static final SearchRequest searchRequestOneLevel = SearchRequest.Builder.newInstance()
            .setAttributeToSearch("attribute")
            .setConditionType(ConditionType.EQ)
            .setValueToSearch("value")
            .build();

    private static final SearchRequest searchRequestTwoLevelsAndSize3 = SearchRequest.Builder.newInstance()
            .setAttributeToSearch("attribute1")
            .setConditionType(ConditionType.EQ)
            .setValueToSearch("value1")
            .and(Arrays.asList(SearchRequest.Builder.newInstance()
                            .setAttributeToSearch("attribute2")
                            .setConditionType(ConditionType.EQ)
                            .setValueToSearch("value2")
                            .build(),
                    SearchRequest.Builder.newInstance()
                            .setAttributeToSearch("attribute3")
                            .setConditionType(ConditionType.EQ)
                            .setValueToSearch("value3")
                            .build()))
            .build();

    @Test
    public void getMaxTest() {
        SearchRequestLimitations searchRequestLimitations = new SearchRequestLimitations(1, 1);
        Assert.assertEquals(1, searchRequestLimitations.getMaxDepth());
        Assert.assertEquals(1, searchRequestLimitations.getMaxSize());
    }

    @Test
    public void depthTest() {
        SearchRequestLimitations searchRequestLimitations = new SearchRequestLimitations(1, 1);
        Assert.assertTrue(searchRequestLimitations.checkDepth(searchRequestOneLevel));
        Assert.assertFalse(searchRequestLimitations.checkDepth(searchRequestTwoLevelsAndSize3));
    }

    @Test
    public void sizeTest() {
        SearchRequestLimitations searchRequestLimitations = new SearchRequestLimitations(1, 1);
        Assert.assertTrue(searchRequestLimitations.checkSize(searchRequestOneLevel));
        Assert.assertFalse(searchRequestLimitations.checkSize(searchRequestTwoLevelsAndSize3));
    }

}