package net.sourceforge.soshi;

import net.sourceforge.soshi.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class Matcher
{
    private Collection aOnly;
    private Collection bOnly;
    private Collection matching;
    private Collection aExcluded;
    private Collection bExcluded;
    
    public Matcher(Side a, Side b)
    {
        Collection aKeys = a.getKeys();
        Collection bKeys = b.getKeys();

        aExcluded = CollectionUtils.intersection(aKeys, b.getDuplicateKeys());
        bExcluded = CollectionUtils.intersection(bKeys, a.getDuplicateKeys());

        aKeys = CollectionUtils.subtract(aKeys, aExcluded);
        bKeys = CollectionUtils.subtract(bKeys, bExcluded);

        aOnly = CollectionUtils.subtract(aKeys, bKeys);
        bOnly = CollectionUtils.subtract(bKeys, aKeys);

        matching = new HashSet();

        Map bKeyMap = new HashMap();
        for (Iterator i = bKeys.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            bKeyMap.put(key, key);
        }

        for (Iterator i = aKeys.iterator(); i.hasNext();) {
            Key keyA = (Key) i.next();
            Key keyB = (Key) bKeyMap.get(keyA);

            if (keyB != null) {
                matching.add(new KeyPair(keyA, keyB));
            }
        }
    }

    public Collection getSideAOnlyKeys()
    {
        return Collections.unmodifiableCollection(aOnly);
    }

    public Collection getSideBOnlyKeys()
    {
        return Collections.unmodifiableCollection(bOnly);
    }

    public Collection getMatchingKeys()
    {
        return Collections.unmodifiableCollection(matching);
    }

    public Collection getSideAExcluded()
    {
        return Collections.unmodifiableCollection(aExcluded);
    }

    public Collection getSideBExcluded()
    {
        return Collections.unmodifiableCollection(bExcluded);
    }
}
