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

    public static <U> Collection<U> combinedResult(SearchRequest searchRequest,
                                                   Map<SearchRequest, Collection<U>> fromSharding,
                                                   Map<SearchRequest, Collection<U>> fromCurrentMachine) {
        Collection<U> leafResult = new HashSet<>();
        SearchRequest mainPart = searchRequest.getMainPart();
        Collection<U> resultFromSharding = fromSharding.get(mainPart);
        if (null != resultFromSharding && !resultFromSharding.isEmpty()) {
            leafResult.addAll(resultFromSharding);
        }
        Collection<U> resultFromCurrent = fromCurrentMachine.get(mainPart);
        if (null != resultFromCurrent && !resultFromCurrent.isEmpty()) {
            leafResult.addAll(resultFromCurrent);
        }
        if (!searchRequest.getAndRequests().isEmpty()) {
            List<Collection<U>> andResults = new ArrayList<>();
            for (SearchRequest innerSearchRequest : searchRequest.getAndRequests()) {
                Collection<U> localResult = combinedResult(innerSearchRequest, fromSharding, fromCurrentMachine);
                if (null != localResult) {
                    andResults.add(localResult);
                }
            }
            leafResult = combineAnd(leafResult, andResults);
        } else {
            for (SearchRequest innerSearchRequest : searchRequest.getOrRequests()) {
                Collection<U> localResult = combinedResult(innerSearchRequest, fromSharding, fromCurrentMachine);
                if (null != localResult) {
                    leafResult.addAll(localResult);
                }
            }
        }
        return leafResult;
    }

    //calculate intersection between List<Collection<U>> results
    //if particular uri is presented in all collections then it will be found n + 1 times where n is collection count
    public static <U> Collection<U> combineAnd(Collection<U> leafResult, List<Collection<U>> results) {
        int targetSize = results.size() + 1;
        Collection<U> result = new HashSet<>();
        Map<U, Long> urisAndCounts = new HashMap<>();
        countUris(urisAndCounts, leafResult);
        results.forEach(i -> countUris(urisAndCounts, i));
        for (Map.Entry<U, Long> count : urisAndCounts.entrySet()) {
            if (count.getValue() == targetSize) {
                result.add(count.getKey());
            }
        }
        return result;
    }

    public static <U> void countUris(Map<U, Long> counts, Collection<U> Us) {
        Us.forEach(i -> counts.merge(i, 1L, (a,b) -> a + b));
    }
    
}
