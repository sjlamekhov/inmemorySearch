package configuration;

import org.junit.Assert;
import org.junit.Test;
import utils.FileUtils;

import java.util.Arrays;

public class ConfigurationServiceTest {

    @Test
    public void testDefaults() {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationService(
                FileUtils.propertiesFromClasspath("configServiceTests/configServiceTestEmpty.properties")
        );
        Assert.assertTrue(configurationService.getClusterNodes().isEmpty());
        Assert.assertEquals(6060, configurationService.getGossipServerPort());
        Assert.assertTrue(configurationService.getTenants().isEmpty());
        Assert.assertEquals(ConfigurationService.OperationMode.reliability, configurationService.getOperationalMode());
        Assert.assertFalse(configurationService.isUseCache());
    }

    @Test
    public void test() {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationService(
                FileUtils.propertiesFromClasspath("configServiceTests/configServiceTest.properties")
        );
        Assert.assertEquals(2, configurationService.getClusterNodes().size());
        Assert.assertTrue(configurationService.getClusterNodes().containsAll(Arrays.asList("localhost:5555", "localhost:6666")));
        Assert.assertEquals(7777, configurationService.getGossipServerPort());
        Assert.assertEquals(2, configurationService.getTenants().size());
        Assert.assertTrue(configurationService.getTenants().containsAll(Arrays.asList("tenantA", "tenantB")));
        Assert.assertEquals(ConfigurationService.OperationMode.sharding, configurationService.getOperationalMode());
    }


}