package net.sourceforge.soshi.config;

import net.sourceforge.soshi.Comparator;
import net.sourceforge.soshi.DummySide;
import net.sourceforge.soshi.Side;
import net.sourceforge.soshi.StandardComparator;

import java.util.Collection;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class ConfigurationTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public ConfigurationTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ConfigurationTest.class);
    }

    private Configuration config;

    public void setUp()
        throws Exception
    {
        config = new Configuration();
    }

    public void testGetName()
        throws Exception
    {
        config.setName("name");
        String name = config.getName();
        Assert.assertTrue(name.equals("name"));
    }

    public void testGetComparator()
        throws Exception
    {
        Assert.assertTrue(config.getComparator("a") == null);
        Assert.assertTrue(config.getComparator("b") == null);
        Assert.assertTrue(config.getComparator("c") == null);

        Comparator comparator1 = new StandardComparator();
        Comparator comparator2 = new StandardComparator();
        Comparator comparator3 = new StandardComparator();

        config.addColumnName("a", true, comparator1, null);
        Assert.assertTrue(config.getComparator("a").equals(comparator1));
        Assert.assertTrue(config.getComparator("b") == null);
        Assert.assertTrue(config.getComparator("c") == null);

        config.addColumnName("b", true, null, null);
        Assert.assertTrue(config.getComparator("a").equals(comparator1));
        Assert.assertTrue(config.getComparator("b") == null);
        Assert.assertTrue(config.getComparator("c") == null);

        config.addColumnName("c", false, comparator3, null);
        Assert.assertTrue(config.getComparator("a").equals(comparator1));
        Assert.assertTrue(config.getComparator("b") == null);
        Assert.assertTrue(config.getComparator("c").equals(comparator3));
    }

    public void testGetColumnNames()
        throws Exception
    {
        Collection names;

        names = config.getColumnNames();
        Assert.assertTrue(names.size() == 0);

        config.addColumnName("a", true, null, "adescr");
        names = config.getColumnNames();
        Assert.assertTrue(names.size() == 1);
        Assert.assertTrue(names.contains("a"));

        config.addColumnName("b", true, new StandardComparator(), null);
        names = config.getColumnNames();
        Assert.assertTrue(names.size() == 2);
        Assert.assertTrue(names.contains("a"));
        Assert.assertTrue(names.contains("b"));

        try {
            names.add("d");
            fail("We were able to modifiy the columns");
        } catch (UnsupportedOperationException e) {
        }
    }

    public void testGetKeyColumnNames()
        throws Exception
    {
        Collection keys;

        keys = config.getKeyColumnNames();
        Assert.assertTrue(keys.size() == 0);

        config.addColumnName("a", false, null, "adescr");
        keys = config.getKeyColumnNames();
        Assert.assertTrue(keys.size() == 0);

        config.addColumnName("b", true, null, null);
        keys = config.getKeyColumnNames();
        Assert.assertTrue(keys.size() == 1);
        Assert.assertTrue(keys.contains("b"));

        config.addColumnName("c", true, null, null);
        keys = config.getKeyColumnNames();
        Assert.assertTrue(keys.size() == 2);
        Assert.assertTrue(keys.contains("b"));
        Assert.assertTrue(keys.contains("c"));

        config.addColumnName("d", false, null, null);
        keys = config.getKeyColumnNames();
        Assert.assertTrue(keys.size() == 2);
        Assert.assertTrue(keys.contains("b"));
        Assert.assertTrue(keys.contains("c"));

        try {
            keys.add("d");
            fail("We were able to modifiy the keys");
        } catch (UnsupportedOperationException e) {
        }
    }

    public void testGetDescription()
        throws Exception
    {
        Assert.assertTrue(config.getDescription("a").equals("a"));
        Assert.assertTrue(config.getDescription("b").equals("b"));

        config.addColumnName("a", true, null, "adescr");
        Assert.assertTrue(config.getDescription("a").equals("adescr"));
        Assert.assertTrue(config.getDescription("b").equals("b"));

        config.addColumnName("b", false, null, "bdescr");
        Assert.assertTrue(config.getDescription("a").equals("adescr"));
        Assert.assertTrue(config.getDescription("b").equals("bdescr"));
    }

    public void testGetSide()
        throws Exception
    {
        Side side;

        try {
            config.getSide(123);
            fail("No IllegalArgumentException thrown");
        } catch (IllegalArgumentException e) {
        }

        side = config.getSide(Side.SIDEA);
        Assert.assertTrue(side == null);
        side = config.getSide(Side.SIDEB);
        Assert.assertTrue(side == null);

        Side dummySideA = new DummySide();
        config.addSide(Side.SIDEA, dummySideA);
        side = config.getSide(Side.SIDEA);
        Assert.assertTrue(side.equals(dummySideA));
        side = config.getSide(Side.SIDEB);
        Assert.assertTrue(side == null);

        Side dummySideB = new DummySide();
        config.addSide(Side.SIDEB, dummySideB);
        side = config.getSide(Side.SIDEA);
        Assert.assertTrue(side.equals(dummySideA));
        side = config.getSide(Side.SIDEB);
        Assert.assertTrue(side.equals(dummySideB));
    }

    public void testAddSide()
        throws Exception
    {
        Side side;
        Side dummySide = new DummySide();

        try {
            config.addSide(123, dummySide);
            fail("No IllegalArgumentException thrown");
        } catch (IllegalArgumentException e) {
        }

        side = config.getSide(Side.SIDEA);
        Assert.assertTrue(side == null);
        side = config.getSide(Side.SIDEB);
        Assert.assertTrue(side == null);
    }
}
