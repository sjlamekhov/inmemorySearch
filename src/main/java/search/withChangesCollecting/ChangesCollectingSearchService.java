package search.withChangesCollecting;

import networking.Message;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import search.SearchService;
import search.request.SearchRequest;

import javax.print.Doc;
import java.net.InetAddress;
import java.util.*;

public class ChangesCollectingSearchService<U extends AbstractObjectUri, T extends AbstractObject> implements SearchService<U, T> {

    private final SearchService<U, T> searchService;
    private final Queue<Message> localChanges;
    private String hostName = "localhost";

    public ChangesCollectingSearchService(SearchService<U, T> searchService) {
        this.searchService = searchService;
        this.localChanges = new LinkedList<>();
    }

    public ChangesCollectingSearchService(
            SearchService<U, T> searchService,
            Queue<Message> changes) {
        this.searchService = searchService;
        this.localChanges = changes;
        try {
            this.hostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {}
    }

    @Override
    public T getObjectByUri(U uri) {
        return searchService.getObjectByUri(uri);
    }

    @Override
    public U addObjectToIndex(T object) {
        U result = searchService.addObjectToIndex(object);
        localChanges.add(new Message(
                System.currentTimeMillis(),
                (Document) object,
                "",
                Message.MessageType.CREATE
        ));
        return result;
    }

    @Override
    public void removeObjectFromIndex(T object) {
        searchService.removeObjectFromIndex(object);
        localChanges.add(new Message(
                System.currentTimeMillis(),
                (Document) object,
                "",
                Message.MessageType.DELETE
        ));
    }

    @Override
    public Map<Set<String>, Collection<U>> searchNearestDocuments(T object) {
        return searchService.searchNearestDocuments(object);
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
