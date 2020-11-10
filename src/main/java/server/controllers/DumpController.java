package server.controllers;

import dump.DumpContext;
import dump.DumpService;
import objects.Document;
import objects.DocumentUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DumpController {

    @Autowired
    private DumpService<DocumentUri, Document> dumpService;

    @RequestMapping(value = "/dump/statistics",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Map<String, Object> getStatistics() {
        return dumpService.getStatistics();
    }

    @RequestMapping(value = "/dump/{contextId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public DumpContext<Document> getContextById(@PathVariable("contextId") String contextId) {
        return dumpService.getContextByDumpProcessId(contextId);
    }

    @RequestMapping(value = "/dump/{contextId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public DumpContext<Document> deleteContextById(@PathVariable("contextId") String contextId) {
        return dumpService.deleteContextById(contextId);
    }


}
