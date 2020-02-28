package search.optimizer;

import search.request.SearchRequest;

public class TransparentRequestOptimizer implements SearchRequestOptimizer {

    @Override
    public SearchRequest optimize(SearchRequest input) {
        return input;
    }

}
