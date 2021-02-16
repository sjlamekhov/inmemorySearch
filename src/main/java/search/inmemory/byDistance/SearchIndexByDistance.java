package search.inmemory.byDistance;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.inmemory.SearchIndex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface SearchIndexByDistance<U extends AbstractObjectUri, T extends AbstractObject> extends SearchIndex<U, T> {

    Map<Set<String>, Collection<U>> searchNearestDocuments(T input);

}
