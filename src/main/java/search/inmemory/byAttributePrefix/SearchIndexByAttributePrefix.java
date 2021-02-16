package search.inmemory.byAttributePrefix;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.inmemory.SearchIndex;

import java.util.Set;

public interface SearchIndexByAttributePrefix<U extends AbstractObjectUri, T extends AbstractObject> extends SearchIndex<U, T> {

    Set<U> searchByPrefix(String field, String prefix);

}
