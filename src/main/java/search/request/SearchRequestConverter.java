package search.request;

public interface SearchRequestConverter {

    SearchRequest buildFromString(String input);
    String convertToString(SearchRequest searchRequest);

}
