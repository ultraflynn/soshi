package net.sourceforge.soshi;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class IgnoreCaseAndWhitespaceComparatorTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public IgnoreCaseAndWhitespaceComparatorTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(IgnoreCaseAndWhitespaceComparatorTest.class);
    }

    private Comparator comparator;

    public void setUp()
        throws Exception
    {
        comparator = new IgnoreCaseAndWhitespaceComparator();
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

        result = comparator.equals("a", "A");
        Assert.assertTrue(result == true);

        result = comparator.equals("A", "a");
        Assert.assertTrue(result == true);

        result = comparator.equals("a", "b");
        Assert.assertTrue(result == false);

        result = comparator.equals("a", "a ");
        Assert.assertTrue(result == true);

        result = comparator.equals("A", "a ");
        Assert.assertTrue(result == true);

        result = comparator.equals("a", "A ");
        Assert.assertTrue(result == true);

        result = comparator.equals("a ", "a");
        Assert.assertTrue(result == true);

        result = comparator.equals("A ", "a");
        Assert.assertTrue(result == true);

        result = comparator.equals("a ", "A");
        Assert.assertTrue(result == true);

        result = comparator.equals(" a", "a");
        Assert.assertTrue(result == true);

        result = comparator.equals(" A", "a");
        Assert.assertTrue(result == true);

        result = comparator.equals(" a", "A");
        Assert.assertTrue(result == true);

        result = comparator.equals(" a a ", "aa");
        Assert.assertTrue(result == true);

        result = comparator.equals(" A A ", "aa");
        Assert.assertTrue(result == true);

        result = comparator.equals(" a a ", "AA");
        Assert.assertTrue(result == true);

        result = comparator.equals("\t\n\t   a\t\t\n\r", "a");
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
        Assert.assertTrue(result.equals("A"));

        result = comparator.getCanonicalForm("  a   ");
        Assert.assertTrue(result.equals("A"));

        result = comparator.getCanonicalForm("  a      a  ");
        Assert.assertTrue(result.equals("AA"));
    }
}
