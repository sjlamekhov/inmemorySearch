package sharding.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import networking.Message;
import objects.AbstractObject;
import objects.AbstractObjectUri;
import objects.Document;
import objects.DocumentUri;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import search.ConditionType;
import search.request.SearchRequest;
import search.request.SearchRequestConverter;
import search.request.SearchRequestStringConverter;

import java.util.*;
import java.util.function.Consumer;

public class RestSearchClient<U extends AbstractObjectUri, T extends AbstractObject> extends SearchClient<U, T> {

    private final SearchRequestConverter searchRequestConverter;
    private final List<String> clusterNodes;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final HttpHeaders headers;

    public RestSearchClient(List<String> clusterNodes) {
        searchRequestConverter = new SearchRequestStringConverter();
        this.clusterNodes = new ArrayList<>(clusterNodes);
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
        this.headers = buildDefaultHeader();
    }

    @Override
    public Collection<U> executeSearchRequest(String tenantId, SearchRequest searchRequest) {
        String convertedRequest = searchRequestConverter.convertToString(searchRequest);
        String nodeToRequest = clusterNodes.stream().findAny().orElse(null);
        if (null == nodeToRequest) {
            return Collections.emptySet();
        }
        String uri = String.format("http://%s/%s/search?request=%s", nodeToRequest, tenantId, convertedRequest);

        String response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        try {
            return mapper.readValue(response, new TypeReference<Collection<DocumentUri>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    @Override
    public void executeSearchRequestAsync(String tenantId, SearchRequest searchRequest, Consumer<Collection<U>> consumer) {
        consumer.accept(executeSearchRequest(tenantId, searchRequest));
    }

    private HttpHeaders buildDefaultHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
