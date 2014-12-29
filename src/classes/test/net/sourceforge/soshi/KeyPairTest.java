package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.HashMap;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase; 
import junit.framework.TestSuite;

import junit.textui.TestRunner;


public class KeyPairTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public KeyPairTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(KeyPairTest.class);
    }
    
    private Configuration config;

    public void setUp()
        throws Exception
    {
        config = new Configuration();
        config.addColumnName("colA", true, new StandardComparator(), null);
        config.addColumnName("colB", true, new StandardComparator(), null);
        config.addColumnName("colC", true, new StandardComparator(), null);
    }

    public void testGetKey()
        throws Exception
    {
        Key keyA = new NormalKey(config);
        keyA.addColumn("colA", "valA");
        keyA.addColumn("colB", "valB");

        Key keyB = new NormalKey(config);
        keyB.addColumn("colC", "valC");

        KeyPair p = new KeyPair(keyA, keyB);

        Assert.assertTrue(p.getKey(Side.SIDEA).equals(keyA));
        Assert.assertTrue(p.getKey(Side.SIDEB).equals(keyB));

        Assert.assertTrue(!(p.getKey(Side.SIDEA).equals(p.getKey(Side.SIDEB))));
        Assert.assertTrue(!(p.getKey(Side.SIDEB).equals(p.getKey(Side.SIDEA))));

        p = new KeyPair(keyA, keyA);

        Assert.assertTrue(p.getKey(Side.SIDEA).equals(keyA));
        Assert.assertTrue(p.getKey(Side.SIDEB).equals(keyA));

        Assert.assertTrue(p.getKey(Side.SIDEA).equals(p.getKey(Side.SIDEB)));
        Assert.assertTrue(p.getKey(Side.SIDEB).equals(p.getKey(Side.SIDEA)));
    }

    public void testToString()
        throws Exception
    {
        Key keyA = new NormalKey(config);
        keyA.addColumn("colA", "valA");

        Key keyB = new NormalKey(config);
        keyA.addColumn("colB", "valB");

        KeyPair p = new KeyPair(keyA, keyB);

        Assert.assertTrue(p.toString(Side.SIDEA).equals(keyA.toString()));
        Assert.assertTrue(p.toString(Side.SIDEB).equals(keyB.toString()));
        Assert.assertTrue(!(p.toString(Side.SIDEA).equals(keyB.toString())));
        Assert.assertTrue(!(p.toString(Side.SIDEB).equals(keyA.toString())));

        Assert.assertTrue(p.toString(Side.SIDEA).equals(p.toString(Side.SIDEA)));
        Assert.assertTrue(p.toString(Side.SIDEB).equals(p.toString(Side.SIDEB)));

        Assert.assertTrue(!(p.toString(Side.SIDEA).equals(p.toString(Side.SIDEB))));
        Assert.assertTrue(!(p.toString(Side.SIDEB).equals(p.toString(Side.SIDEA))));
    }

    public void testHashLookup()
        throws Exception
    {
        Key keyA1 = new NormalKey(config);
        keyA1.addColumn("colA", "valA");
        keyA1.addColumn("colB", "valB");

        Key keyB1 = new NormalKey(config);
        keyB1.addColumn("colA", "valA");
        keyB1.addColumn("colB", "valB");

        KeyPair p1 = new KeyPair(keyA1, keyB1);

        Key keyA2 = new NormalKey(config);
        keyA2.addColumn("colA", "valA");
        keyA2.addColumn("colB", "valB");

        Key keyB2 = new NormalKey(config);
        keyB2.addColumn("colA", "valA");
        keyB2.addColumn("colB", "valB");

        KeyPair p2 = new KeyPair(keyA2, keyB2);

        HashMap hash = new HashMap();
        hash.put(p1, "yes");
        Object result = hash.get(p2);
        assertTrue(result.equals("yes"));
    }
}
