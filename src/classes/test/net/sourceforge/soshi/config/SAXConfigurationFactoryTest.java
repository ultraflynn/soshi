package net.sourceforge.soshi.config;

import net.sourceforge.soshi.Comparator;
import net.sourceforge.soshi.DummySide;
import net.sourceforge.soshi.Side;
import net.sourceforge.soshi.StandardComparator;

import java.net.URL;

import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class SAXConfigurationFactoryTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public SAXConfigurationFactoryTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(SAXConfigurationFactoryTest.class);
    }

    private ConfigurationFactory factory;

    public void setUp()
        throws Exception
    {
        factory = new SAXConfigurationFactory();
    }

    public void testGetConfiguration()
        throws Exception
    {
        URL url = getClass().getClassLoader().getResource("net/sourceforge/soshi/config/testconfig.xml");
        Configuration config = factory.getConfiguration(url);

        String name = config.getName();
        Assert.assertTrue(name.equals("Rec"));

        Comparator comparator = config.getComparator("col1");
        Assert.assertTrue(comparator instanceof StandardComparator);

        Collection columns = config.getColumnNames();
        Assert.assertTrue(columns.size() == 4);
        Assert.assertTrue(columns.contains("col1"));
        Assert.assertTrue(columns.contains("col2"));
        Assert.assertTrue(columns.contains("col3"));
        Assert.assertTrue(columns.contains("col4"));

        Collection keyColumns = config.getKeyColumnNames();
        Assert.assertTrue(keyColumns.size() == 2);
        Assert.assertTrue(keyColumns.contains("col1"));
        Assert.assertTrue(keyColumns.contains("col2"));

        Side sideA = config.getSide(Side.SIDEA);
        Assert.assertTrue(sideA instanceof DummySide);
        DummySide dummyA = (DummySide) sideA;
        Assert.assertTrue(dummyA.getName().equals("Side1"));
        Map paramsA = dummyA.getParameters();
        Assert.assertTrue(paramsA.size() == 2);
        Assert.assertTrue(paramsA.get("param1").equals("value1"));
        Assert.assertTrue(paramsA.get("param2").equals("value2"));

        Side sideB = config.getSide(Side.SIDEB);
        Assert.assertTrue(sideB instanceof DummySide);
        DummySide dummyB = (DummySide) sideB;
        Assert.assertTrue(dummyB.getName().equals("Side2"));
        Map paramsB = dummyB.getParameters();
        Assert.assertTrue(paramsB.size() == 2);
        Assert.assertTrue(paramsB.get("param3").equals("value3"));
        Assert.assertTrue(paramsB.get("param4").equals("value4"));
    }

    public void testMissingName()
    {
        URL url = getClass().getClassLoader().getResource("net/sourceforge/soshi/config/testconfig-missingname.xml");
        try {
            Configuration config = factory.getConfiguration(url);
            fail("No ConfigurationException thrown despite missing rec name");
        } catch (ConfigurationException e) {
        }
    }
}
