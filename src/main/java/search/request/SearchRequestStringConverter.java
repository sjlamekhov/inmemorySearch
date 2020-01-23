package search.request;


public class SearchRequestStringConverter implements  SearchRequestConverter {

    private static final String AND = "and";
    private static final String OR = "or";

    @Override
    public SearchRequest buildFromString(String input) {
        return null;
    }

    @Override
    public String convertToString(SearchRequest searchRequest) {
        if (null == searchRequest) {
            return null;
        }
        return convertCondition(searchRequest);
    }

    private static String convertCondition(SearchRequest searchRequest) {
        StringBuilder result = new StringBuilder();
        if (null != searchRequest.getAndRequests() && !searchRequest.getAndRequests().isEmpty()) {
            result.append("(");
            result.append(convertSingleCondition(searchRequest));
            result.append(AND);
            String converted = searchRequest.getAndRequests().stream()
                    .map(SearchRequestStringConverter::convertCondition)
                    .reduce((i1, i2) -> i1 + AND + i2)
                    .orElse("");
            result.append(converted);
            result.append(")");
        } else if (null != searchRequest.getOrRequests() && !searchRequest.getOrRequests().isEmpty()) {
            result.append("(");
            result.append(convertSingleCondition(searchRequest));
            result.append(OR);
            String converted = searchRequest.getOrRequests().stream()
                    .map(SearchRequestStringConverter::convertCondition)
                    .reduce((i1, i2) -> i1 + OR + i2)
                    .orElse("");
            result.append(converted);
            result.append(")");
        } else {
            return convertSingleCondition(searchRequest);
        }
        return result.toString();
    }

    private static String convertSingleCondition(SearchRequest searchRequest) {
        return String.format("(%s,%s,%s)",
                searchRequest.getAttributeToSearch(),
                searchRequest.getConditionType(),
                searchRequest.getValueToSearch());
    }
}
