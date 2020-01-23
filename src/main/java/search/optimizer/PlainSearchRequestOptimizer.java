package search.optimizer;

import search.ConditionType;
import search.request.SearchRequest;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class PlainSearchRequestOptimizer implements SearchRequestOptimizer {

    public SearchRequest optimize(SearchRequest input) {
        Set<SearchRequest> andRequests = input.getAndRequests();
        Set<SearchRequest> orRequests = input.getOrRequests();
        boolean processAndRequests = !andRequests.isEmpty();    //AND requests are in priority
        List<SearchRequest> optimizedAndRequests = new ArrayList<>(andRequests.size());
        Set<SearchRequest> optimizedOrRequests = new HashSet<>(orRequests.size());
        if (processAndRequests) {
            for (SearchRequest searchRequest : andRequests) {
                SearchRequest optimized = optimize(searchRequest);
                if (null == optimized) {
                    return null;
                }
                optimizedAndRequests.add(optimized);
            }
            if (!checkRequestCompatibility(input, optimizedAndRequests)) {
                return null;
            }
        } else {
            for (SearchRequest searchRequest : orRequests) {
                SearchRequest optimized = optimize(searchRequest);
                if (null == optimized) {
                    return null;
                }
                optimizedOrRequests.add(optimized);
            }
        }
        return SearchRequest.Builder.newInstance()
                .setConditionType(input.getConditionType())
                .setAttributeToSearch(input.getAttributeToSearch())
                .setValueToSearch(input.getValueToSearch())
                .and(optimizedAndRequests)
                .or(optimizedOrRequests)
                .build();
    }

    private static boolean checkRequestCompatibility(SearchRequest input, Collection<SearchRequest> optimizedAndRequests) {
        Map<String, Set<SearchRequest>> groupedByAttributeName = optimizedAndRequests.stream()
                .collect(Collectors.groupingBy(SearchRequest::getAttributeToSearch, toSet()));
        groupedByAttributeName.computeIfAbsent(input.getAttributeToSearch(), i -> new HashSet<>()).add(input);

        //check attribute intersections
        for (Map.Entry<String, Set<SearchRequest>> attributeAndRequests : groupedByAttributeName.entrySet()) {
            Set<SearchRequest> requests = attributeAndRequests.getValue();
            if (requests.size() == 1) {
                continue;
            }
            Map<ConditionType, Set<SearchRequest>> groupedByConditionType = requests.stream()
                    .collect(Collectors.groupingBy(SearchRequest::getConditionType, toSet()));
            //ConditionType.EQ
            Set<SearchRequest> eqRequests = groupedByConditionType.get(ConditionType.EQ);
            if (null != eqRequests && eqRequests.size() > 1) {
                Set<String> values = eqRequests.stream().map(SearchRequest::getValueToSearch).collect(toSet());
                if (values.size() > 1) {
                    return false;   //there are search requests with differente EQ values on same level
                }
            }
            //ConditionType.LT && ConditionType.GT
            Set<SearchRequest> ltRequests = groupedByConditionType.get(ConditionType.LT);
            Set<SearchRequest> gtRequests = groupedByConditionType.get(ConditionType.GT);
            if ((null != ltRequests && !ltRequests.isEmpty()) && (null != gtRequests && !gtRequests.isEmpty())) {
                SortedSet<String> ltValues = ltRequests.stream()
                        .map(SearchRequest::getValueToSearch).distinct()
                        .collect(Collectors.toCollection(TreeSet::new));
                SortedSet<String> gtValues = gtRequests.stream()
                        .map(SearchRequest::getValueToSearch).distinct()
                        .collect(Collectors.toCollection(TreeSet::new));
                //at least one GT value should be lower than at least one LT value
                boolean intersectionCanExist = gtValues.last().compareTo(ltValues.first()) < 0;
                if (!intersectionCanExist) {
                    return false;
                }
            }
        }

        return true;
    }

}
