package search.inmemory;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.ConditionType;
import search.request.SearchRequest;
import search.SearchService;

import java.util.*;
import java.util.stream.Collectors;

import static search.SearchServiceUtils.combineAnd;

//TODO: locks and concurrency stuff
public class InMemorySearchService<U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final Map<String, TreeMap<String, Set<U>>> attributeIndexes;
    private final Map<String, Trie<U>> attributePrefixIndexes;
    private final Map<U, Set<String>> reverseAttributeIndex;

    public InMemorySearchService() {
        this.attributeIndexes = new HashMap<>();
        this.attributePrefixIndexes = new HashMap<>();
        this.reverseAttributeIndex = new HashMap<>();
    }

    @Override
    public void addObjectToIndex(T object) {
        Objects.requireNonNull(object);
        U uri = (U) object.getUri();
        Map<String, String> attributes = object.getAttributes();
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            attributeIndexes
                    .computeIfAbsent(attribute.getKey(), i -> new TreeMap<>())
                    .computeIfAbsent(attribute.getValue(), i -> new HashSet<>())
                    .add(uri);
            String value = attribute.getValue().toLowerCase();
            attributePrefixIndexes
                    .computeIfAbsent(attribute.getKey(), i -> new Trie<>())
                    .addValueAndUri(value, uri);
            reverseAttributeIndex.computeIfAbsent(uri, i -> new HashSet<>()).add(attribute.getKey());
        }
    }

    @Override
    public void removeObjectFromIndex(T object) {
        Objects.requireNonNull(object);
        U uri = (U) object.getUri();
        Set<String> attributeNamesToWipe = reverseAttributeIndex.get(uri);
        for (String attributeName : attributeNamesToWipe) {
            Map<String, Set<U>> attributeIndex = attributeIndexes.get(attributeName);
            if (attributeIndex == null) {
                continue;
            }
            for (Map.Entry<String, Set<U>> entry : attributeIndex.entrySet()) {
                entry.getValue().removeIf(i -> Objects.equals(uri, i));
            }
            Trie<U> trie = attributePrefixIndexes.get(attributeName);
            if (trie == null) {
                continue;
            }
            trie.removeUriFromTrie(uri);
        }
        reverseAttributeIndex.remove(uri);
    }

    @Override
    public Collection<U> search(String tenantId, SearchRequest searchRequest) {
        return searchInternal(tenantId, searchRequest);
    }

    private Collection<U> searchInternal(String tenantId, SearchRequest searchRequest) {
        Collection<U> result = Collections.emptySet();
        Collection<U> leafResult = searchLeaf(tenantId, searchRequest);
        if (searchRequest.getAndRequests().isEmpty() && searchRequest.getOrRequests().isEmpty()) {
            return leafResult;
        } else if (!searchRequest.getAndRequests().isEmpty()) {
            List<Collection<U>> results = new ArrayList<>();
            for (SearchRequest andRequest : searchRequest.getAndRequests()) {
                results.add(searchInternal(tenantId, andRequest));
            }
            result = combineAnd(leafResult, results);
        } else if (!searchRequest.getOrRequests().isEmpty()) {
            result = new HashSet<>(leafResult);
            for (SearchRequest orRequest : searchRequest.getOrRequests()) {
                result.addAll(searchInternal(tenantId, orRequest));
            }
        }
        return result;
    }

    private Set<U> searchLeaf(String tenantId, SearchRequest searchRequest) {
        ConditionType conditionType = searchRequest.getConditionType();
        String attributeToSearch = searchRequest.getAttributeToSearch();
        String valueToSearch = searchRequest.getValueToSearch();

        if (conditionType == ConditionType.ALL) {
            return new HashSet<>(reverseAttributeIndex.keySet());
        }

        Set<U> result = new HashSet<>();
        TreeMap<String, Set<U>> attributeIndex = attributeIndexes.get(attributeToSearch);
        if (attributeIndex == null) {
            return Collections.emptySet();
        }
        if (conditionType == ConditionType.EQ) {
            Set<U> innerResult = attributeIndex.get(valueToSearch);
            if (null != innerResult && !innerResult.isEmpty()) {
                result.addAll(innerResult);
            }
        } else if (conditionType == ConditionType.NE) {
            attributeIndex.forEach((key, value) -> {
                if (!Objects.equals(valueToSearch, key)) {
                    result.addAll(value);
                }
            });
        } else if(conditionType == ConditionType.GT) {
            Map<String, Set<U>> map = attributeIndex.tailMap(valueToSearch, false);
            result.addAll(map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        } else if(conditionType == ConditionType.LT) {
            Map<String, Set<U>> map = attributeIndex.headMap(valueToSearch, false);
            result.addAll(map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        } else {
            throw new UnsupportedOperationException();
        }
        return result;
    }

    @Override
    public long count(String tenantId, SearchRequest searchRequest) {
        return search(tenantId, searchRequest).size();
    }

    @Override
    public Collection<U> typeAheadSearch(String tenantId, String field, String prefix) {
        Trie<U> prefixSearchIndex = attributePrefixIndexes.get(field);
        if (prefixSearchIndex == null) {
            return Collections.emptySet();
        }
        prefix = prefix.toLowerCase();
        return prefixSearchIndex.getUrisByStartsWith(prefix);
    }

    @Override
    public void dropIndexes(String tenantId) {
        attributeIndexes.clear();
        reverseAttributeIndex.clear();
    }

    @Override
    public void close() {
    }
}
