package core.dispatch.impl;

import api.event.EventSink;
import api.event.JSpiderEvent;
import core.dispatch.EventDispatcher;
import core.util.config.PropertySet;
import spi.Plugin;

/**
 * Proxy class that enables event filtering for a plugin.  If the plugin is
 * configured for local event filtering, an instance of this class will wrap
 * the real plugin instance and filter the incoming dispatched events.
 *
 * $Id: PluginSocket.java,v 1.9 2003/04/03 16:24:51 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class PluginSocket implements Plugin {

    protected Plugin plugin;
    protected EventDispatcher dispatcher;

    public PluginSocket ( Plugin plugin, PropertySet props) {
       this.plugin = plugin;
       EventSink[] sinks = new EventSink[1];
       sinks[0] = plugin;
       dispatcher = new EventDispatcherImpl("EventDispatcher for Plugin '" + plugin.getName() + "'", sinks, props);
    }

    public void initialize() {
        dispatcher.initialize ( );
    }

    public void shutdown() {
        dispatcher.shutdown();
    }

    public String getName() {
        return plugin.getName ( );
    }

    public String getVersion() {
        return plugin.getVersion ( );
    }

    public String getDescription() {
        return plugin.getDescription ( );
    }

    public String getVendor() {
        return plugin.getVendor ( );
    }

    public synchronized void notify(JSpiderEvent event) {
        dispatcher.dispatch(event);
    }

}
