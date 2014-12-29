package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

public class DummySide implements Side
{
    private String name;
    private int type;
    private Map params = new HashMap();
    private Collection keys;
    private Collection duplicates = new HashSet();

    public DummySide()
    {
    }

    public DummySide(Collection keys) {
        this.keys = keys;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getSideType()
    {
        return type;
    }

    public void setSideType(int type)
    {
        this.type = type;
    }

    public Row getRow(Key key)
    {
        return null;
    }

    public Collection getAllRows(Key key)
    {
        return new ArrayList();
    }
    
    public Collection getKeys()
    {
        return keys;
    }

    public void addDuplicate(Key key)
    {
        duplicates.add(key);
    }

    public Collection getDuplicateKeys()
    {
        return duplicates;
    }

    public void setConfiguration(Configuration config)
    {
    }

    public void addParameter(String name, String value)
    {
        params.put(name, value);
    }

    public void load()
        throws DataException, ConfigurationException
    {
    }

    public Map getParameters()
    {
        return params;
    }
}
