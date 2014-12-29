package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class DifferenceTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public DifferenceTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(DifferenceTest.class);
    }

    private KeyPair keyPair;

    public void setUp()
        throws Exception
    {
        Configuration config = new Configuration();
        config.addColumnName("key", true, new StandardComparator(), "descr");
        Key key = new NormalKey(config);
        key.addColumn("key", "value");
        keyPair = new KeyPair(key, key);
    }

    public void testGetKey()
        throws Exception
    {
        Difference difference = new Difference(keyPair, "col", "valA", "valB");
        KeyPair result = difference.getKeyPair();
        Assert.assertTrue(result.equals(keyPair));
    }

    public void testGetColumn()
        throws Exception
    {
        Difference difference = new Difference(keyPair, "col", "valA", "valB");
        String column = difference.getColumn();
        Assert.assertTrue(column.equals("col"));
    }

    public void testGetValueA()
        throws Exception
    {
        Difference difference = new Difference(keyPair, "col", "valA", "valB");
        String valA = difference.getValueA();
        Assert.assertTrue(valA.equals("valA"));
    }

    public void testGetValueB()
        throws Exception
    {
        Difference difference = new Difference(keyPair, "col", "valA", "valB");
        String valB = difference.getValueB();
        Assert.assertTrue(valB.equals("valB"));
    }
}
