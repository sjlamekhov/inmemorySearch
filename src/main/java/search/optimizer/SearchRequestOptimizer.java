package sdk.search.optimizer;

import sdk.search.SearchRequest;

public interface SearchRequestOptimizer {

    SearchRequest optimize(SearchRequest input);

}
