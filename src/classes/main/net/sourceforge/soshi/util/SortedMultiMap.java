package net.sourceforge.soshi.util;

import java.util.Comparator;

public interface SortedMultiMap extends MultiMap
{
    public Comparator comparator();

    public Object firstKey();

    public SortedMultiMap headMap(Object toKey);

    public Object lastKey();

    public SortedMultiMap subMap(Object fromKey, Object toKey);

    public SortedMultiMap tailMap(Object fromKey);
}
