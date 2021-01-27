package dump;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import dump.consumers.AbstractObjectConsumer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import search.SearchService;
import search.request.SearchRequest;

import java.util.*;

import static dump.DumpServiceConstants.ACTIVE_DUMP_THREAD_COUNT;
import static dump.DumpServiceConstants.NUMBER_OF_CONTEXTS;
import static dump.DumpServiceConstants.POOL_SIZE;

public class DumpService<U extends AbstractObjectUri, T extends AbstractObject> {

    //dumpProcessId -> DumpContext
    Map<String, DumpContext> dumpContexts;
    ThreadPoolTaskExecutor taskExecutor;
    private final SearchService<U, T> searchService;

    public DumpService(ThreadPoolTaskExecutor taskExecutor, SearchService<U, T> searchService) {
        this.dumpContexts = new HashMap<>();
        this.taskExecutor = taskExecutor;
        this.searchService = searchService;
    }

    public DumpContext addAndStartNewTask(String tenantId, SearchRequest searchRequest, int maxSize, AbstractObjectConsumer consumer) {
        String dumpProcessId = UUID.randomUUID().toString();
        DumpContext dumpContext = new DumpContext(dumpProcessId, System.currentTimeMillis(), consumer);
        dumpContexts.put(dumpProcessId, dumpContext);
        taskExecutor.execute(() -> {
            searchService.extractObjectsByIterator(tenantId, searchRequest,null, maxSize, consumer);
            dumpContext.finish();
        });
        return dumpContext;
    }

    public DumpContext getContextByDumpProcessId(String dumpProcessId) {
        return dumpContexts.get(dumpProcessId);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        result.put(NUMBER_OF_CONTEXTS, dumpContexts.size());
        result.put(ACTIVE_DUMP_THREAD_COUNT, taskExecutor.getActiveCount());
        result.put(POOL_SIZE, taskExecutor.getPoolSize());
        //add number of alive and finished dumps

        return result;
    }

    public DumpContext deleteContextById(String dumpProcessId) {
        DumpContext beforeDeletion = dumpContexts.get(dumpProcessId);
        if (null == beforeDeletion) {
            return null;
        }
        dumpContexts.remove(dumpProcessId);
        return beforeDeletion;
    }

}
