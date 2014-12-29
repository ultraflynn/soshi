package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class NormalKey extends AbstractKey
{
    private Map canonicalValues;

    public NormalKey(Configuration config)
    {
        super(config);

        canonicalValues = new HashMap();
    }

    public void addColumn(String column, String value)
    {
        values.put(column, value);
        Comparator comparator = config.getComparator(column);
        String canonicalValue = comparator.getCanonicalForm(value);
        canonicalValues.put(column, canonicalValue);
    }

    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof NormalKey)) {
            return false;
        }
        
        NormalKey k = (NormalKey) o;

        return canonicalValues.equals(k.canonicalValues);
    }
    
    public int hashCode()
    {
        return canonicalValues.hashCode();
    }

    public Map getCanonicalValues()
    {
        return canonicalValues;
    }
}
