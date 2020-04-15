package search.cached;

import objects.AbstractObject;
import search.ConditionType;
import search.request.SearchRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import static search.editDistance.LevenshteinDistance.getDistance;

public class ApplianceChecker implements BiPredicate<SearchRequest, AbstractObject> {

    private final Map<ConditionType, BiPredicate<String, String>> predicates;

    public ApplianceChecker() {
        predicates = new HashMap<>();
        predicates.put(ConditionType.ALL, (valueFromRequest, valueFromObject) -> true);
        predicates.put(ConditionType.EQ, (valueFromRequest, valueFromObject) -> Objects.equals(valueFromRequest, valueFromObject));
        predicates.put(ConditionType.NE, (valueFromRequest, valueFromObject) -> !Objects.equals(valueFromRequest, valueFromObject));
        predicates.put(ConditionType.GT, (valueFromRequest, valueFromObject) -> valueFromObject.compareTo(valueFromRequest) > 0);
        predicates.put(ConditionType.LT, (valueFromRequest, valueFromObject) -> valueFromObject.compareTo(valueFromRequest) < 0);
        predicates.put(ConditionType.STWITH, (valueFromRequest, valueFromObject) -> valueFromObject.startsWith(valueFromRequest));
        predicates.put(ConditionType.CONTAINS, (valueFromRequest, valueFromObject) -> valueFromObject.contains(valueFromRequest));
        predicates.put(ConditionType.LENGTH, (valueFromRequest, valueFromObject) -> valueFromObject.length() == Integer.parseInt(valueFromRequest));
        predicates.put(ConditionType.CLOSE_TO, (valueFromRequest, valueFromObject) -> getDistance(valueFromObject, valueFromRequest) < 3);
    }

    @Override
    public boolean test(SearchRequest searchRequest, AbstractObject object) {
        Objects.requireNonNull(searchRequest);
        boolean mainRequestResult = testLeaf(searchRequest, object);
        Set<SearchRequest> andRequests = searchRequest.getAndRequests();
        Set<SearchRequest> orRequests = searchRequest.getOrRequests();
        if (orRequests.isEmpty() && andRequests.isEmpty()) {
            return mainRequestResult;
        }
        if (mainRequestResult && !andRequests.isEmpty()) {
            for (SearchRequest innerSearchRequest : andRequests) {
                if (!test(innerSearchRequest, object)) {
                    return false;
                }
            }
            return true;
        } else if (!orRequests.isEmpty()) {
            boolean orResult = mainRequestResult;
            for (SearchRequest innerSearchRequest : orRequests) {
                orResult = orResult || test(innerSearchRequest, object);
            }
            return orResult;
        }
        return false;
    }

    private boolean testLeaf(SearchRequest searchRequest, AbstractObject object) {
        Objects.requireNonNull(object);

        String attributeToSearch = searchRequest.getAttributeToSearch();
        String valueFromRequest = searchRequest.getValueToSearch();
        ConditionType conditionType = searchRequest.getConditionType();
        String valueFromObject = object.getAttributes().get(attributeToSearch);

        if (null == valueFromObject && conditionType != ConditionType.ALL) {
            return false;
        }
        BiPredicate<String, String> predicate = predicates.get(conditionType);
        if (null == predicate) {
            return false;
        }
        return predicate.test(valueFromRequest, valueFromObject);
    }

}
