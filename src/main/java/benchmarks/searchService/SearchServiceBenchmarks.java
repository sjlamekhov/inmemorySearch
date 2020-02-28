package benchmarks.searchService;

import objects.Document;
import objects.DocumentUri;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import search.ConditionType;
import search.SearchService;
import search.request.SearchRequest;

import java.util.Collections;

@State(Scope.Benchmark)
public class SearchServiceBenchmarks {

    @Benchmark
    public void doSearchInMemory(InmemorySearchExecutionPlan executionPlan) {
        test(
                executionPlan.searchService,
                InmemorySearchExecutionPlan.getTenantId(),
                InmemorySearchExecutionPlan.documentCount
        );
    }

    @Benchmark
    public void doSearchCachedWithOptimizer(CachedWithOptimizerSearchExecutionPlan executionPlan) {
        test(
                executionPlan.searchService,
                CachedWithOptimizerSearchExecutionPlan.getTenantId(),
                CachedWithOptimizerSearchExecutionPlan.getDocumentCount()
        );
    }

    @Benchmark
    public void doSearchCachedNoOptimizer(CachedNoOptimizerSearchExecutionPlan executionPlan) {
        test(
                executionPlan.searchService,
                CachedWithOptimizerSearchExecutionPlan.getTenantId(),
                CachedWithOptimizerSearchExecutionPlan.getDocumentCount()
        );
    }

    private void test(SearchService<DocumentUri, Document> searchService,
                      String tenantId,
                      int documentCount) {
        int toSearch = RandomUtils.nextInt(0, documentCount / 128);
        searchService.search(tenantId, SearchRequest.Builder.newInstance()
                .setAttributeToSearch("attribute" + toSearch)
                .setConditionType(ConditionType.EQ)
                .setValueToSearch("value" + toSearch)
                .and(Collections.singleton(
                        SearchRequest.Builder.newInstance()
                                .setAttributeToSearch("attribute" + toSearch + 1)
                                .setConditionType(ConditionType.EQ)
                                .setValueToSearch("value" + toSearch + 1)
                                .build()))
                .build());
    }

}
