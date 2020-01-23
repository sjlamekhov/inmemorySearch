package search.cache;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;
import search.optimizer.PlainSearchRequestOptimizer;
import search.optimizer.SearchRequestOptimizer;

import java.util.*;
import java.util.function.Function;

public class SearchCache<U extends AbstractObjectUri, T extends AbstractObject> {

    public final int MAX_CACHE_SIZE = 16;
    private final int MAX_RESULTS_SIZE = 128;
    private final int MAX_REQUESTS_TO_REMOVE = 1;

    private final ApplianceChecker applianceChecker;
    private final SearchRequestOptimizer searchRequestOptimizer;
    private final Map<SearchRequest, RequestContext<U>> cachedRequests;
    private final Function<SearchRequest, Collection<U>> loader;

    public SearchCache(Function<SearchRequest, Collection<U>> loader) {
        this.applianceChecker = new ApplianceChecker();
        this.searchRequestOptimizer = new PlainSearchRequestOptimizer();
        this.cachedRequests = new HashMap<>();
        this.loader = loader;
    }

    public synchronized Collection<U> getCached(SearchRequest searchRequest) {
        SearchRequest optimized = searchRequestOptimizer.optimize(searchRequest);
        if (null == optimized) {
            return Collections.emptySet();
        }
        RequestContext<U> context = cachedRequests.get(searchRequest);
        if (null != context) {
            return context.getUris();
        }
        Collection<U> loadedResult = loader.apply(searchRequest);
        RequestContext<U> requestContext = new RequestContext<>(searchRequest);
        requestContext.addUris(loadedResult);
        prepareSpaceForNewRequestCache();
        cachedRequests.put(searchRequest, requestContext);
        return loadedResult;
    }

    //TODO create strategy for invalidation - oldest, smallest, largest
    //TODO now it works basing on oldest
    private void prepareSpaceForNewRequestCache() {
        if (cachedRequests.size() >= MAX_CACHE_SIZE) {
            SortedMap<Long, Set<SearchRequest>> timestamps = new TreeMap<>();
            for (Map.Entry<SearchRequest, RequestContext<U>> entry : cachedRequests.entrySet()) {
                timestamps.computeIfAbsent(entry.getValue().getTimestampOfLastUpdate(), i -> new HashSet<>()).add(entry.getKey());
            }
            Set<SearchRequest> oldest = timestamps.get(timestamps.firstKey());
            int removed = 0;
            for (SearchRequest oldSearchRequest : oldest) {
                cachedRequests.remove(oldSearchRequest);
                removed++;
                if (removed >= MAX_REQUESTS_TO_REMOVE) {
                    break;
                }
            }
        }
    }

    public synchronized void addToIndex(T object) {
        Objects.requireNonNull(object);
        for (Map.Entry<SearchRequest, RequestContext<U>> entry : cachedRequests.entrySet()) {
            if (applianceChecker.test(entry.getKey(), object)) {
                entry.getValue().addUris(Collections.singleton((U)object.getUri()));
            }
        }
    }

    public synchronized void removeObjectFromIndex(T object) {
        Objects.requireNonNull(object);
        for (Map.Entry<SearchRequest, RequestContext<U>> entry : cachedRequests.entrySet()) {
            if (applianceChecker.test(entry.getKey(), object)) {
                entry.getValue().removeUri((U)object.getUri());
            }
        }
    }

    public Set<SearchRequest> getCachedRequests() {
        return new HashSet<>(cachedRequests.keySet());
    }

    public synchronized void dropCache() {
        cachedRequests.clear();
    }

}
