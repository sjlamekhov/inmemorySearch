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

        String[] tenantsFromProperties = properties.getProperty(ConfigurationPropertiesConstants.TENANTS, "").split(",");
        result.tenants = new ArrayList<>(Arrays.asList(tenantsFromProperties));

        result.enableSync = Boolean.valueOf(properties.getProperty(ConfigurationPropertiesConstants.ENABLE_SYNC, "false"));

        result.serverPort = Integer.valueOf(properties.getProperty(ConfigurationPropertiesConstants.SERVER_PORT), 6060);

        String[] clusterNodes = properties.getProperty(ConfigurationPropertiesConstants.CLUSTER_NODES, "").split(",");
        result.clusterNodes = new ArrayList<>(Arrays.asList(clusterNodes));

        return result;
    }

}
