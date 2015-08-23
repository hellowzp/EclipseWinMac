package core.util.config.properties;

import api.model.Site;
import core.util.config.*;

import java.io.File;

/**
 * $Id: PropertiesConfiguration.java,v 1.14 2003/04/10 16:19:16 vanrogu Exp $
 * configure jspiderHome, output folder and the other three properties 
 * in the corresponding PropertySet according to the specified conf passed to 
 * the constructor, if no parameter passed, use the default one
 */
public class PropertiesConfiguration implements JSpiderConfiguration {

    protected String configuration;          //default download..
    protected String jspiderHome;
    protected PropertySet jspiderProperties; //global properties
    protected PropertySet pluginsProperties;
    protected PropertySet websitesConfig;
    protected File defaultOutputFolder;

    public PropertiesConfiguration ( ) {
        this ( ConfigurationFactory.CONFIG_DEFAULT );
    }

    public PropertiesConfiguration ( String configuration ) {

        jspiderHome = System.getProperty("jspider.home");
        if ( jspiderHome == null || "".equals(jspiderHome.trim()) || "null".equals(jspiderHome.trim())) {
          jspiderHome = ".";
        }
        defaultOutputFolder = new File ( jspiderHome + File.separator + "output" );
        System.err.println("[Engine] jspider.home=" + jspiderHome );
        System.err.println("[Engine] default output folder=" + defaultOutputFolder );
        System.err.println("[Engine] starting with configuration '" + configuration + "'");

        this.configuration = configuration;

        jspiderProperties = PropertyFilePropertySet.getInstance ( jspiderHome, configuration, "jspider.properties" );
        pluginsProperties = PropertyFilePropertySet.getInstance ( jspiderHome, configuration, "plugin.properties" );
        websitesConfig = PropertyFilePropertySet.getInstance(jspiderHome, configuration, "sites.properties" );
    }

    public File getDefaultOutputFolder() {
        return defaultOutputFolder;
    }

    public PropertySet getJSpiderConfiguration() {
        return jspiderProperties;
    }

    public PropertySet getPluginsConfiguration() {
        return pluginsProperties;
    }

    public PropertySet getPluginConfiguration(String pluginName) {
        return PropertyFilePropertySet.getInstance(jspiderHome, configuration, "plugins" + File.separator + pluginName + ".properties");
    }

    public File getPluginConfigurationFolder(String pluginName) {
        File jspiderHomeFile = new File (jspiderHome);
        File configFolder  = new File ( jspiderHomeFile,  "conf" );
        File config = new File ( configFolder, configuration );
        File plugins = new File ( config, "plugins" );
        return new File ( plugins, pluginName );
    }

    public PropertySet getSiteConfiguration(Site site) {
        if ( site.isBaseSite() ) {
          return ConfigurationFactory.getConfiguration().getBaseSiteConfiguration();
        } else {
          return getSiteConfiguration(site.getHost(), site.getPort());
        }
    }

    public PropertySet getSiteConfiguration(String host, int port) {
        String matchString = host + ":" + port;
        String configName = null;
        if ( port > 0 ) {
            configName = websitesConfig.getString(matchString, null);
        }
        if ( configName == null ) {
            matchString = host;
            configName = websitesConfig.getString(matchString, websitesConfig.getString(ConfigConstants.SITES_DEFAULT_SITE, "default") );
        }
        return PropertyFilePropertySet.getInstance(jspiderHome, configuration, "sites" + File.separator + configName + ".properties" );
    }

    public PropertySet getDefaultSiteConfiguration() {
        return PropertyFilePropertySet.getInstance(jspiderHome, configuration, "sites" + File.separator + websitesConfig.getString(ConfigConstants.SITES_DEFAULT_SITE, "default" ) + ".properties" );
    }

    public PropertySet getBaseSiteConfiguration() {
        return PropertyFilePropertySet.getInstance(jspiderHome, configuration, "sites" + File.separator + websitesConfig.getString(ConfigConstants.SITES_BASE_SITE, "default" ) + ".properties" );
    }

}
