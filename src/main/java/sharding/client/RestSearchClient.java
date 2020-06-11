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
            return mapper.readValue(response, new TypeReference<Collection<DocumentUri>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    @Override
    public void executeSearchRequestAsync(String tenantId, SearchRequest searchRequest, Consumer<Collection<U>> consumer) {
        consumer.accept(executeSearchRequest(tenantId, searchRequest));
    }

    @Override
    public T getObjectByUriRequest(U uri) {
        Objects.requireNonNull(uri);
        for (String clusterNode : clusterNodes) {
            T localResult = getObjectByUriRequestInternal(uri.getId(), uri.getTenantId(), clusterNode);
            if (null != localResult) {
                return localResult;
            }
        }
        return null;
    }

    @Override
    public Map<List<String>, Collection<U>> searchNearestDocuments(T object) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(object.getUri());
        String tenantId = object.getUri().getTenantId();
        Map<List<String>, Collection<U>> result = new HashMap<>();
        for (String clusterNode : clusterNodes) {
            String uri = String.format("http://%s/%s/search/nearest", clusterNode, tenantId);
            Map<List<String>, Collection<U>> response = (Map<List<String>, Collection<U>>) restTemplate.postForEntity(uri, object, Map.class);
            mergeMapWithColectionsResults(result, response);
        }
        return result;
    }

    private <A, B> void mergeMapWithColectionsResults(Map<A, Collection<B>> target, Map<A, Collection<B>> source) {
        for (Map.Entry<A, Collection<B>> entry : source.entrySet()) {
            target
                    .computeIfAbsent(entry.getKey(), i -> new HashSet<>())
                    .addAll(entry.getValue());
        }
    }

    @Override
    public void removeObjectFromIndex(T object) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(object.getUri());
        String tenantId = object.getUri().getTenantId();
        String id = object.getUri().getId();
        for (String clusterNode : clusterNodes) {
            String uri = String.format("http://%s/%s/search/remove/%s", clusterNode, tenantId, id);
            restTemplate.exchange(uri, HttpMethod.DELETE, new HttpEntity<>(headers), String.class).getBody();
        }
    }

    private T getObjectByUriRequestInternal(String id, String tenantId, String nodeToRequest) {
        String uri = String.format("http://%s/%s/search/getById/%s", nodeToRequest, tenantId, id);

        String response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        try {
            return mapper.readValue(response, new TypeReference<Collection<DocumentUri>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private HttpHeaders buildDefaultHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
