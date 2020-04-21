package server.controllers;

import objects.Document;
import objects.DocumentUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import search.SearchService;
import search.request.SearchRequest;
import search.request.SearchRequestConverter;
import search.request.SearchRequestStringConverter;
import search.request.SearchRequestLimitations;

import java.util.Collection;

@RestController
public class SearchController {

    @Autowired
    private SearchService<DocumentUri, Document> searchService;

    @Autowired
    private SearchRequestLimitations searchRequestLimitations;

    private SearchRequestConverter searchRequestConverter = new SearchRequestStringConverter();

    @RequestMapping(value = "/{tenantId}/search/getById/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Document getByUri(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") String id) {
        return searchService.getObjectByUri(new DocumentUri(id, tenantId));
    }

    @RequestMapping(value = "/{tenantId}/search/index",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public void indexDocument(
            @PathVariable("tenantId") String tenantId,
            @RequestBody Document document) {
        searchService.addObjectToIndex(document);
    }

    @RequestMapping(value = "/{tenantId}/search/remove/{documentId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public void removeDocumentFromIndex(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("documentId") String documentId) {
        searchService.removeObjectFromIndex(new Document(new DocumentUri(documentId, tenantId)));
    }

    @RequestMapping(value = "/{tenantId}/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Collection<DocumentUri> searchDocument(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "request", required = false) String request) {
        SearchRequest searchRequest = searchRequestConverter.buildFromString(request);
        if (null == searchRequest) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search request can't be parsed");
        }
        if (!searchRequestLimitations.checkDepth(searchRequest) || !searchRequestLimitations.checkSize(searchRequest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search request does not pass depth or size check");
        }
        return searchService.search(tenantId, searchRequest);
    }

    @RequestMapping(value = "/{tenantId}/count",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public CountResponse countDocuments(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "request", required = false) String request) {
        SearchRequest searchRequest = searchRequestConverter.buildFromString(request);
        if (null == searchRequest) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search request can't be parsed");
        }
        if (!searchRequestLimitations.checkDepth(searchRequest) || !searchRequestLimitations.checkSize(searchRequest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search request does not pass depth or size check");
        }
        return new CountResponse(searchService.count(tenantId, searchRequest));
    }

    public class CountResponse {
        private long count;

        CountResponse(long count) {
            this.count = count;
        }

        public long getCount() {
            return count;
        }
    }

}
