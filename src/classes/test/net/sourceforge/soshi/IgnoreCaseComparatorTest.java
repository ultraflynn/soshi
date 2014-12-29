package net.sourceforge.soshi;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class IgnoreCaseComparatorTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public IgnoreCaseComparatorTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(IgnoreCaseComparatorTest.class);
    }

    private Comparator comparator;

    public void setUp()
        throws Exception
    {
        comparator = new IgnoreCaseComparator();
    }

    public void testEquals()
        throws Exception
    {
        boolean result;

        result = comparator.equals(null, null);
        Assert.assertTrue(result == true);

        result = comparator.equals("a", null);
        Assert.assertTrue(result == false);

        result = comparator.equals(null, "b");
        Assert.assertTrue(result == false);

        result = comparator.equals("a", "a");
        Assert.assertTrue(result == true);

        result = comparator.equals("a", "b");
        Assert.assertTrue(result == false);

        result = comparator.equals("a", "A");
        Assert.assertTrue(result == true);

        result = comparator.equals("A", "A");
        Assert.assertTrue(result == true);
    }

    public void testGetCanonicalForm()
        throws Exception
    {
        String result;

        result = comparator.getCanonicalForm(null);
        Assert.assertTrue(result == null);

        result = comparator.getCanonicalForm("a");
        Assert.assertTrue(result.equals("A"));

        result = comparator.getCanonicalForm("A");
        Assert.assertTrue(result.equals("A"));
    }
}
