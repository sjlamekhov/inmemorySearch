package search;

import search.request.SearchRequest;

import java.util.*;

public class SearchServiceUtils {

    public static Collection<SearchRequest> collectAllSearchRequests(SearchRequest searchRequest) {
        if (null == searchRequest) {
            return Collections.emptySet();
        }
        Set<SearchRequest> result = new HashSet<>();
        result.add(SearchRequest.Builder.newInstance()
                .setAttributeToSearch(searchRequest.getAttributeToSearch())
                .setConditionType(searchRequest.getConditionType())
                .setValueToSearch(searchRequest.getValueToSearch())
                .build());
        if (!searchRequest.getAndRequests().isEmpty()) {
            for (SearchRequest andRequest : searchRequest.getAndRequests()) {
                result.addAll(collectAllSearchRequests(andRequest));
            }
        } else {
            for (SearchRequest orRequest : searchRequest.getOrRequests()) {
                result.addAll(collectAllSearchRequests(orRequest));
            }
        }
        return result;
    }

    //TODO: implement
    public static <U> Collection<U> combinedResult(SearchRequest searchRequest,
                                                       Map<SearchRequest, Collection<U>> fromSharding,
                                                       Map<SearchRequest, Collection<U>> fromCurrentMachine) {
        return Collections.emptySet();
    }

    //calculate intersection between List<Collection<U>> results
    public static <U> Collection<U> combineAnd(Collection<U> leafResult, List<Collection<U>> results) {
        int targetSize = results.size() + 1;
        Collection<U> result = new HashSet<>();
        Map<U, Long> UsAndCounts = new HashMap<>();
        countUs(UsAndCounts, leafResult);
        results.forEach(i -> countUs(UsAndCounts, i));
        for (Map.Entry<U, Long> count : UsAndCounts.entrySet()) {
            if (count.getValue() == targetSize) {
                result.add(count.getKey());
            }
        }
        return result;
    }

    public static <U> void countUs(Map<U, Long> counts, Collection<U> Us) {
        Us.forEach(i -> counts.merge(i, 1L, (a,b) -> a + b));
    }
    
}
