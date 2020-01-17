package search;

import java.util.*;
import java.util.function.Function;

public class SearchRequest {

    private final Set<SearchRequest> andRequests;
    private final Set<SearchRequest> orRequests;
    private final ConditionType conditionType;
    private final String attributeToSearch;
    private final String valueToSearch;

    private final static Function<SearchRequest, String> requestStringer = r ->
            String.format("%s@=@%s@=@%s", r.getAttributeToSearch(), r.getConditionType(), r.getValueToSearch());

    private final static Comparator<? super SearchRequest> requestComparator = (Comparator<SearchRequest>) (o1, o2) -> requestStringer.apply(o1).compareTo(requestStringer.apply(o2));

    private SearchRequest(ConditionType conditionType, String attributeToSearch, String valueToSearch,
                          Set<SearchRequest> andRequests, Set<SearchRequest> orRequests) {
        this.conditionType = conditionType;
        this.attributeToSearch = attributeToSearch;
        this.valueToSearch = valueToSearch;
        this.andRequests = new HashSet<>(andRequests);
        this.orRequests = new HashSet<>(orRequests);
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public String getValueToSearch() {
        return valueToSearch;
    }

    public String getAttributeToSearch() {
        return attributeToSearch;
    }

    public Set<SearchRequest> getAndRequests() {
        return andRequests;
    }

    public Set<SearchRequest> getOrRequests() {
        return orRequests;
    }

    public static class Builder {

        private ConditionType conditionType;
        private String attributeToSearch;
        private String valueToSearch;
        private Set<SearchRequest> andRequests;
        private Set<SearchRequest> orRequests;

        protected Builder() {
            this.andRequests = new HashSet<>();
            this.orRequests = new HashSet<>();
        }

        public Builder setConditionType(ConditionType conditionType) {
            this.conditionType = conditionType;
            return this;
        }

        public Builder setAttributeToSearch(String attributeToSearch) {
            this.attributeToSearch = attributeToSearch;
            return this;
        }

        public Builder setValueToSearch(String valueToSearch) {
            this.valueToSearch = valueToSearch;
            return this;
        }

        public Builder and(SearchRequest searchRequest) {
            this.andRequests.add(searchRequest);
            return this;
        }

        public Builder and(Collection<SearchRequest> searchRequests) {
            this.andRequests.addAll(searchRequests);
            return this;
        }

        public Builder or(SearchRequest searchRequest) {
            this.orRequests.add(searchRequest);
            return this;
        }

        public Builder or(Collection<SearchRequest> searchRequests) {
            this.orRequests.addAll(searchRequests);
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public SearchRequest build() {
            return new SearchRequest(
                    conditionType,
                    attributeToSearch,
                    valueToSearch,
                    andRequests,
                    orRequests
            );
        }
    }

    @Override
    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        if (!other.getClass().equals(SearchRequest.class)) {
            return false;
        }
        SearchRequest otherToCompare = (SearchRequest) other;
        return checkRecursively(this, otherToCompare);
    }

    @Override
    public int hashCode() {
        return requestStringer.apply(this).hashCode();
    }

    private static boolean checkRecursively(SearchRequest o1, SearchRequest o2) {
        if (null == o1 || null == o2
                || !Objects.equals(o1.getValueToSearch(), o2.getValueToSearch())
                || !Objects.equals(o1.getAttributeToSearch(), o2.getAttributeToSearch())
                || !Objects.equals(o1.getConditionType(), o2.getConditionType())
                || !Objects.equals(o1.getAndRequests().size(), o2.getAndRequests().size())
                || !Objects.equals(o1.getOrRequests().size(), o2.getOrRequests().size())) {
            return false;
        }
        if (!o1.getAndRequests().isEmpty() && !o2.getAndRequests().isEmpty()) {
            return compareRequestCollections(o1.getAndRequests(), o2.getAndRequests());
        } else if (!o1.getOrRequests().isEmpty() && !o2.getOrRequests().isEmpty()) {
            return compareRequestCollections(o1.getOrRequests(), o2.getOrRequests());
        }
        return true;
    }

    private static boolean compareRequestCollections(Collection<SearchRequest> o1, Collection<SearchRequest> o2) {
        List<SearchRequest> o1NestedRequests = new ArrayList<>(o1);
        List<SearchRequest> o2NestedRequests = new ArrayList<>(o2);
        o1NestedRequests.sort(requestComparator);
        o2NestedRequests.sort(requestComparator);
        if (o1NestedRequests.size() != o2NestedRequests.size()) {
            return false;
        }
        for (int i = 0; i < o1NestedRequests.size(); i++) {
            if (!checkRecursively(o1NestedRequests.get(i), o2NestedRequests.get(i))) {
                return false;
            }
        }
        return true;
    }


}
