package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import java.util.Collection;

public interface Side
{
    public static final int SIDEA = 1;
    public static final int SIDEB = 2;
    
    public String getName();

    public void setName(String name);

    public int getSideType();

    public void setSideType(int type);

    public Row getRow(Key key);

    public Collection getAllRows(Key key);
    
    public Collection getKeys();

    public Collection getDuplicateKeys();

    public void setConfiguration(Configuration config);

    public void addParameter(String name, String value);

    public void load()
        throws DataException, ConfigurationException;
}
