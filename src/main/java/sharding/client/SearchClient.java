package sharding.client;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class SearchClient<U extends AbstractObjectUri, T extends AbstractObject> {

    public abstract Collection<U> executeSearchRequest(String tenantId, SearchRequest searchRequest);

    public abstract void executeSearchRequestAsync(String tenantId, SearchRequest searchRequest, Consumer<Collection<U>> consumer);

    public abstract T getObjectByUriRequest(U uri);

    public abstract Map<List<String>, Collection<U>> searchNearestDocuments(T object);

    public abstract void removeObjectFromIndex(T object);
}
