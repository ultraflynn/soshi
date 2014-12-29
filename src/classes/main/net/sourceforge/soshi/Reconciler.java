package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.Collection;
import java.util.Iterator;

public class Reconciler
{
    private Configuration config;

    public Reconciler(Configuration config)
    {
        this.config = config;
    }

    public Results reconcile(Side a, Side b)
    {
        Results results = new Results();
        results.setConfiguration(config);
        results.setSide(Side.SIDEA, a);
        results.setSide(Side.SIDEB, b);

        Matcher matcher = new Matcher(a, b);

        for (Iterator i = matcher.getSideAOnlyKeys().iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            results.addMissingKey(Side.SIDEB, key);
        }

        for (Iterator i = matcher.getSideBOnlyKeys().iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            results.addMissingKey(Side.SIDEA, key);
        }

        for (Iterator i = a.getDuplicateKeys().iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            results.addDuplicateKey(Side.SIDEA, key);
        }

        for (Iterator i = b.getDuplicateKeys().iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            results.addDuplicateKey(Side.SIDEB, key);
        }

        for (Iterator i = matcher.getSideAExcluded().iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            results.addExclusion(Side.SIDEA, key);
        }

        for (Iterator i = matcher.getSideBExcluded().iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            results.addExclusion(Side.SIDEB, key);
        }

        for (Iterator i = matcher.getMatchingKeys().iterator(); i.hasNext();) {
            KeyPair keys = (KeyPair) i.next();

            Key keyA = (Key) keys.getKey(Side.SIDEA);
            Key keyB = (Key) keys.getKey(Side.SIDEB);

            results.addMatchedKey(keys);
            reconcileRow(results, a.getRow(keyA), b.getRow(keyB));
        }

        return results;
    }

    private void reconcileRow(Results results, Row a, Row b)
    {
        Collection names = config.getColumnNames();
                
        for (Iterator i = names.iterator(); i.hasNext();) {
            String name = (String) i.next();
            String colA = a.getColumnValue(name);
            String colB = b.getColumnValue(name);

            Comparator comparator = config.getComparator(name);

            if (!comparator.equals(colA, colB)) {
                results.addDifference(new KeyPair(a.getKey(), b.getKey()), name, colA, colB);
            }
        }
    }
}
