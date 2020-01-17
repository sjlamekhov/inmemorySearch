package search.optimizer;

import search.SearchRequest;

public interface SearchRequestOptimizer {

    SearchRequest optimize(SearchRequest input);

}
