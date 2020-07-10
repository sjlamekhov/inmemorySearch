package configuration;

import java.util.*;

public class ConfigurationService {

    public enum OperationMode {
        reliability,
        sharding
    }

    private List<String> tenants;
    private int gossipServerPort;
    private List<String> clusterNodes;
    private int maxSearchRequestDepth;
    private int maxSearchRequestSize;
    private OperationMode operationalMode;
    private boolean useCache;
    private int maxUriLength;

    private ConfigurationService() {
    }

    public List<String> getTenants() {
        return Collections.unmodifiableList(tenants);
    }

    public int getGossipServerPort() {
        return gossipServerPort;
    }

    public int getMaxSearchRequestDepth() {
        return maxSearchRequestDepth;
    }

    public int getMaxSearchRequestSize() {
        return maxSearchRequestSize;
    }

    public List<String> getClusterNodes() {
        return Collections.unmodifiableList(clusterNodes);
    }

    public OperationMode getOperationalMode() {
        return operationalMode;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public int getMaxUriLength() {
        return maxUriLength;
    }

    public static ConfigurationService buildConfigurationService(Properties properties) {
        ConfigurationService result = new ConfigurationService();

        String tenantsFromProperties = properties.getProperty(ConfigurationPropertiesConstants.TENANTS);
        result.tenants = tenantsFromProperties != null ? new ArrayList<>(Arrays.asList(tenantsFromProperties.split(","))) : Collections.emptyList();

        result.gossipServerPort = Integer.valueOf(properties.getProperty(ConfigurationPropertiesConstants.GOSSIP_SERVER_PORT, "6060"));

        String clusterNodesFromProperties = properties.getProperty(ConfigurationPropertiesConstants.CLUSTER_NODES);
        result.clusterNodes = clusterNodesFromProperties != null ? new ArrayList<>(Arrays.asList(clusterNodesFromProperties.split(","))) : Collections.emptyList();

        result.maxSearchRequestDepth = Integer.valueOf(properties.getProperty(ConfigurationPropertiesConstants.MAX_SEARCH_REQUEST_DEPTH, "8"));

        result.maxSearchRequestSize = Integer.valueOf(properties.getProperty(ConfigurationPropertiesConstants.MAX_SEARCH_REQUEST_SIZE, "8"));

        result.operationalMode = OperationMode.valueOf(properties.getProperty(ConfigurationPropertiesConstants.OPERATIONAL_MODE, ConfigurationPropertiesConstants.RELIABILITY));

        result.useCache = Boolean.valueOf(properties.getProperty(ConfigurationPropertiesConstants.USE_CACHE, "false"));

        result.maxUriLength = Integer.valueOf(properties.getProperty(ConfigurationPropertiesConstants.MAX_URI_LENGTH, "12"));

        return result;
    }

}
