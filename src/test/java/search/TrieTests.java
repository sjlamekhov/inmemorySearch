package search;

import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import search.inmemory.Trie;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class TrieTests {

    private final String tenantId = "testTenantId";

    @Test
    public void addToTrieAndGet() {
        Trie<DocumentUri> trie = new Trie<>();

        DocumentUri documentUriVal = new DocumentUri("documentUriVal", tenantId);
        trie.addValueAndUri("val", documentUriVal);

        DocumentUri documentUriValue = new DocumentUri("documentUriValue", tenantId);
        trie.addValueAndUri("value", documentUriValue);

        DocumentUri documentUriValhalla = new DocumentUri("documentUriValhalla", tenantId);
        trie.addValueAndUri("valhalla", documentUriValhalla);

        Collection<DocumentUri> searchResult = trie.getUrisByStartsWith("");
        Assert.assertEquals(0, searchResult.size());

        searchResult = trie.getUrisByStartsWith("notExistingValue");
        Assert.assertEquals(0, searchResult.size());

        searchResult = trie.getUrisByStartsWith("v");
        Assert.assertEquals(3, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUriVal, documentUriValue, documentUriValhalla)));

        searchResult = trie.getUrisByStartsWith("val");
        Assert.assertEquals(3, searchResult.size());
        Assert.assertTrue(searchResult.containsAll(Arrays.asList(documentUriVal, documentUriValue, documentUriValue)));

        searchResult = trie.getUrisByStartsWith("valhalla");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUriValhalla));

        searchResult = trie.getUrisByStartsWith("value");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUriValue));
    }

    @Test
    public void wipeAndGet() {
        Trie<DocumentUri> trie = new Trie<>();

        DocumentUri documentUriVal = new DocumentUri("documentUriVal", tenantId);
        trie.addValueAndUri("val", documentUriVal);

        DocumentUri documentUriValue = new DocumentUri("documentUriValue", tenantId);
        trie.addValueAndUri("value", documentUriValue);

        //ended value
        Collection<DocumentUri> searchResult = trie.getUrisByStartsWith("value");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUriValue));
        trie.removeUriFromTrie(documentUriValue);
        searchResult = trie.getUrisByStartsWith("value");
        Assert.assertEquals(0, searchResult.size());

        //middle value
        searchResult = trie.getUrisByStartsWith("val");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(documentUriVal));
        trie.removeUriFromTrie(documentUriVal);
        searchResult = trie.getUrisByStartsWith("val");
        Assert.assertEquals(0, searchResult.size());
    }

}
