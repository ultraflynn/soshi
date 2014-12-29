package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class KeyTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public KeyTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(KeyTest.class);
    }

    private Key key1;
    private Key key2;
    private Key key3;

    public void setUp()
        throws Exception
    {
        Configuration config = new Configuration();
        config.addColumnName("cola", true, new IgnoreWhitespaceComparator(), null);
        config.addColumnName("colb", true, new IgnoreWhitespaceComparator(), null);

        key1 = new NormalKey(config);
        key1.addColumn("cola", "vala");
        key1.addColumn("colb", "valb");

        key2 = new NormalKey(config);
        key2.addColumn("cola", "v a la ");
        key2.addColumn("colb", " va lb ");

        key3 = new NormalKey(config);
        key3.addColumn("cola", "vala");
        key3.addColumn("colb", "different");
    }

    public void testEquals()
        throws Exception
    {
        boolean result;

        result = key1.equals(null);
        Assert.assertTrue(result == false);

        result = key1.equals("");
        Assert.assertTrue(result == false);

        result = key1.equals(key2);
        Assert.assertTrue(result == true);

        result = key2.equals(key1);
        Assert.assertTrue(result == true);

        result = key1.equals(key3);
        Assert.assertTrue(result == false);
    }

    public void testHashCode()
        throws Exception
    {
        int hash1 = key1.hashCode();
        int hash2 = key2.hashCode();

        Assert.assertTrue(hash1 == hash2);
    }
}
