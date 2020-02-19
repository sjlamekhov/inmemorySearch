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

import java.util.Collection;

@RestController
public class SearchController {

    @Autowired
    private SearchService<DocumentUri, Document> searchService;

    private SearchRequestConverter searchRequestConverter = new SearchRequestStringConverter();

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

    @RequestMapping(value = "/{tenantId}/search/index",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Collection<DocumentUri> searchDocument(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "request", required = false) String request) {
        SearchRequest searchRequest = searchRequestConverter.buildFromString(request);
        if (null == searchRequest) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search request can't be parsed");
        }
        return searchService.search(tenantId, searchRequest);
    }

}
