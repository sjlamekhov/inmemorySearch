package search.inmemory;

import dao.AbstractUriIterator;
import dao.DocumentUriIterator;
import dao.ExtractObjectsResult;
import dao.UriGenerator;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import objects.DocumentUri;
import dump.consumers.AbstractObjectConsumer;
import search.ConditionType;
import search.cached.ApplianceChecker;
import search.closestTo.DocumentToCoordinatesCalculator;
import search.editDistance.EditGenerator;
import search.inmemory.byAttributePrefix.SearchIndexByAttributePrefix;
import search.inmemory.byAttributePrefix.SearchIndexByAttributePrefixImpl;
import search.inmemory.byAttributeValue.SearchIndexByAttributeValue;
import search.inmemory.byAttributeValue.SearchIndexByAttributeValueImpl;
import search.request.SearchRequest;
import search.SearchService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static search.SearchServiceUtils.combineAnd;

//TODO: locks and concurrency stuff
public class InMemorySearchService<U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

//    private final Map<String, TreeMap<String, Set<U>>> attributeIndexes;
//    private final Map<U, Set<String>> reverseAttributeIndex;

    private final SearchIndexByAttributeValue<U, T> searchIndexByAttributeValue;
    private final SearchIndexByAttributePrefix<U, T> searchIndexByAttributePrefix;
    private final Map<Set<String>, TreeMap<Long, Set<U>>> objectDistanceIndexes;
    private final DocumentToCoordinatesCalculator<T> documentToCoordinatesCalculator;
    private final Map<String, AbstractUriIterator<U>> documentIteratorMap;
    private final UriGenerator uriGenerator;

    public InMemorySearchService() {
        //move to attribute value index
//        this.attributeIndexes = new HashMap<>();
//        this.reverseAttributeIndex = new HashMap<>();
        this.searchIndexByAttributeValue = new SearchIndexByAttributeValueImpl<>();

        this.searchIndexByAttributePrefix = new SearchIndexByAttributePrefixImpl<>();
        //move to distance index
        this.objectDistanceIndexes = new HashMap<>();
        this.documentToCoordinatesCalculator = new DocumentToCoordinatesCalculator<>();

        this.documentIteratorMap = new HashMap<>();
        this.uriGenerator = new UriGenerator();
    }

    public InMemorySearchService(UriGenerator uriGenerator) {
//        this.attributeIndexes = new HashMap<>();
//        this.reverseAttributeIndex = new HashMap<>();
        this.searchIndexByAttributeValue = new SearchIndexByAttributeValueImpl<>();
//        this.attributePrefixIndexes = new HashMap<>();
        this.searchIndexByAttributePrefix = new SearchIndexByAttributePrefixImpl<>();
        this.objectDistanceIndexes = new HashMap<>();
        this.documentToCoordinatesCalculator = new DocumentToCoordinatesCalculator<>();
        this.documentIteratorMap = new HashMap<>();
        this.uriGenerator = uriGenerator;
    }

    @Override
    public T getObjectByUri(U uri) {
        return searchIndexByAttributeValue.getObjectByUri(uri);
    }

    @Override
    public U addObjectToIndex(String tenantId, T object) {
        Objects.requireNonNull(object);
        propagateUriForNewObject(object, tenantId);
        U uri = (U) object.getUri();
        searchIndexByAttributePrefix.indexObject(object);
        searchIndexByAttributeValue.indexObject(object);

        Map<Set<String>, Long> attributesAndDistances = documentToCoordinatesCalculator
                .combineAttributesAndCoordinates(object, object.getAttributes().keySet());

        for (Map.Entry<Set<String>, Long> distanceEntry : attributesAndDistances.entrySet()) {
            objectDistanceIndexes
                    .computeIfAbsent(distanceEntry.getKey(), i -> new TreeMap<>())
                    .computeIfAbsent(distanceEntry.getValue(), i -> new HashSet<>())
                    .add(uri);
        }
        //TODO: add support of postfix array for CONTAINS requests
        return uri;
    }

    private void propagateUriForNewObject(T object, String tenantId) {
        U uri = (U) object.getUri();
        if (null == uri || (uri.getIsNew() && null ==uri.getId())) {
            try {
                uri = (U) uri.getClass()
                        .getDeclaredConstructor(String.class, String.class)
                        .newInstance(uriGenerator.generateId(), tenantId);
                object.setUri((DocumentUri) uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeObjectFromIndex(T object) {
        Objects.requireNonNull(object);
        U uri = (U) object.getUri();
        searchIndexByAttributePrefix.removeObjectFromIndex(uri);
        searchIndexByAttributeValue.removeObjectFromIndex(uri);
        //TODO: add deleting from objectDistanceIndexes
        //TODO: add deleting from postfix arrays
    }

    @Override
    public Map<Set<String>, Collection<U>> searchNearestDocuments(T input) {
        Map<Set<String>, Long> distancesFromInput = documentToCoordinatesCalculator
                .combineAttributesAndCoordinates(input, input.getAttributes().keySet());
        Map<Set<String>, Collection<U>> result = new HashMap<>();
        for (Map.Entry<Set<String>, Long> distanceEntry : distancesFromInput.entrySet()) {
            Set<String> attributeCombination = distanceEntry.getKey();
            Long distance = distanceEntry.getValue();
            TreeMap<Long, Set<U>> map = objectDistanceIndexes.get(attributeCombination);
            if (null == map) {
                continue;
            }
            Map.Entry<Long, Set<U>> floorEntry = map.floorEntry(distance);
            if (null != floorEntry && distance.equals(floorEntry.getKey())) {
                result.put(attributeCombination, floorEntry.getValue());
                continue;
            }
            Map.Entry<Long, Set<U>> ceilEntry = map.ceilingEntry(distance);
            if (null != ceilEntry && distance.equals(ceilEntry.getKey())) {
                result.put(attributeCombination, ceilEntry.getValue());
                continue;
            }
            if (null != floorEntry && null != ceilEntry) {
                long lowerAbs = Math.abs(floorEntry.getKey() - distance);
                long upperAbs = Math.abs(floorEntry.getKey() - distance);
                if (lowerAbs < upperAbs || lowerAbs == upperAbs) {
                    result.put(attributeCombination, floorEntry.getValue());
                } else {
                    result.put(attributeCombination, ceilEntry.getValue());
                }
            }
        }
        return result;
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

        if (ConditionType.STWITH == conditionType) {
            return searchIndexByAttributePrefix
                    .searchByPrefix(searchRequest.getAttributeToSearch(), searchRequest.getValueToSearch());
        }

        if (ConditionType.LENGTH == conditionType) {
            return searchIndexByAttributeValue.searchByLength(
                    searchRequest.getAttributeToSearch(), Integer.parseInt(searchRequest.getValueToSearch())
            );
        }

        if (ConditionType.CONTAINS == conditionType) {
            return searchIndexByAttributeValue.searchByContains(
                    searchRequest.getAttributeToSearch(),
                    searchRequest.getValueToSearch());
        }

        if (ConditionType.EDIIT_DIST3 == conditionType) {
            return searchIndexByAttributeValue.searchByDistance(
                    searchRequest.getAttributeToSearch(),
                    searchRequest.getValueToSearch()
            );
        }


        return searchIndexByAttributeValue.search(searchRequest);
    }

    @Override
    public long count(String tenantId, SearchRequest searchRequest) {
        return search(tenantId, searchRequest).size();
    }

    public AbstractUriIterator<U> getIterator(String tenantId, String cursorId) {
        AbstractUriIterator<U> documentUriIterator;
        Iterator<Map.Entry<String, AbstractUriIterator<U>>> iterator = documentIteratorMap.entrySet().iterator();
        while (iterator.hasNext()) {
            AbstractUriIterator<U> iteratorItem = iterator.next().getValue();
            if (!iteratorItem.hasNext()) {
                iterator.remove();
            }
        }
        int lengthOfUri = uriGenerator.getLength();
        if (null == cursorId) {
            documentUriIterator = (AbstractUriIterator<U>) new DocumentUriIterator(tenantId, lengthOfUri);
            documentUriIterator.setCursorId(uriGenerator.generateId());
            documentIteratorMap.put(documentUriIterator.getCursorId(), documentUriIterator);
        } else {
            documentUriIterator = documentIteratorMap.get(cursorId);
        }
        return documentUriIterator;
    }

    @Override
    public ExtractObjectsResult<T> extractObjectsByIterator(String tenantId, String cursorId, int maxSize) {
        AbstractUriIterator<U> uriIterator = getIterator(tenantId, cursorId);
        if (null == uriIterator || !uriIterator.hasNext()) {
            return new ExtractObjectsResult<>(cursorId, false, Collections.emptyList());
        }
        List<T> result = new ArrayList<>();
        while (uriIterator.hasNext() && (maxSize == -1 || result.size() < maxSize)) {
            T object = getObjectByUri(uriIterator.next());
            if (null != object) {
                result.add(object);
            }
        }
        return new ExtractObjectsResult<>(uriIterator.getCursorId(), uriIterator.hasNext(), result);
    }

    @Override
    public void extractObjectsByIterator(String tenantId, SearchRequest searchRequest, String cursorId, int maxSize, AbstractObjectConsumer consumer) {
        ApplianceChecker applianceChecker = new ApplianceChecker();
        extractObjectsByIteratorInternal(tenantId, object -> applianceChecker.test(searchRequest, object), cursorId, maxSize, consumer);
    }

    private void extractObjectsByIteratorInternal(String tenantId,
                                                  Predicate<AbstractObject> checker,
                                                  String cursorId,
                                                  int maxSize,
                                                  AbstractObjectConsumer consumer) {
        AbstractUriIterator<U> uriIterator = getIterator(tenantId, cursorId);
        int extractedCount = 0;
        while (uriIterator.hasNext() && (maxSize == -1 || extractedCount < maxSize)) {
            T object = getObjectByUri(uriIterator.next());
            if (null != object && checker.test(object)) {
                consumer.accept(object);
                extractedCount++;
            }
        }
    }

    @Override
    public void dropIndexes(String tenantId) {
        searchIndexByAttributeValue.dropIndexes();
        searchIndexByAttributePrefix.dropIndexes();
    }

    @Override
    public void close() {
    }
}
