package net.sourceforge.soshi;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class StandardComparatorTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public StandardComparatorTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(StandardComparatorTest.class);
    }

    public void testEquals()
        throws Exception
    {
        Comparator comparator1 = new StandardComparator();
        Assert.assertTrue(comparator1.equals("test1","test1"));
        Assert.assertTrue(comparator1.equals("test1","test2") == false);
        Assert.assertTrue(comparator1.equals("test2","test1") == false);
        Assert.assertTrue(comparator1.equals("test\n1","test\n1"));
    }
    
    public void testGetCanonicalForm()
    	throws Exception
    {
        Comparator comparator1 = new StandardComparator();
        Assert.assertTrue(comparator1.getCanonicalForm("test1").equals("test1"));
        Assert.assertTrue(comparator1.getCanonicalForm("test\n1").equals("test\n1"));
    }
 
}
