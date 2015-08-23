package net.pandoragames.far.ui.swing.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Adapter to interact with the standard menu on Apples Mac OS X. Intercepts the
 * "Quit" menu item for controlled shut down and helps to show the "About" and "Settings"
 * entries where Mac users expect them. If you thought Windows was a pain, then come 
 * and be amazed.
 * <p>
 * Implementation notes: The "public" API for this stuff is actually not public at all
 * but part of Apples JRE release. Thus, in order to stay platform independent,
 * we do a lot of reflection magic here (following Apples advises). This object
 * serves as a <code>java.lang.reflect.<b>Proxy</b></code> for an 
 * <code>com.apple.eawt.<b>ApplicationListener</b></code> interface that is plugged into Apples
 * <code>com.apple.eawt.<b>Application</b></code> class. Since the EAWT classes are
 * not directly accessible (except on Mac OS X), they are loaded using 
 * <code>Class.forName</code>. Wow, I'm not sure if MS could do worse!
 *
 * @author Olivier Wehner at 01/12/2009
 * <!--
 *  FAR - Find And Replace
 *  Copyright (C) 2009,  Olivier Wehner

 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  -->
 */
public class MacOSXMenuAdapter implements InvocationHandler {
	
	/**
	 * These enum constants represent the methods that can potentially be called
	 * on the <code>ApplicationListener</code> interface. Use them with method
	 * {@link MacOSXMenuAdapter#registerEventHandler(ActionListener, net.pandoragames.far.ui.swing.component.MacOSXMenuAdapter.OSXCOMMAND) registerEventHandler()}
	 * to register one or more ActionnListener that should handle the call.
	 */
	public enum OSXCOMMAND {  /** Called when the user selects the About item in the application menu. */
							  About, 
							  /** Called when the application is started. */ 
							  OpenApplication,
							  /** Called when the application receives an Open Document event from the Finder or another application.*/
							  OpenFile,
							  /** Called when the Preference item in the application menu is selected.*/
							  Preferences,
							  /** Called when the application is sent a request to print a particular file or files.*/
							  PrintFile,
							  /** Called when the application is about to shut down.*/
							  Quit,
							  /** Called when the minimized application regains the focus.*/
							  ReOpenApplication
							  };

	private static final String METHODPREFIX = "handle";
	private static final String CL_OSX_APP = "com.apple.eawt.Application";
	private static final String CL_OSX_APP_LSTR = "com.apple.eawt.ApplicationListener";
	
	// list of registered listener
	private Map<String, List<ActionListener>> listenerMap 
				= new HashMap<String, List<ActionListener>>();
	// actually an instance of com.apple.eawt.Application
	private Object macOSXApplication;
	// THIS as Proxy for interface ApplicationListener
	private Object applicationListenerProxy;
	
	private Log logger = LogFactory.getLog( this.getClass() );
	
