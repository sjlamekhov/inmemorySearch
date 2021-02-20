package search.inmemory.byAttributeValue;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import search.inmemory.SearchIndex;
import search.request.SearchRequest;

import java.util.Set;

public interface SearchIndexByAttributeValue<U extends AbstractObjectUri, T extends AbstractObject> extends SearchIndex<U, T> {

    Set<U> search(SearchRequest searchRequest);

    Set<U> searchByLength(String attributeToSearch, int lengthToSearch);

    Set<U> searchByContains(String attributeToSearch, String valueToSearch);

    Set<U> searchByDistance(String attributeToSearch, String valueToSearch);

    T getObjectByUri(U uri);

}
