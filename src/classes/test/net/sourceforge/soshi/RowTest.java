package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class RowTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public RowTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(RowTest.class);
    }

    private Configuration config;

    public void setUp()
        throws Exception
    {
        config = new Configuration();
    }

    public void testGetKey()
        throws Exception
    {
        Row row = new Row();

        // make sure we get a null back if no Key defined
        Key key = row.getKey();
        Assert.assertTrue(key == null);

        // Set a key and then make sure it is the same once retrieved
        Key key1 = new NormalKey(config);
        row.setKey(key1);
        Key key2 = row.getKey();
        Assert.assertTrue(key1.equals(key2));

        // Set a different key, make sure that this is returned
        Key key3 = new NormalKey(config);
        row.setKey(key3);
        Key key4 = row.getKey();
        Assert.assertTrue(key4.equals(key3));
    }

    public void testSetKey()
        throws Exception
    {
        // Set tests covered by testGetKey()
    }

    public void testAddColumn()
        throws Exception
    {
        // Add test covered by testGetColumnValue()
    }

    public void testGetColumnValue()
        throws Exception
    {
        Row row = new Row();
        String value;

        //  Try to get a column value which doesn't exist
        value = row.getColumnValue("COL1");
        Assert.assertTrue(value == null);

        // Add a column then make sure it's value is the same once retrieved
        row.addColumn("COL1", "VAL1");
        value = row.getColumnValue("COL1");
        Assert.assertTrue(value.equals("VAL1"));

        // Add a second column and get it's value
        row.addColumn("COL2", "VAL2");
        value = row.getColumnValue("COL2");
        Assert.assertTrue(value.equals("VAL2"));

        // Now get the first value and see if it's still available
        value = row.getColumnValue("COL1");
        Assert.assertTrue(value.equals("VAL1"));
    }
}
