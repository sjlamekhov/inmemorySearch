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
import search.request.SearchRequest;
import search.request.SearchRequestConverter;
import search.request.SearchRequestLimitations;
import search.request.SearchRequestStringConverter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@RestController
public class DumpController {

    @Autowired
    private DumpService<DocumentUri, Document> dumpService;

    @Autowired
    private SearchRequestLimitations searchRequestLimitations;

    private SearchRequestConverter searchRequestConverter = new SearchRequestStringConverter();

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
                                    @RequestParam(value = "maxSize", required = false) Integer maxSize,
                                    @RequestParam(value = "consumerType", required = true) String consumerType,
                                    @RequestParam(value = "consumerPath", required = false) String consumerPath,
                                    @RequestParam(value = "request", required = false) String request) {
        SearchRequest searchRequest = searchRequestConverter.buildFromString(request);
        if (!searchRequestLimitations.checkDepth(searchRequest) || !searchRequestLimitations.checkSize(searchRequest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search request does not pass depth or size check");
        }
        if (null == consumerType
                || (Objects.equals("fileconsumer", consumerType.toLowerCase()) && null == consumerPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Wrong dump parameters, consumerType=%s, consumerPath=%s", consumerType, consumerPath));
        }
        AbstractObjectConsumer<Document> documentConsumer = Objects.equals("fileconsumer", consumerType.toLowerCase()) ?
                new FileConsumer<>(consumerPath)
                : new InMemoryConsumer<>(new ArrayList<>());    //for fast export
        return dumpService.addAndStartNewTask(tenantId, searchRequest, maxSize, documentConsumer);
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
