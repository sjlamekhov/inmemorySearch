package dump;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import org.springframework.core.task.TaskExecutor;
import dump.consumers.AbstractObjectConsumer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import search.SearchService;

import java.util.*;

import static dump.DumpServiceConstants.ACTIVE_DUMP_THREAD_COUNT;
import static dump.DumpServiceConstants.NUMBER_OF_CONTEXTS;
import static dump.DumpServiceConstants.POOL_SIZE;

public class DumpService<U extends AbstractObjectUri, T extends AbstractObject> {

    //dumpProcessId -> DumpContext
    Map<String, DumpContext<T>> dumpContexts;
    ThreadPoolTaskExecutor taskExecutor;
    private final SearchService<U, T> searchService;

    public DumpService(ThreadPoolTaskExecutor taskExecutor, SearchService<U, T> searchService) {
        this.dumpContexts = new HashMap<>();
        this.taskExecutor = taskExecutor;
        this.searchService = searchService;
    }

    public DumpContext<T> addAndStartNewTask(String tenantId, int maxSize, AbstractObjectConsumer consumer) {
        String dumpProcessId = UUID.randomUUID().toString();
        DumpContext<T> dumpContext = new DumpContext<>(dumpProcessId, System.currentTimeMillis(), consumer);
        dumpContexts.put(dumpProcessId, dumpContext);
        taskExecutor.execute(() -> {
            searchService.extractObjectsByIterator(tenantId, null, maxSize, consumer);
            dumpContext.finish();
        });
        return dumpContext;
    }

    public DumpContext<T> getContextByDumpProcessId(String dumpProcessId) {
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

    public DumpContext<T> deleteContextById(String dumpProcessId) {
        DumpContext<T> beforeDeletion = dumpContexts.get(dumpProcessId);
        if (null == beforeDeletion) {
            return null;
        }
        dumpContexts.remove(dumpProcessId);
        return beforeDeletion;
    }

}
