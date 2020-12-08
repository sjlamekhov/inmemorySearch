package server.controllers;

import dump.DumpContext;
import dump.DumpService;
import dump.consumers.AbstractObjectConsumer;
import dump.consumers.FileConsumer;
import dump.consumers.InMemoryConsumer;
import objects.Document;
import objects.DocumentUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@RestController
public class DumpController {

    @Autowired
    private DumpService<DocumentUri, Document> dumpService;

    @RequestMapping(value = "/dump/statistics",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Map<String, Object> getStatistics() {
        return dumpService.getStatistics();
    }

    @RequestMapping(value = "/dump/{tenantId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public DumpContext startDumping(@PathVariable("tenantId") String tenantId,
                                    @RequestParam(value = "request", required = false) Integer maxSize,
                                    @RequestParam(value = "consumerType", required = true) String consumerType,
                                    @RequestParam(value = "consumerPath", required = false) String consumerPath) {
        if (null == consumerType
                || (Objects.equals("fileconsumer", consumerType.toLowerCase()) && null == consumerPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Wrong dump parameters, consumerType=%s, consumerPath=%s", consumerType, consumerPath));
        }
        AbstractObjectConsumer<Document> documentConsumer = Objects.equals("fileconsumer", consumerType.toLowerCase()) ?
                new FileConsumer<>(consumerPath)
                : new InMemoryConsumer<>(new ArrayList<>());    //for fast export
        //TODO: implement dump by specific search request
        return dumpService.addAndStartNewTask("tenantId", maxSize, documentConsumer);
    }

    @RequestMapping(value = "/dump/{contextId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public DumpContext getContextById(@PathVariable("contextId") String contextId) {
        return dumpService.getContextByDumpProcessId(contextId);
    }

    @RequestMapping(value = "/dump/{contextId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public DumpContext deleteContextById(@PathVariable("contextId") String contextId) {
        return dumpService.deleteContextById(contextId);
    }


}
