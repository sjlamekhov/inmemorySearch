package search.request;

public class SearchRequestLimitations {

    private final int maxDepth;
    private final int maxSize;

    public SearchRequestLimitations(int maxDepth, int maxSize) {
        this.maxDepth = maxDepth;
        this.maxSize = maxSize;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean checkDepth(SearchRequest searchRequest) {
        return getDepthInternal(searchRequest, 0) <= maxDepth;
    }

    private static int getDepthInternal(SearchRequest searchRequest, int currentLevel) {
        if (null == searchRequest) {
            return currentLevel;
        }
        if (searchRequest.getOrRequests().isEmpty() && searchRequest.getAndRequests().isEmpty()) {
            return currentLevel + 1;
        }
        int maxOrDepth = -1;
        for (SearchRequest orItem : searchRequest.getOrRequests()) {
            maxOrDepth = Math.max(maxOrDepth, getDepthInternal(orItem, currentLevel + 1));
        }
        int maxAndDepth = -1;
        for (SearchRequest andItem : searchRequest.getAndRequests()) {
            maxAndDepth = Math.max(maxAndDepth, getDepthInternal(andItem, currentLevel + 1));
        }
        return Math.max(maxOrDepth, maxAndDepth);
    }

    public boolean checkSize(SearchRequest searchRequest) {
        return getSizeInternal(searchRequest, 0) <= maxSize;
    }

    private static int getSizeInternal(SearchRequest searchRequest, int currentSize) {
        if (null == searchRequest) {
            return currentSize;
        }
        int result = currentSize + 1;
        for (SearchRequest orItem : searchRequest.getOrRequests()) {
            result += getSizeInternal(orItem, currentSize);
        }
        for (SearchRequest andItem : searchRequest.getAndRequests()) {
            result += getSizeInternal(andItem, currentSize);
        }
        return result;
    }
}
