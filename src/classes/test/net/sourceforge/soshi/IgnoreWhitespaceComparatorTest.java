package net.sourceforge.soshi;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class IgnoreWhitespaceComparatorTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public IgnoreWhitespaceComparatorTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(IgnoreWhitespaceComparatorTest.class);
    }

    private Comparator comparator;

    public void setUp()
        throws Exception
    {
        comparator = new IgnoreWhitespaceComparator();
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

        result = comparator.equals("a", "a ");
        Assert.assertTrue(result == true);

        result = comparator.equals("a", "\t\n\t   a\t\t\n\r");
        Assert.assertTrue(result == true);
    }

    public void testGetCanonicalForm()
        throws Exception
    {
        String result;

        result = comparator.getCanonicalForm(null);
        Assert.assertTrue(result == null);

        result = comparator.getCanonicalForm("a");
        Assert.assertTrue(result.equals("a"));

        result = comparator.getCanonicalForm("  a   ");
        Assert.assertTrue(result.equals("a"));
    }
}
