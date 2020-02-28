package benchmarks.searchService;

import objects.Document;
import objects.DocumentUri;
import search.SearchService;

import java.util.*;

public class BaseSearchExecutionPlan {

    private List<Document> data;
    protected final static String tenantId = "tenantId";
    protected final static int documentCount = 4096;

    public BaseSearchExecutionPlan() {
        data = new ArrayList<>();
        for (int i = 0; i < documentCount; i++) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("attribute" + i, "value" + i);
            data.add(new Document(new DocumentUri(tenantId), attributes));
        }
    }

    protected Iterator<Document> getDataIterator() {
        return data.iterator();
    }

    protected void initSearchService(SearchService<DocumentUri, Document> searchService) {
        Iterator<Document> dataIterator = getDataIterator();
        while (dataIterator.hasNext()) {
            searchService.addObjectToIndex(dataIterator.next());
        }
    }

    public static String getTenantId() {
        return tenantId;
    }

    public static int getDocumentCount() {
        return documentCount;
    }

}
