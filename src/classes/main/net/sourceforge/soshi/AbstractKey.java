package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractKey implements Key
{
    protected Configuration config;
    protected Map values;

    public AbstractKey(Configuration config)
    {
        this.config = config;
        values = new HashMap();
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        int valuesSize = values.size();

        for (Iterator i = values.values().iterator(); i.hasNext();) {
            String value = (String) i.next();

            if (isFirst) {
                isFirst = false;
            } else {
                sb.append("#");
            }

            sb.append(value);
        }

        return sb.toString();
    }
}
