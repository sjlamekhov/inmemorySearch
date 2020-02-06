package search.cached;

import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class RequestContext<U extends AbstractObjectUri> {

    private final SearchRequest searchRequest;
    private final Set<U> uris;
    private long timestampOfLastUpdate;
    private AtomicLong numberOfRequests;

    public RequestContext(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
        this.uris = new HashSet<>();
        this.timestampOfLastUpdate = System.currentTimeMillis();
        this.numberOfRequests = new AtomicLong(0);
    }

    public void incrementNumberOfRequests() {
        numberOfRequests.incrementAndGet();
    }

    public long getNumberOfRequests() {
        return numberOfRequests.get();
    }

    public void addUris(Collection<U> toAdd) {
        uris.addAll(toAdd);
        timestampOfLastUpdate = System.currentTimeMillis();
    }

    public void removeUri(U uri) {
        uris.remove(uri);
        timestampOfLastUpdate = System.currentTimeMillis();
    }

    public long getTimestampOfLastUpdate() {
        return timestampOfLastUpdate;
    }

    public long getUrisSize() {
        return uris.size();
    }

    public Set<U> getUris() {
        return uris;
    }
}