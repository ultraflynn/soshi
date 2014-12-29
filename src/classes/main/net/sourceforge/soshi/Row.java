package net.sourceforge.soshi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Row
{
    private Map map;
    private Key key;
    
    public Row()
    {
        map = new HashMap();
    }

    public Key getKey()
    {
        return key;
    }

    public void setKey(Key key)
    {
        this.key = key;
    }

    public void addColumn(String column, String value)
    {
        map.put(column, value);
    }

    public String getColumnValue(String column)
    {
        return (String) map.get(column);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Row ID: " + key + "\n");

        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            sb.append("key: " + entry.getKey() + " value: " + entry.getValue() + "\n");
        }

        return sb.toString();
    }
}
