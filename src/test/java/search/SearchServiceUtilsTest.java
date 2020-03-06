package search;

import org.junit.Assert;
import org.junit.Test;
import search.request.SearchRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class SearchServiceUtilsTest {

    private static final SearchRequest simpleSearchRequest = SearchRequest.Builder.newInstance()
            .setAttributeToSearch("attribute")
            .setConditionType(ConditionType.EQ)
            .setValueToSearch("value")
            .build();

    private static final SearchRequest innerSearchRequest1 = SearchRequest.Builder.newInstance()
            .setAttributeToSearch("innerAttribute1")
            .setConditionType(ConditionType.EQ)
            .setValueToSearch("innerValue1")
            .build();

    private static final SearchRequest innerSearchRequest2 = SearchRequest.Builder.newInstance()
            .setAttributeToSearch("innerAttribute2")
            .setConditionType(ConditionType.EQ)
            .setValueToSearch("innerValue2")
            .build();

    @Test
    public void collectAllSearchRequestsTest() {
        Assert.assertEquals(
                Collections.singleton(simpleSearchRequest),
                SearchServiceUtils.collectAllSearchRequests(simpleSearchRequest)
        );
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(simpleSearchRequest, innerSearchRequest1, innerSearchRequest2)),
                SearchServiceUtils.collectAllSearchRequests(SearchRequest.Builder.newInstance()
                        .setAttributeToSearch(simpleSearchRequest.getAttributeToSearch())
                        .setConditionType(simpleSearchRequest.getConditionType())
                        .setValueToSearch(simpleSearchRequest.getValueToSearch())
                        .and(Arrays.asList(innerSearchRequest1, innerSearchRequest2))
                        .build())
        );
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(simpleSearchRequest, innerSearchRequest1, innerSearchRequest2)),
                SearchServiceUtils.collectAllSearchRequests(SearchRequest.Builder.newInstance()
                        .setAttributeToSearch(simpleSearchRequest.getAttributeToSearch())
                        .setConditionType(simpleSearchRequest.getConditionType())
                        .setValueToSearch(simpleSearchRequest.getValueToSearch())
                        .or(Arrays.asList(innerSearchRequest1, innerSearchRequest2)).build())
        );
        //AND is in priority
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(simpleSearchRequest, innerSearchRequest1)),
                SearchServiceUtils.collectAllSearchRequests(SearchRequest.Builder.newInstance()
                        .setAttributeToSearch(simpleSearchRequest.getAttributeToSearch())
                        .setConditionType(simpleSearchRequest.getConditionType())
                        .setValueToSearch(simpleSearchRequest.getValueToSearch())
                        .and(innerSearchRequest1)
                        .or(innerSearchRequest2)
                        .build())
        );
    }

}