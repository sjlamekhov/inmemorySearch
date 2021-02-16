package search.inmemory.byAttributePrefix;

import objects.AbstractObject;
import objects.AbstractObjectUri;

import java.util.*;

public class SearchIndexByAttributePrefixImpl<U extends AbstractObjectUri, T extends AbstractObject> implements SearchIndexByAttributePrefix<U,T> {

    Map<String, Trie<U>> storage;

    public SearchIndexByAttributePrefixImpl() {
        this.storage = new HashMap<>();
    }

    @Override
    public Set<U> searchByPrefix(String field, String prefix) {
        Trie<U> prefixSearchIndex = storage.get(field);
        if (null == prefixSearchIndex || prefixSearchIndex.getMaxLength() + 1 < prefix.length()) {
            return Collections.emptySet();
        }
        prefix = prefix.toLowerCase();
        return new HashSet<>(prefixSearchIndex.getUrisByStartsWith(prefix));
    }

    @Override
    public U indexObject(T object) {
        U uri = (U) object.getUri();
        Map<String, String> attributes = object.getAttributes();
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            String value = attribute.getValue().toLowerCase();
            storage
                    .computeIfAbsent(attribute.getKey(), i -> new Trie<>())
                    .addValueAndUri(value, uri);
        }
        return uri;
    }

    @Override
    public void removeObjectFromIndex(Set<String> attributeNamesToWipe, U uri) {
        for (String attributeName : attributeNamesToWipe) {
            Trie<U> trie = storage.get(attributeName);
            if (trie == null) {
                continue;
            }
            trie.removeUriFromTrie(uri);
        }
    }

}
