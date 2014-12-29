package net.sourceforge.soshi.util;

import java.io.Serializable;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TreeMultiMap extends UnderlyingMapMultiMap implements SortedMultiMap, Serializable
{
    private SortedMap map;

    public TreeMultiMap()
    {
        map = new TreeMap();
    }

    public TreeMultiMap(Comparator comparator)
    {
        map = new TreeMap(comparator);
    }

    public TreeMultiMap(Map map)
    {
        this();
        putAll(map);
    }

    protected Map getMap()
    {
        return map;
    }

    public Comparator comparator()
    {
        return map.comparator();
    }

    public Object firstKey()
    {
        return map.firstKey();
    }

    public SortedMultiMap headMap(Object toKey)
    {
        TreeMultiMap headMap = new TreeMultiMap();
        headMap.map = map.headMap(toKey);
        return headMap;
    }

    public Object lastKey()
    {
        return map.lastKey();
    }

    public SortedMultiMap subMap(Object fromKey, Object toKey)
    {
        TreeMultiMap subMap = new TreeMultiMap();
        subMap.map = map.subMap(fromKey, toKey);
        return subMap;
    }

    public SortedMultiMap tailMap(Object fromKey)
    {
        TreeMultiMap tailMap = new TreeMultiMap();
        tailMap.map = map.tailMap(fromKey);
        return tailMap;
    }
}
