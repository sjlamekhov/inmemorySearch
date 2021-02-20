package search.inmemory.byAttributeValue;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import objects.DocumentUri;
import search.ConditionType;
import search.editDistance.EditGenerator;
import search.request.SearchRequest;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchIndexByAttributeValueImpl<U extends AbstractObjectUri, T extends AbstractObject>
        implements SearchIndexByAttributeValue<U, T> {

    private final Map<String, TreeMap<String, Set<U>>> attributeIndexes;
    private final Map<U, Set<String>> reverseAttributeIndex;

    public SearchIndexByAttributeValueImpl() {
        this.attributeIndexes = new HashMap<>();
        this.reverseAttributeIndex = new HashMap<>();
    }

    @Override
    public Set<U> search(SearchRequest searchRequest) {
        ConditionType conditionType = searchRequest.getConditionType();

        if (ConditionType.ALL == conditionType) {
            return new HashSet<>(reverseAttributeIndex.keySet());
        }

        String attributeToSearch = searchRequest.getAttributeToSearch();
        String valueToSearch = searchRequest.getValueToSearch();

        Set<U> result = new HashSet<>();
        TreeMap<String, Set<U>> attributeIndex = attributeIndexes.get(attributeToSearch);
        if (attributeIndex == null) {
            return Collections.emptySet();
        }
        if (ConditionType.EQ == conditionType) {
            Set<U> innerResult = attributeIndex.get(valueToSearch);
            if (null != innerResult && !innerResult.isEmpty()) {
                result.addAll(innerResult);
            }
        } else if (ConditionType.NE == conditionType) {
            attributeIndex.forEach((key, value) -> {
                if (!Objects.equals(valueToSearch, key)) {
                    result.addAll(value);
                }
            });
        } else if(ConditionType.GT == conditionType) {
            Map<String, Set<U>> map = attributeIndex.tailMap(valueToSearch, false);
            result.addAll(map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        } else if(ConditionType.LT == conditionType) {
            Map<String, Set<U>> map = attributeIndex.headMap(valueToSearch, false);
            result.addAll(map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        } else {
            throw new UnsupportedOperationException();
        }
        return result;
    }

    @Override
    public U indexObject(T object) {
        U uri = (U) object.getUri();
        Map<String, String> attributes = object.getAttributes();
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            attributeIndexes
                    .computeIfAbsent(attribute.getKey(), i -> new TreeMap<>())
                    .computeIfAbsent(attribute.getValue(), i -> new HashSet<>())
                    .add(uri);
            reverseAttributeIndex.computeIfAbsent(uri, i -> new HashSet<>()).add(attribute.getKey());
        }
        return uri;
    }

    @Override
    public void removeObjectFromIndex(U uri) {
        Set<String> attributeNames = reverseAttributeIndex.get(uri);
        for (String attributeName : attributeNames) {
            Map<String, Set<U>> attributeIndex = attributeIndexes.get(attributeName);
            if (attributeIndex == null) {
                continue;
            }
            Iterator<Map.Entry<String, Set<U>>> entryIterator = attributeIndex.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, Set<U>> entry = entryIterator.next();
                entry.getValue().removeIf(i -> Objects.equals(uri, i));
                if (entry.getValue().isEmpty()) {
                    entryIterator.remove();
                }
            }
        }
        reverseAttributeIndex.remove(uri);
    }

    @Override
    public void dropIndexes() {
        attributeIndexes.clear();
        reverseAttributeIndex.clear();
    }

    @Override
    public Set<U> searchByLength(String attributeToSearch, int lengthToSearch) {
        return searchByPredicate(attributeToSearch, i -> lengthToSearch == i.length());
    }

    @Override
    public Set<U> searchByContains(String attributeToSearch, String valueToSearch) {
        //TODO: implement using suffix array
        return searchByPredicate(attributeToSearch, i -> i.contains(valueToSearch));
    }

    @Override
    public Set<U> searchByDistance(String attributeToSearch, String valueToSearch) {
        Set<String> allEditsOfInputString = EditGenerator.generateAllEdits(valueToSearch, 3);
        return searchByPredicate(attributeToSearch, allEditsOfInputString::contains);
    }

    @Override
    public T getObjectByUri(U uri) {
        Map<String, String> attributes = new HashMap<>();
        for (Map.Entry<String, TreeMap<String, Set<U>>> entry : attributeIndexes.entrySet()) {
            String attributeName = entry.getKey();
            for (Map.Entry<String, Set<U>> valueAndUri : entry.getValue().entrySet()) {
                if (valueAndUri.getValue().contains(uri)) {
                    attributes.put(attributeName, valueAndUri.getKey());
                    break;
                }
            }
        }
        if (attributes.isEmpty()) {
            return null;
        }
        return (T) new Document((DocumentUri) uri, attributes);
    }

    private Set<U> searchByPredicate(String attributeToSearch, Predicate<String> stringPredicate) {
        TreeMap<String, Set<U>> attributeIndex = attributeIndexes.get(attributeToSearch);
        if (null == attributeIndex) {
            return Collections.emptySet();
        }
        Set<U> result = new HashSet<>();
        for (Map.Entry<String, Set<U>> entry : attributeIndex.entrySet()) {
            if (stringPredicate.test(entry.getKey())) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

}
