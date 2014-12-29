package net.sourceforge.soshi.util;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

public class HashMultiMap extends UnderlyingMapMultiMap implements Serializable
{
    private HashMap map;

    public HashMultiMap()
    {
        map = new HashMap();
    }

    public HashMultiMap(Map map)
    {
        this();
        putAll(map);
    }

    protected Map getMap()
    {
        return map;
    }
}
