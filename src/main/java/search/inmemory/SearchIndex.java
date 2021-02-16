package search.inmemory;

import objects.AbstractObject;
import objects.AbstractObjectUri;

import java.util.Set;

public interface SearchIndex<U extends AbstractObjectUri, T extends AbstractObject> {

    U indexObject(T object);
    void removeObjectFromIndex(Set<String> attributeNames, U uri);


}
