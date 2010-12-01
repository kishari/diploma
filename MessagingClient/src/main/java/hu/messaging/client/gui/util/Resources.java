package hu.messaging.client.gui.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Manages the resource bundle for the client
 *
 */
public class Resources
{
    /**
     * Sole instance of the Resources class
     */
    private static Resources instance = new Resources();
    
    /**
     * Resource bundle
     */
    private ResourceBundle bundle;
    
    /**
     * Present instantiation of the Resources class.
     *
     */
    private Resources()
    {
        // private constructor to prevent instantiation
        bundle = ResourceBundle.getBundle(Resources.class.getPackage().getName()+".resources");
    }

    /**
     * Retrives the instance of the Resources
     * @return
     */
    public final static Resources getInstance()
    {
        return instance;
    }
    
    /**
     * Retrieves the value for <code>key</code> in the resource bundle and format it.
     * @param key key for the resource bundle
     * @return the value in the resource bundle
     */
    public String get(String key)
    {
        String value = null;
        try
        {
            value = bundle.getString(key);
        }
        catch(MissingResourceException e)
        {
            value = key;
        }
        return value;
    }
    
    /**
     * Retrieves the value for <code>key</code> in the resource bundle and format it.
     * @param key key for the resource bundle
     * @param params parameters to format the string
     * @return the formatted string
     */
    public String get(String key, Object[] params)
    {
        String value = get(key);
        MessageFormat format = new MessageFormat(value);
        return format.format(params);
    }
}
