package search.optimizer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import search.ConditionType;
import search.request.SearchRequest;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class PlainSearchRequestOptimizer implements SearchRequestOptimizer {

    public SearchRequest optimize(SearchRequest input) {
        return optimizeInternal(input).getLeft();
    }

    private ImmutablePair<SearchRequest, Boolean> optimizeInternal(SearchRequest input) {
        Set<SearchRequest> andRequests = input.getAndRequests();
        Set<SearchRequest> orRequests = input.getOrRequests();
        boolean processAndRequests = !andRequests.isEmpty();    //AND requests are in priority
        List<SearchRequest> optimizedAndRequests = null;
        List<SearchRequest> optimizedOrRequests = null;
        boolean changesDetected = false;
        if (processAndRequests) {
            optimizedAndRequests = new ArrayList<>(andRequests.size());
            for (SearchRequest searchRequest : andRequests) {
                Pair<SearchRequest, Boolean> optimized = optimizeInternal(searchRequest);
                if (null == optimized.getLeft()) {
                    return new ImmutablePair<>(null, true);
                }
                changesDetected |= optimized.getRight();
                optimizedAndRequests.add(optimized.getLeft());
            }
            if (!checkRequestCompatibility(input, optimizedAndRequests)) {
                return new ImmutablePair<>(null, true);
            }
        } else {
            optimizedOrRequests = new ArrayList<>(orRequests.size());
            for (SearchRequest searchRequest : orRequests) {
                Pair<SearchRequest, Boolean> optimized = optimizeInternal(searchRequest);
                if (null == optimized.getLeft()) {
                    return new ImmutablePair<>(null, true);
                }
                changesDetected |= !optimized.getRight();
                optimizedOrRequests.add(optimized.getLeft());
            }
        }
        if (!changesDetected) {
            return new ImmutablePair<>(input, false);
        } else {
            return new ImmutablePair<>(SearchRequest.Builder.newInstance()
                    .setConditionType(input.getConditionType())
                    .setAttributeToSearch(input.getAttributeToSearch())
                    .setValueToSearch(input.getValueToSearch())
                    .and(processAndRequests ? optimizedAndRequests : andRequests)
                    .or(!processAndRequests ? optimizedOrRequests : orRequests)
                    .build(), true);
        }
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

            //ConditionType.STWITH
            if (!checkStwithCompatibility(groupedByConditionType.get(ConditionType.STWITH))) {
                return false;
            }

            //ConditionType.LENGTH
            if (!checkLengthCompatibility(groupedByConditionType.get(ConditionType.LENGTH))) {
                return false;
            }

            //ConditionType.EQ
            if (!checkEqCompatilibility(groupedByConditionType.get(ConditionType.EQ))) {
                return false;
            }

            //ConditionType.LT && ConditionType.GT
            if (!checkGtLtCompatibility(
                    groupedByConditionType.get(ConditionType.LT),
                    groupedByConditionType.get(ConditionType.GT))) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkLengthCompatibility(Set<SearchRequest> lengthRequests) {
        if (null != lengthRequests && lengthRequests.size() > 1) {
            Set<Integer> lengths = lengthRequests.stream().map(i -> Integer.parseInt(i.getValueToSearch())).collect(toSet());
            return 1 == lengths.size();
        }
        return true;
    }

    private static boolean checkGtLtCompatibility(Set<SearchRequest> ltRequests, Set<SearchRequest> gtRequests) {
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
        return true;
    }

    private static boolean checkEqCompatilibility(Set<SearchRequest> eqRequests) {
        if (null != eqRequests && eqRequests.size() > 1) {
            Set<String> values = eqRequests.stream().map(SearchRequest::getValueToSearch).collect(toSet());
            return 1 == values.size();
        }
        return true;
    }

    private static boolean checkStwithCompatibility(Set<SearchRequest> stwithRequests) {
        if (null != stwithRequests && stwithRequests.size() > 1) {
            List<String> startsWithValues = stwithRequests.stream()
                    .map(SearchRequest::getValueToSearch).distinct()
                    .sorted((i1, i2) -> Integer.compare(i2.length(), i1.length()))  //DESCENDING order
                    .collect(toList());
            Iterator<String> valueIterator = startsWithValues.iterator();
            String mainValueToCompare = valueIterator.next();
            while (valueIterator.hasNext()) {
                if (!mainValueToCompare.startsWith(valueIterator.next())) {
                    return false;
                }
            }
        }
        return true;
    }

}
