package search;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import objects.DocumentUri;
import org.junit.Assert;
import org.junit.Test;
import search.inmemory.InMemorySearchService;

import static org.junit.Assert.fail;

public class CompositeSearchTest {

    private static final String registeredTenant = "testTenant";
    private static final String unRegisteredTenant = "notTestTenant";

    private CompositeSearch<DocumentUri, Document> getCompositeSearch() {
        CompositeSearch<DocumentUri, Document> compositeSearch = new CompositeSearch<>();
        compositeSearch.addService(registeredTenant, new InMemorySearchService<>());
        return compositeSearch;
    }

    @Test
    public void addAlreadyRegisteredService() {
        CompositeSearch<DocumentUri, Document> compositeSearch = getCompositeSearch();
        try {
            compositeSearch.addService(registeredTenant, new InMemorySearchService<>());
            fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getClass().equals(RuntimeException.class));
        }
    }

    @Test
    public void addNotYetRegisteredService() {
        CompositeSearch<DocumentUri, Document> compositeSearch = getCompositeSearch();
        compositeSearch.addService(unRegisteredTenant, new InMemorySearchService<>());
        try {
            compositeSearch.addService(unRegisteredTenant, new InMemorySearchService<>());
            fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getClass().equals(RuntimeException.class));
        }
    }
}
