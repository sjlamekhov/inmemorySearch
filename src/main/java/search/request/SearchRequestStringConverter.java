package search.request;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import search.ConditionType;

import java.util.ArrayList;
import java.util.List;

import static search.request.ConverterUtils.skipN;
import static search.request.ConverterUtils.skipParenthesis;
import static search.request.ConverterUtils.stripSearchRequestString;

public class SearchRequestStringConverter implements SearchRequestConverter {

    private static final String AND = "and";
    private static final String OR = "or";

    @Override
    public SearchRequest buildFromString(String input) {
        if (null == input || input.isEmpty()) {
            return null;
        }
        return convertFromStringInternal(stripSearchRequestString(input)).getLeft();
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

    private static SearchRequest convertFromSingleCondition(String input) {
        String skipped = skipParenthesis(input);
        String[] splitted = skipped.split(",");
        return SearchRequest.Builder.newInstance()
                .setAttributeToSearch(splitted[0])
                .setConditionType(ConditionType.valueOf(splitted[1]))
                .setValueToSearch(splitted[2])
                .build();
    }

    //receives stripped request
    private static Pair<SearchRequest, Integer> convertFromStringInternal(String request) {
        if (null == request || request.isEmpty()) {
            return new ImmutablePair<>(null, 0);
        }
        int rightBorder = ConverterUtils.findConditionBorder(request);
        if (-1 == rightBorder) {
            return null;
        }
        if (request.length() == rightBorder) {
            return new ImmutablePair<>(convertFromSingleCondition(request), request.length());
        } else {
            SearchRequest mainSearchRequest = convertFromSingleCondition(request.substring(0, rightBorder));
            String restPart = request.substring(rightBorder);
            List<SearchRequest> toAppend = new ArrayList<>();
            ConverterUtils.RequestType requestType = null;
            if (restPart.startsWith("and")) {
                requestType = ConverterUtils.RequestType.AND_COMPLEX_REQUEST;
            } else if (restPart.startsWith("or")) {
                requestType = ConverterUtils.RequestType.OR_COMPLEX_REQUEST;
            } else {
                return new ImmutablePair<>(mainSearchRequest, request.length());
            }

            String newInput = skipN(restPart, (requestType.equals(ConverterUtils.RequestType.AND_COMPLEX_REQUEST) ? "and" : "or").length());
            while (true) {
                Pair<SearchRequest, Integer> tmpPair = convertFromStringInternal(stripSearchRequestString(newInput));
                SearchRequest tmpSearchRequest = tmpPair.getLeft();
                if (null != tmpSearchRequest) {
                    toAppend.add(tmpSearchRequest);
                } else {
                    break;
                }
                newInput = skipN(newInput, tmpPair.getRight());
            }
            if (requestType.equals(ConverterUtils.RequestType.AND_COMPLEX_REQUEST)) {
                mainSearchRequest.getAndRequests().addAll(toAppend);
            } else {
                mainSearchRequest.getOrRequests().addAll(toAppend);
            }
            return new ImmutablePair<>(mainSearchRequest, request.length());

        }
    }

    public static void main(String[] args) {
        System.out.println(convertFromStringInternal("(attribute1,EQ,value1)and((attribute2,LT,value2)and(attribute3,LT,value3))").getLeft());
        System.out.println(convertFromStringInternal("(attribute1,EQ,value1)or((attribute2,LT,value2)or(attribute3,LT,value3))").getLeft());
    }

}
