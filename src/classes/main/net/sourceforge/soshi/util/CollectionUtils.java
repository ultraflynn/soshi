package net.sourceforge.soshi.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class CollectionUtils
{
    public static Collection intersection(Collection a, Collection b)
    {
        Collection results = new HashSet();

        for (Iterator i = a.iterator(); i.hasNext();) {
            Object o = i.next();
            if (b.contains(o)) {
                results.add(o);
            }
        }
        
        return results;
    }

    public static Collection subtract(Collection a, Collection b)
    {
        Collection results = new HashSet();

        for (Iterator i = a.iterator(); i.hasNext();) {
            Object o = i.next();
            if (!(b.contains(o))) {
                results.add(o);
            }
        }

        return results;
    }
}
