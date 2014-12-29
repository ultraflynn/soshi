package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import java.util.Collection;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class ExcelSideTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public ExcelSideTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ExcelSideTest.class);
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

        side = new ExcelSide();
        side.setSideType(Side.SIDEA);

        side.setConfiguration(config);
        side.addParameter("filename", "build/classes/test/net/sourceforge/soshi/excelsidetest.xls");
        side.addParameter("initialSheet", "Sheet2");
        side.addParameter("sheetContinuation", "false");
        side.addParameter("debug", "true");
    }

    public void testGetName()
        throws Exception
    {
        String name;

        name = side.getName();
        Assert.assertTrue(name == null);

        side.setName("name");
        name = side.getName();
        Assert.assertEquals(name, "name");
    }

    public void testMissingConfigurationLoad()
        throws Exception
    {
        Side emptySide = new ExcelSide();
        emptySide.setSideType(Side.SIDEA);

        try {
            emptySide.load();
            fail("No warning about missing configuration");
        } catch (ConfigurationException e) {
            Assert.assertEquals("The configuration must be set", e.getMessage());
        }

        emptySide.setConfiguration(config);

        try {
            emptySide.load();
            fail("No warning about missing filename parameter");
        } catch (ConfigurationException e) {
            Assert.assertEquals("Filename must be specified for Excel Side", e.getMessage());
        }

        emptySide.addParameter("filename", "build/classes/test/net/sourceforge/soshi/excelsidetest.xls");
        emptySide.addParameter("initialSheet", "Sheet1");
        emptySide.addParameter("sheetContinuation", "false");
        emptySide.addParameter("debug", "true");

        emptySide.load();
    }

    public void testLoad()
        throws Exception
    {
        testLoad(2);
    }

    public void testGetRow()
        throws Exception
    {
        side.load();

        Row row = side.getRow(getKey("AAA"));
        Assert.assertTrue(row != null);

        try {
            Key nullKey = null;
            row = side.getRow(nullKey);
            fail("No warning about key being null");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Key is null", e.getMessage());
        }

        try {
            row = side.getRow(new DiskKey(config, 0));
            fail("No warning about Normalkey having to be from a ExcelSide");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Key must be a NormalKey on a ExcelSide", e.getMessage());
        }
    }

    public void testCheckColumnsExist()
        throws Exception
    {
        config.addColumnName("col_d", false, new StandardComparator(), null);

        try {
            side.load();
            fail("ExcelSide load did not warn about missing column");
        } catch (DataException e) {
            String msg = e.getMessage();
            Assert.assertTrue(msg.startsWith("Data source did not contain required columns."));
        }
    }

    public void testLoadContinued()
        throws Exception
    {
        side.addParameter("sheetContinuation", "true");
        testLoad(4);
    }

    public void testForMissingSheet()
        throws Exception
    {
        Side emptySide = new ExcelSide();
        emptySide.setSideType(Side.SIDEA);

        emptySide.setConfiguration(config);
        emptySide.addParameter("filename", "build/classes/test/net/sourceforge/soshi/excelsidetest.xls");
        emptySide.addParameter("initialSheet", "MissingSheet");
        emptySide.addParameter("sheetContinuation", "false");
        emptySide.addParameter("debug", "false");

        try {
            emptySide.load();
            fail("No warning about missing sheet");
        } catch (ConfigurationException e) {
            Assert.assertEquals("The file build/classes/test/net/sourceforge/soshi/excelsidetest.xls does not contain sheet MissingSheet", e.getMessage());
        }
    }

    private Key getKey(String val)
        throws Exception
    {
        Key key = new NormalKey(config);
        key.addColumn("col_a", val);

        return key;
    }

    private void testLoad(int rows)
        throws Exception
    {
        side.load();

        Collection keys = side.getKeys();
        Assert.assertEquals(rows, keys.size());

        if (rows >= 2) {
            Assert.assertTrue(keys.contains(getKey("AAA")));
            Assert.assertTrue(keys.contains(getKey("aaa")));
        }
        if (rows == 4) {
            Assert.assertTrue(keys.contains(getKey("DDD")));
            Assert.assertTrue(keys.contains(getKey("1")));
        }

        Row row;

        if (rows >= 2) {
            row = side.getRow(getKey("AAA"));
            Assert.assertEquals("BBB", row.getColumnValue("col_b"));
            Assert.assertEquals("CCC", row.getColumnValue("col_c"));
            row = side.getRow(getKey("aaa"));
            Assert.assertEquals("bbb", row.getColumnValue("col_b"));
            Assert.assertEquals("ccc", row.getColumnValue("col_c"));
        }
        if (rows == 4) {
            row = side.getRow(getKey("DDD"));
            Assert.assertEquals("EEE", row.getColumnValue("col_b"));
            Assert.assertEquals("FFF", row.getColumnValue("col_c"));
            row = side.getRow(getKey("1"));
            Assert.assertEquals("2.24", row.getColumnValue("col_b"));
            Assert.assertEquals("3", row.getColumnValue("col_c"));
        }
        row = side.getRow(getKey("123"));
        Assert.assertTrue(row == null);

        Collection duplicates = side.getDuplicateKeys();
        Assert.assertTrue(duplicates.size() == 0);
    }
}
