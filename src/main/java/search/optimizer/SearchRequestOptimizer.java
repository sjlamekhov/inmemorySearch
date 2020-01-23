package search.optimizer;

import search.request.SearchRequest;

public interface SearchRequestOptimizer {

    SearchRequest optimize(SearchRequest input);

}
