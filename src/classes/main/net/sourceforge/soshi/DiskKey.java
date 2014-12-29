package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DiskKey extends AbstractKey
{
    private List fileOffsets;

    public DiskKey(Configuration config, long fileOffset)
    {
        super(config);

        fileOffsets = new ArrayList();
        fileOffsets.add(new Long(fileOffset));
    }

    public void addColumn(String column, String value)
    {
        values.put(column, value);
    }
    
    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof DiskKey)) {
            return false;
        }
        
        DiskKey k = (DiskKey) o;

        return getCanonicalValues().equals(k.getCanonicalValues());
    }
    
    public int hashCode()
    {
        return getCanonicalValues().hashCode();
    }

    public Map getCanonicalValues()
    {
        Map canonicalValues = new HashMap();

        for (Iterator i = values.keySet().iterator(); i.hasNext();) {
            String column = (String) i.next();
            String value = (String) values.get(column);

            Comparator comparator = config.getComparator(column);
            String canonicalValue = comparator.getCanonicalForm(value);
            canonicalValues.put(column, canonicalValue);
        }

        return canonicalValues;
    }

    public void addFileOffset(long fileOffset)
    {
        fileOffsets.add(new Long(fileOffset));
    }

    public long getFileOffset()
    {
        Long l = (Long) fileOffsets.get(0);
        return l.longValue();
    }

    public Collection getAllFileOffsets()
    {
        return Collections.unmodifiableCollection(fileOffsets);
    }
}
