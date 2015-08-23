package core.util.config.properties;

import core.util.config.PropertySet;
import java.io.*;
import java.util.*;

/**
 * $Id: PropertiesFilePropertySet.java,v 1.8 2003/03/27 17:44:23 vanrogu Exp $
 * maintains a PropertyResourceBundle for a given property file
 * and a Map of file name to PropertySet
 */
public class PropertyFilePropertySet implements PropertySet {

    protected ResourceBundle propertyBundle;
    protected String fileName;	//property file like plugin.properties
    // map of file name to property set in the file
    protected static final Map<String, PropertySet> instances = new HashMap<>();

    private PropertyFilePropertySet(String jspiderHome, String configuration, String fileName ) {
       this.fileName = fileName;
        try {
            InputStream is = new FileInputStream(jspiderHome + File.separator + "conf" + File.separator + configuration + File.separator + fileName );
            propertyBundle = new PropertyResourceBundle(is);
        } catch (IOException e) {
            System.err.println("[Config] " + fileName + " couldn't be found -- using all default values !");
            propertyBundle = null;
        }
    }

    public synchronized static PropertySet getInstance(String jspiderHome, String configuration, String fileName) {
            if (instances.get(fileName) == null) {
                instances.put(fileName, new PropertyFilePropertySet(jspiderHome, configuration, fileName));
            }
            return (PropertySet)instances.get(fileName);
    }

    public String getString(String name, String defaultValue) {
        String value = null;
        try {
            value = getProperty(name);
        } catch (MissingResourceException e) {
//          System.err.println("[Config] Value for '" + name + "' not found in " + fileName + " - defaulting to '" + defaultValue + "'");
            value = defaultValue;
        }
        return value;
    }

    public Class<?> getClass(String name, Class<?> defaultValue) {
        String className = null;
        if ( defaultValue == null ) {
          className = getString(name, name);
        } else {
          className = getString(name, defaultValue.getName());
        }

        Class<?> retClass = null;

        try {
            //Using the thread's ContextClassLoader gives ClassCastException in JUnit.
            //retClass = Thread.currentThread().getContextClassLoader().loadClass(className);
            retClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            //System.err.println("[Config] Class '" + className + "' couldn't be loaded - defaulted to '" + defaultValue + "'");
            retClass = defaultValue;
        }
        
        return retClass;

    }

    public int getInteger(String name, int defaultValue) {
        String stringValue = getString(name, "" + defaultValue);

        int value = 0;
        try {
            value = Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            //System.err.println("[Config] NumberFormatException on value '" + name + "', defaulted to " + defaultValue);
            value = defaultValue;
        }

        return value;
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        String stringValue = getString(name, "" + defaultValue);
        boolean value = new Boolean(stringValue).booleanValue();
        return value;
    }

    protected String getProperty ( String name ) throws MissingResourceException {
        if ( propertyBundle == null ) {
            throw new MissingResourceException(name,name,name);
        } else{
            return propertyBundle.getString(name);
        }
    }
}
