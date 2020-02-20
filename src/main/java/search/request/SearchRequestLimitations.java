package search.request;

public class SearchRequestLimitations {

    private int maxDepth;
    private int maxSize;

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

    //TODO: implement
    public boolean checkDepth(SearchRequest searchRequest) {
        return true;
    }

    //TODO: implement
    public boolean checkSize(SearchRequest searchRequest) {
        return true;
    }
}
