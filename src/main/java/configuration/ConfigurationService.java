package configuration;

import java.util.*;

public class ConfigurationService {

    private List<String> tenants;
    private boolean enableSync;
    private int serverPort;
    private List<String> clusterNodes;

    private ConfigurationService() {
    }

    public List<String> getTenants() {
        return Collections.unmodifiableList(tenants);
    }

    public boolean isEnableSync() {
        return enableSync;
    }

    public int getServerPort() {
        return serverPort;
    }

    public List<String> getClusterNodes() {
        return Collections.unmodifiableList(clusterNodes);
    }

    public static ConfigurationService buildConfigurationService(Properties properties) {
        ConfigurationService result = new ConfigurationService();

        String tenantsFromProperties = properties.getProperty(ConfigurationPropertiesConstants.TENANTS);
        result.tenants = tenantsFromProperties != null ? new ArrayList<>(Arrays.asList(tenantsFromProperties.split(","))) : Collections.emptyList();

        result.enableSync = Boolean.valueOf(properties.getProperty(ConfigurationPropertiesConstants.ENABLE_SYNC, "false"));

        result.serverPort = Integer.valueOf(properties.getProperty(ConfigurationPropertiesConstants.SERVER_PORT, "6060"));

        String clusterNodesFromProperties = properties.getProperty(ConfigurationPropertiesConstants.CLUSTER_NODES);
        result.clusterNodes = clusterNodesFromProperties != null ? new ArrayList<>(Arrays.asList(clusterNodesFromProperties.split(","))) : Collections.emptyList();

        return result;
    }

}
