package core.util.config;

/**
 * just provide a convernience for properties having the same prefix 
 */
public class MappedPropertySet implements PropertySet {

    protected String prefix;
    protected PropertySet delegate;

    public MappedPropertySet ( String prefix, PropertySet delegate ) {
        this.prefix = prefix;
        this.delegate = delegate;
    }

    public String getString(String name, String defaultValue) {
        return delegate.getString(prefix + "." + name, defaultValue);
    }

    public Class<?> getClass(String name, Class<?> defaultValue) {
        return delegate.getClass(prefix + "." + name, defaultValue);
    }

    public int getInteger(String name, int defaultValue) {
        return delegate.getInteger(prefix + "." + name, defaultValue);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return delegate.getBoolean(prefix + "." + name, defaultValue);
    }

}
