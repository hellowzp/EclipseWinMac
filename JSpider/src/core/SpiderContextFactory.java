package core;


import api.event.EventSink;
import core.dispatch.EventDispatcher;
import core.dispatch.impl.EventDispatcherImpl;
import core.impl.*;
import core.storage.Storage;
import core.storage.StorageFactory;
import core.throttle.ThrottleFactory;
import core.util.config.*;

import java.net.URL;


/**
 *
 * $Id: SpiderContextFactory.java,v 1.12 2003/04/03 15:57:14 vanrogu Exp $
 *
 * @author Günther Van Roey
 */
public class SpiderContextFactory {

    public static SpiderContext createContext(URL baseURL) {

        EventSink[] sinks = new PluginFactory().createPlugins();
        PropertySet props = ConfigurationFactory.getConfiguration().getPluginsConfiguration();
        PropertySet filterProps = new MappedPropertySet ( ConfigConstants.CONFIG_FILTER, props );
        EventDispatcher dispatcher = new EventDispatcherImpl("Global Event Dispatcher", sinks, filterProps);
        dispatcher.initialize();

        Storage storage = new StorageFactory().createStorage();
        ThrottleFactory throttleFactory = new ThrottleFactory();

        SpiderContext context = new SpiderContextImpl(baseURL, dispatcher, throttleFactory, storage);

        //todo: This is quite weird, isn't it?  REFACTOR the relationship between Agent and Context!  HOW ???
        context.setAgent(new AgentImpl(context));

        return context;
    }
}