	/**
	 * Instantiates the <code>Application</code> class and initialises <i>this</i>
	 * as a Proxy for interface <code>ApplicationListener</code>. Wires the two together.
	 */
	public MacOSXMenuAdapter() {
		// 1. get an instance of apples Application class
		Class applicationClass = null;
		try {
			applicationClass = Class.forName( CL_OSX_APP );
			macOSXApplication = applicationClass.newInstance();
		} catch (Exception x) {
			String message = x.getClass().getName() + " instantiating " + CL_OSX_APP + ": " + x.getMessage();
			logger.error( message );
			throw new IllegalStateException( message );
		}
		// 2. Make THIS a Proxy for an ApplicationListener instance
		try {
			Class applicationListenerClass = Class.forName( CL_OSX_APP_LSTR );
			applicationListenerProxy = Proxy.newProxyInstance(MacOSXMenuAdapter.class.getClassLoader(), 
																new Class[] { applicationListenerClass }, 
																this);
			Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", 
										new Class[] { applicationListenerClass });
			addListenerMethod.invoke(macOSXApplication, new Object[] { applicationListenerProxy });
		} catch (Exception x) {
			String message = x.getClass().getName() + " instantiating " + CL_OSX_APP_LSTR + ": " + x.getMessage();
			logger.error( message );
			throw new IllegalStateException( message );
		}
	}
	
	/**
	 * Adds an ActionListener for the specified command.
	 * @param listener to be executed on the indicated command.
	 * @param cmd the command to trigger the action. 
	 */
	public void registerEventHandler(ActionListener listener, OSXCOMMAND cmd) {
		if(listener == null) throw new NullPointerException("ActionListener must not be null");
		if( cmd == null ) throw new NullPointerException("Command parameter must not be null");
		if( listenerMap.containsKey( cmd.name() ) ) {
			listenerMap.get( cmd.name() ).add( listener );
		} else {
			List<ActionListener> list = new ArrayList<ActionListener>();
			list.add( listener );
			listenerMap.put( cmd.name(), list );
		}
		switch ( cmd ) {
			case About : enableApplicationMenuItem("setEnabledAboutMenu"); break;
			case Preferences : enableApplicationMenuItem("setEnabledPreferencesMenu"); break;
			case OpenFile : /* not implemented */ break;
		}
		logger.info("Registered " + listener.getClass().getName() 
					+ " for command " + cmd.name().toUpperCase());
	}
	
	/**
	 * Implementation of the InvocationHandler interface. 
	 * @param proxy the proxy instance that the method was invoked on.
	 * @param method the Method instance corresponding to the interface method invoked.
	 * This is supposed to be one of the methods of class 
	 * <code>com.apple.eawt.ApplicationListener</code>
	 * @param args method arguments, i.e. an instance of 
	 * <code>com.apple.eawt.ApplicationEvent</code>
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable {
		String methodName = method.getName();
		logger.debug("Callback for method " + methodName + " received");
		if (validateCallParameter(methodName, args) && canHandle( methodName )) {
            try {
            	EventObject osxEvent = (EventObject) args[0];
            	OSXActionEvent actionEvent = new OSXActionEvent( methodName, osxEvent );
                fireOSXEvent( actionEvent );
            	// mark the event object as handled
                Method setHandledMethod = osxEvent.getClass().getDeclaredMethod("setHandled", new Class[] { boolean.class });
                setHandledMethod.invoke(osxEvent, new Object[] { Boolean.TRUE });
            } catch (Exception erx) {
                logger.error( erx.getClass().getName() + " handling call to method "
                			 + methodName + ": " + erx.getMessage(), erx);
            }
        }
        // void methods return null
        return null;
	}

	/**
	 * Makes sure the method name starts with "handle" and the argument array
	 * is non null, of length one and containing an EventObject instance.
	 * @param methodName name of calling method to be tested
	 * @param args method arguments to be validated 
	 * @return true if the call can in principle be handled
	 */
	private boolean validateCallParameter(String methodName, Object[] args) {
		if( ! methodName.startsWith( METHODPREFIX )) {
			logger.warn("Received call for method " + methodName 
					+ " on Proxy for com.apple.eawt.ApplicationListener");
			return false;
		}
		if( args == null ) {
			logger.warn("No arguments specified for call to method " + methodName);
			return false;
		}
		if( args.length != 1 ) {
			logger.warn("Illegal number of arguments for call to method " 
					    + methodName + ":" + args.length );
			return false;
		}
		if( args[0] == null ) {
			logger.warn("Expected com.apple.eawt.ApplicationEvent but was null");
			return false;
		}
		if( ! (args[0] instanceof EventObject) ) {
			logger.warn("Expected com.apple.eawt.ApplicationEvent but was " + args[0].getClass().getName());
			return false;
		}
		return true;
	}
	
	/**
	 * Returns true if a handler (ActionListener) has been defined for the 
	 * specified method. Returns false otherwise.
	 * @param methodName representing the command to be executed.
	 * @return true if a handler has been defined
	 */
	private boolean canHandle(String methodName) {
		String command = methodName.substring(METHODPREFIX.length());
		try {
			OSXCOMMAND cmd = Enum.valueOf(OSXCOMMAND.class, command);
			return listenerMap.containsKey( cmd.name() );
		} catch (IllegalArgumentException iax) {
			logger.warn("Method " + methodName 
					+ " is not supported (unknown command: " + command +")");
			return false;
		}
	}
	
	/**
	 * Calls the ActionListener that was registered for the command sting of this
	 * ActionEvent. The command string is derived from the method name of the callback method. 
	 * @param event wrapping an original apple event
	 */
	private void fireOSXEvent(OSXActionEvent event) {
		List<ActionListener> listenerList = listenerMap.get( event.getActionCommand() );
		if( listenerList == null ) {
			// should never happen - but we only log a warning though
			logger.warn("No listener found for command " + event.getActionCommand().toUpperCase());
			return;
		}
		logger.debug("Calling " + listenerList.size() + " ActionListener for command " + event.getActionCommand());
		for(ActionListener listener : listenerList) {
			listener.actionPerformed( event );
		}
	}
	
	/**
	 * Some methods must explicitely be enabled.
	 * @param methodName name of a method on class Application that takes a single boolean argument
	 */
	private void enableApplicationMenuItem(String methodName) {
        try {
            Method enableAboutMethod = macOSXApplication.getClass().getDeclaredMethod(methodName, new Class[] { boolean.class });
            enableAboutMethod.invoke(macOSXApplication, new Object[] { Boolean.TRUE });
        } catch (Exception x) {
            logger.error( x.getClass().getName() + " enabling menu item, calling " 
            		+ methodName + ": " + x.getMessage());
        }
	}
	/**
	 * A wrapper for Apples <code>com.apple.eawt.ApplicationEvent</code>.
	 */
	class OSXActionEvent extends ActionEvent {
		/**
		 * Wraps the original OS X event 
		 * @param methodName used to generate the action comand string
		 * @param baseEvent original event
		 */
		public OSXActionEvent(String methodName, EventObject baseEvent) {
			super( baseEvent.getSource(), 
					ActionEvent.ACTION_PERFORMED, 
					methodName.substring(METHODPREFIX.length()));
		}
	}
}
