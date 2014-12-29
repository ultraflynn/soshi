package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class MatcherTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public MatcherTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(MatcherTest.class);
    }

    public void testNothingInCommon()
        throws Exception
    {
        Collection aKeys = new HashSet();
        aKeys.add(getKey("a"));
        aKeys.add(getKey("b"));
        aKeys.add(getKey("c"));

        Collection bKeys = new HashSet();
        bKeys.add(getKey("d"));
        bKeys.add(getKey("e"));

        Matcher matcher = new Matcher(new DummySide(aKeys), new DummySide(bKeys));
        Collection a = matcher.getSideAOnlyKeys();
        Assert.assertTrue(a.size() == 3);
        Assert.assertTrue(a.contains(getKey("a")));
        Assert.assertTrue(a.contains(getKey("b")));
        Assert.assertTrue(a.contains(getKey("c")));

        Collection b = matcher.getSideBOnlyKeys();
        Assert.assertTrue(b.size() == 2);
        Assert.assertTrue(b.contains(getKey("d")));
        Assert.assertTrue(b.contains(getKey("e")));

        Collection m = matcher.getMatchingKeys();
        Assert.assertTrue(m.size() == 0);
    }

    public void testOneSideExtra()
        throws Exception
    {
        Collection aKeys = new HashSet();
        Key AkeyA = getKey("a");
        Key AkeyB = getKey("b");
        Key AkeyC = getKey("c");
        aKeys.add(AkeyA);
        aKeys.add(AkeyB);
        aKeys.add(AkeyC);

        Collection bKeys = new HashSet();
        Key BkeyA = getKey("a");
        Key BkeyB = getKey("b");
        bKeys.add(BkeyA);
        bKeys.add(BkeyB);

        Matcher matcher = new Matcher(new DummySide(aKeys), new DummySide(bKeys));
        Collection a = matcher.getSideAOnlyKeys();
        Assert.assertTrue(a.size() == 1);
        Assert.assertTrue(a.contains(AkeyC));

        Collection b = matcher.getSideBOnlyKeys();
        Assert.assertTrue(b.size() == 0);

        Collection m = matcher.getMatchingKeys();
        Assert.assertTrue(m.size() == 2);

        Assert.assertTrue(contains(m, AkeyA, BkeyA));
        Assert.assertTrue(contains(m, AkeyB, BkeyB));
    }

    public void testMatching()
        throws Exception
    {
        Collection aKeys = new HashSet();
        Key AkeyA = getKey("a");
        Key AkeyB = getKey("b");
        aKeys.add(AkeyA);
        aKeys.add(AkeyB);

        Collection bKeys = new HashSet();
        Key BkeyA = getKey("a");
        Key BkeyB = getKey("b");
        bKeys.add(BkeyA);
        bKeys.add(BkeyB);

        Matcher matcher = new Matcher(new DummySide(aKeys), new DummySide(bKeys));
        Collection a = matcher.getSideAOnlyKeys();
        Assert.assertTrue(a.size() == 0);

        Collection b = matcher.getSideBOnlyKeys();
        Assert.assertTrue(b.size() == 0);

        Collection m = matcher.getMatchingKeys();
        Assert.assertTrue(m.size() == 2);
        Assert.assertTrue(contains(m, AkeyA, BkeyA));
        Assert.assertTrue(contains(m, AkeyB, BkeyB));
    }

    public void testUnmodifiable()
        throws Exception
    {
        Collection aKeys = new HashSet();
        Collection bKeys = new HashSet();
        Matcher matcher = new Matcher(new DummySide(aKeys), new DummySide(bKeys));

        Collection a = matcher.getSideAOnlyKeys();
        try {
            a.add(getKey("f"));
            fail("Side A only keys are modifiable");
        } catch (UnsupportedOperationException e) {
        }

        Collection b = matcher.getSideBOnlyKeys();
        try {
            b.add(getKey("f"));
            fail("Side B only keys are modifiable");
        } catch (UnsupportedOperationException e) {
        }

        Collection m = matcher.getMatchingKeys();
        try {
            m.add(getKey("f"));
            fail("Matched keys are modifiable");
        } catch (UnsupportedOperationException e) {
        }
    }

    public void testExclusions()
        throws Exception
    {
        Collection aKeys = new HashSet();
        aKeys.add(getKey("a"));
        aKeys.add(getKey("b"));
        aKeys.add(getKey("c"));

        Collection bKeys = new HashSet();
        bKeys.add(getKey("a"));
        bKeys.add(getKey("b"));
        bKeys.add(getKey("c"));

        DummySide dsA = new DummySide(aKeys);
        DummySide dsB = new DummySide(bKeys);

        dsA.addDuplicate(getKey("c"));
        dsB.addDuplicate(getKey("a"));

        Matcher matcher = new Matcher(dsA, dsB);

        Collection sideAExcluded = matcher.getSideAExcluded();
        Collection sideBExcluded = matcher.getSideBExcluded();

        assertTrue(sideAExcluded.contains(getKey("a")));
        assertTrue(!(sideAExcluded.contains(getKey("b"))));
        assertTrue(!(sideAExcluded.contains(getKey("c"))));
        assertTrue(!(sideBExcluded.contains(getKey("a"))));
        assertTrue(!(sideBExcluded.contains(getKey("b"))));
        assertTrue(sideBExcluded.contains(getKey("c")));
    }

    private Key getKey(String val)
        throws Exception
    {
        Configuration config = new Configuration();
        config.addColumnName("col", true, new StandardComparator(), null);

        Key key = new NormalKey(config);
        key.addColumn("col", val);

        return key;
    }

    private boolean contains(Collection p, Key keyA, Key keyB)
    {
        boolean result = false;

        for (Iterator i = p.iterator(); i.hasNext();) {
            KeyPair o = (KeyPair) i.next();

            if (o.getKey(Side.SIDEA).equals(keyA) && o.getKey(Side.SIDEB).equals(keyB)) {
                result = true;
                break;
            }
        }

        return result;
    }
}
