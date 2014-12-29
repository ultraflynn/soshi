package net.sourceforge.soshi.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface MultiMap
{
    public void clear();

    public Set coarseEntrySet();

    public Collection coarseValues();

    public boolean containsEntry(Object key, Object value);

    public boolean containsKey(Object key);

    public boolean containsValue(Object value);

    public Set fineEntrySet();

    public Collection fineValues();

    public boolean equals(Object o);

    public Collection get(Object key);

    public int hashCode();

    public boolean hasKeys();

    public boolean hasValues();

    public Set keySet();

    public Collection put(Object key);

    public void put(Object key, Object value);

    public void putAll(Map map);

    public void putAll(MultiMap map);

    public Collection remove(Object key);

    public boolean remove(Object key, Object value);

    public int sizeKeys();

    public int sizeValues();

    public interface Entry
    {
        public Object getKey();

        public Object getValue();
    }
}
