package platform.dump;

import com.google.common.collect.AbstractIterator;
import objects.AbstractObject;
import org.springframework.core.task.TaskExecutor;

import java.util.HashMap;
import java.util.Map;

public class DumpService {

    Map<String, DumpContext> iterators;
    TaskExecutor taskExecutor;

    public DumpService(TaskExecutor taskExecutor) {
        this.iterators = new HashMap<>();
        this.taskExecutor = taskExecutor;
    }



}
