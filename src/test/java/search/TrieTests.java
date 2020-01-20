package search;

import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import search.inmemory.Trie;


import java.util.Arrays;
import java.util.Collection;

public class TrieTests {

    private final String tenantId = "testTenantId";

    @Test
    public void addToTrieAndGet() {
        Trie<DocumentUri> trie = new Trie<>();

        DocumentUri documentUriVal = new DocumentUri(tenantId);
        trie.addValueAndUri("val", documentUriVal);

        DocumentUri documentUriValue = new DocumentUri(tenantId);
        trie.addValueAndUri("value", documentUriValue);

        Collection<DocumentUri> searchResult = trie.getUrisByStartsWith("");
        Assert.assertEquals(0, searchResult.size());

        searchResult = trie.getUrisByStartsWith("notExistingValue");
        Assert.assertEquals(0, searchResult.size());

        searchResult = trie.getUrisByStartsWith("v");
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUriVal, documentUriValue)));

        searchResult = trie.getUrisByStartsWith("val");
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUriVal, documentUriValue)));

        searchResult = trie.getUrisByStartsWith("value");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUriValue));
    }

}
