package platform.dump;

import objects.AbstractObject;
import objects.AbstractObjectUri;
import org.springframework.core.task.TaskExecutor;
import search.SearchService;

import java.util.*;
import java.util.function.Consumer;

public class DumpService<U extends AbstractObjectUri, T extends AbstractObject> {

    //dumpProcessId -> DumpContext
    Map<String, DumpContext<T>> dumpContexts;
    TaskExecutor taskExecutor;
    private final SearchService<U, T> searchService;

    public DumpService(TaskExecutor taskExecutor, SearchService<U, T> searchService) {
        this.dumpContexts = new HashMap<>();
        this.taskExecutor = taskExecutor;
        this.searchService = searchService;
    }

    public DumpContext<T> addAndStartNewTask(String tenantId, int maxSize, Consumer<T> consumer) {
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

}
