package benchmarks.searchService;

import objects.Document;
import objects.DocumentUri;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import search.ConditionType;
import search.SearchService;
import search.request.SearchRequest;

import java.util.*;
import java.util.function.Function;

@State(Scope.Benchmark)
public class SearchServiceBenchmarks {

    private static final Function<Integer, SearchRequest> searchRequestWithCompatibleNested = (documentCount) -> SearchRequest.Builder.newInstance()
            .setAttributeToSearch("attribute" + documentCount)
            .setConditionType(ConditionType.EQ)
            .setValueToSearch("value" + documentCount)
            .and(Collections.singleton(
                    SearchRequest.Builder.newInstance()
                            .setAttributeToSearch("attribute" + documentCount + 1)
                            .setConditionType(ConditionType.EQ)
                            .setValueToSearch("value" + documentCount + 1)
                            .build()))
            .build();

    private static final Function<Integer, SearchRequest> searchRequestWithIncompatibleNested = (documentCount) -> SearchRequest.Builder.newInstance()
            .setAttributeToSearch("attribute" + documentCount)
            .setConditionType(ConditionType.EQ)
            .setValueToSearch("value" + documentCount)
            .and(Collections.singleton(
                    SearchRequest.Builder.newInstance()
                            .setAttributeToSearch("attribute" + documentCount)
                            .setConditionType(ConditionType.EQ)
                            .setValueToSearch("value" + documentCount + "8BADF00D")
                            .build()))
            .build();

    @Benchmark
    public void doSearchInMemory(InmemorySearchExecutionPlan executionPlan) {
        searchTest(
                executionPlan.searchService,
                InmemorySearchExecutionPlan.getTenantId(),
                searchRequestWithCompatibleNested,
                InmemorySearchExecutionPlan.documentCount
        );
    }

    @Benchmark
    public void doSearchCachedWithOptimizer(CachedWithOptimizerSearchExecutionPlan executionPlan) {
        searchTest(
                executionPlan.searchService,
                CachedWithOptimizerSearchExecutionPlan.getTenantId(),
                searchRequestWithCompatibleNested,
                CachedWithOptimizerSearchExecutionPlan.getDocumentCount()
        );
    }

    @Benchmark
    public void doSearchCachedNoOptimizer(CachedNoOptimizerSearchExecutionPlan executionPlan) {
        searchTest(
                executionPlan.searchService,
                CachedWithOptimizerSearchExecutionPlan.getTenantId(),
                searchRequestWithCompatibleNested,
                CachedWithOptimizerSearchExecutionPlan.getDocumentCount()
        );
    }

    @Benchmark
    public void doSearchInMemoryIncompatible(InmemorySearchExecutionPlan executionPlan) {
        searchTest(
                executionPlan.searchService,
                InmemorySearchExecutionPlan.getTenantId(),
                searchRequestWithIncompatibleNested,
                InmemorySearchExecutionPlan.documentCount
        );
    }

    @Benchmark
    public void doSearchCachedWithOptimizerIncompatible(CachedWithOptimizerSearchExecutionPlan executionPlan) {
        searchTest(
                executionPlan.searchService,
                CachedWithOptimizerSearchExecutionPlan.getTenantId(),
                searchRequestWithIncompatibleNested,
                CachedWithOptimizerSearchExecutionPlan.getDocumentCount()
        );
    }

    @Benchmark
    public void doSearchCachedNoOptimizerIncompatible(CachedNoOptimizerSearchExecutionPlan executionPlan) {
        searchTest(
                executionPlan.searchService,
                CachedWithOptimizerSearchExecutionPlan.getTenantId(),
                searchRequestWithIncompatibleNested,
                CachedWithOptimizerSearchExecutionPlan.getDocumentCount()
        );
    }

    private void searchTest(SearchService<DocumentUri, Document> searchService,
                                             String tenantId,
                                             Function<Integer, SearchRequest> searchRequest,
                                             int documentCount) {
        int indexToSearch = RandomUtils.nextInt(0, documentCount / 128);
        searchService.search(tenantId, searchRequest.apply(indexToSearch));
    }

}
