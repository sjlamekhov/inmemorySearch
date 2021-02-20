package search.inmemory;

import objects.AbstractObject;
import objects.AbstractObjectUri;

public interface SearchIndex<U extends AbstractObjectUri, T extends AbstractObject> {

    U indexObject(T object);
    void removeObjectFromIndex(U uri);
    void dropIndexes();


}
