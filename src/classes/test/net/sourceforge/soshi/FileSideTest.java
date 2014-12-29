package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import java.util.Collection;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class FileSideTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public FileSideTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(FileSideTest.class);
    }

    private Side side;
    private Configuration config;

    public void setUp()
        throws Exception
    {
        config = new Configuration();
        config.addColumnName("col_a", true, new StandardComparator(), null);
        config.addColumnName("col_b", false, new StandardComparator(), null);
        config.addColumnName("col_c", false, new StandardComparator(), null);

        side = new FileSide();
        side.setSideType(Side.SIDEA);

        side.setConfiguration(config);
        side.addParameter("filename", "build/classes/test/net/sourceforge/soshi/FileSideTest.xml");
    }

    public void testGetName()
        throws Exception
    {
        String name;

        name = side.getName();
        Assert.assertTrue(name == null);

        side.setName("name");
        name = side.getName();
        Assert.assertTrue(name.equals("name"));
    }

    public void testMissingConfigurationLoad()
        throws Exception
    {
        Side emptySide = new FileSide();
        emptySide.setSideType(Side.SIDEA);

        try {
            emptySide.load();
            fail("No warning about missing configuration");
        } catch (ConfigurationException e) {
        }

        emptySide.setConfiguration(config);

        try {
            emptySide.load();
            fail("No warning about missing filename parameter");
        } catch (ConfigurationException e) {
        }

        emptySide.addParameter("filename", "build/classes/test/net/sourceforge/soshi/FileSideTest.xml");

        emptySide.load();
    }

    public void testGetRow()
        throws Exception
    {
        side.load();

        Row row = side.getRow(getKey("AAA"));
        Assert.assertTrue(row != null);

        try {
            row = side.getRow(null);
            fail("No warning about key being null");
        } catch (NullPointerException e) {
        }

        try {
            row = side.getRow(new DiskKey(config, 0));
            fail("No warning about FileSide only accepting a Normalkey");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testLoad()
        throws Exception
    {
        side.load();

        Collection keys = side.getKeys();
        Assert.assertTrue(keys.size() == 2);
        Assert.assertTrue(keys.contains(getKey("AAA")));
        Assert.assertTrue(keys.contains(getKey("aaa")));

        Row row;
        row = side.getRow(getKey("AAA"));
        Assert.assertTrue(row.getColumnValue("col_b").equals("BBB"));
        Assert.assertTrue(row.getColumnValue("col_c").equals("CCC"));
        row = side.getRow(getKey("aaa"));
        Assert.assertTrue(row.getColumnValue("col_b").equals("bbb"));
        Assert.assertTrue(row.getColumnValue("col_c").equals("ccc"));
        row = side.getRow(getKey("123"));
        Assert.assertTrue(row == null);

        Collection duplicates = side.getDuplicateKeys();
        Assert.assertTrue(duplicates.size() == 0);
    }

    private Key getKey(String val)
        throws Exception
    {
        Key key = new NormalKey(config);
        key.addColumn("col_a", val);

        return key;
    }
}
