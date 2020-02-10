package search.withChangesCollecting;

import networking.Message;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.SearchService;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class ChangesCollectingSearchService<U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final SearchService<U, T> searchService;
    private final Queue<Message> localChanges;

    public ChangesCollectingSearchService(SearchService<U, T> searchService) {
        this.searchService = searchService;
        this.localChanges = new LinkedList<>();
    }

    public ChangesCollectingSearchService(SearchService<U, T> searchService, Queue<Message> changes) {
        this.searchService = searchService;
        this.localChanges = changes;
    }

    @Override
    public void addObjectToIndex(T object) {
        searchService.addObjectToIndex(object);
        localChanges.add(new Message(
                System.currentTimeMillis(),
                object,
                "",
                Message.MessageType.CREATE
        ));
    }

    @Override
    public void removeObjectFromIndex(T object) {
        searchService.removeObjectFromIndex(object);
        localChanges.add(new Message(
                System.currentTimeMillis(),
                object,
                "",
                Message.MessageType.DELETE
        ));
    }

    @Override
    public Collection<U> typeAheadSearch(String tenantId, String field, String prefix) {
        return searchService.typeAheadSearch(tenantId, field, prefix);
    }

    @Override
    public Collection<U> search(String tenantId, SearchRequest searchRequest) {
        return searchService.search(tenantId, searchRequest);
    }

    @Override
    public long count(String tenantId, SearchRequest searchRequest) {
        return searchService.count(tenantId, searchRequest);
    }

    @Override
    public void dropIndexes(String tenantId) {
        searchService.dropIndexes(tenantId);
    }

    @Override
    public void close() {
        searchService.close();
    }

    public Message pollLocalChanges() {
        return localChanges.poll();
    }
}