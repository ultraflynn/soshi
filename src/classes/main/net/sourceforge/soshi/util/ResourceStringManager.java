package net.sourceforge.soshi.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;

public class ResourceStringManager
{
    private static ResourceStringManager instance;

    private Map strings;

    public static synchronized ResourceStringManager getInstance()
    {
        if (instance == null) {
            instance = new ResourceStringManager();
        }

        return instance;
    }

    private ResourceStringManager()
    {
        strings = new HashMap();
    }

    public String getString(String resourceName)
    {
        String result = (String) strings.get(resourceName);

        if (result == null) {
            ClassLoader cl = getClass().getClassLoader();
            InputStream is = cl.getResourceAsStream(resourceName);
            if (is == null) {
                String error = "Missing resource: " + resourceName;
                throw new RuntimeException(error);
            }

            InputStreamReader isr = new InputStreamReader(is);
            try {
                result = IOUtils.getAsString(isr);
            } catch (java.io.IOException e) {
                throw new RuntimeException(e.getMessage());
            }

            strings.put(resourceName, result);
        }

        return result;
    }
}
