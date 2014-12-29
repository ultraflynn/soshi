package net.sourceforge.soshi;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class IgnoreNullsAndWhitespaceComparatorTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public IgnoreNullsAndWhitespaceComparatorTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(IgnoreNullsAndWhitespaceComparatorTest.class);
    }

    private Comparator comparator;

    public void setUp()
        throws Exception
    {
        comparator = new IgnoreNullsAndWhitespaceComparator();
    }

    public void testEquals()
        throws Exception
    {
        boolean result;

        result = comparator.equals("", null);
        Assert.assertTrue(result == true);

        result = comparator.equals("", "");
        Assert.assertTrue(result == true);

        result = comparator.equals(null, "");
        Assert.assertTrue(result == true);

        result = comparator.equals(null, null);
        Assert.assertTrue(result == true);

        result = comparator.equals("a", null);
        Assert.assertTrue(result == false);

        result = comparator.equals("a", "");
        Assert.assertTrue(result == false);

        result = comparator.equals(null, "b");
        Assert.assertTrue(result == false);

        result = comparator.equals("", "b");
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
        Assert.assertTrue(result.equals(""));

        result = comparator.getCanonicalForm("");
        Assert.assertTrue(result.equals(""));

        result = comparator.getCanonicalForm("a");
        Assert.assertTrue(result.equals("a"));

        result = comparator.getCanonicalForm("  a   ");
        Assert.assertTrue(result.equals("a"));
    }
}
