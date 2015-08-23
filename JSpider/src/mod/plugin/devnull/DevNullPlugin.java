package mod.plugin.devnull;


import api.event.JSpiderEvent;
import spi.Plugin;


/**
 *
 * $Id: DevNullPlugin.java,v 1.4 2003/04/03 16:25:12 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class DevNullPlugin implements Plugin {

    public String getName() {
        return "DevNull module";
    }

    public void initialize() {
    }

    public void shutdown() {
    }

    public void notify(JSpiderEvent event) {
    }

    public String getVersion() {
        return "1.0";
    }

    public String getDescription() {
        return "A do-nothing implementation of a JSpider module";
    }

    public String getVendor() {
        return "http://www.javacoding.net";
    }
}
