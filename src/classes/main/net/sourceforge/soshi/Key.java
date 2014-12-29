package net.sourceforge.soshi;

import java.util.Map;

public interface Key
{
    public void addColumn(String column, String value);
    
    public boolean equals(Object o);
    
    public int hashCode();

    public String toString();

    public Map getCanonicalValues();
}
