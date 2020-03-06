package sharding;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.request.SearchRequest;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class SearchClient<U extends AbstractObjectUri, T extends AbstractObject> {

    public abstract Collection<U> executeSearchRequest(SearchRequest searchRequest);

    public abstract void executeSearchRequestAsync(SearchRequest searchRequest, Consumer<Collection<U>> consumer);

    public abstract void removeObjectFromIndex(T object);

}
