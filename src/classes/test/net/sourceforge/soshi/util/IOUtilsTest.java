package net.sourceforge.soshi.util;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class IOUtilsTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public IOUtilsTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(IOUtilsTest.class);
    }

    public void testGetAsString()
        throws Exception
    {
        Reader reader;
        String result;
        reader = new StringReader("teststring");
        result = IOUtils.getAsString(reader);
        Assert.assertTrue(result.equals("teststring"));

        reader = new StringReader("test\nstring");
        result = IOUtils.getAsString(reader);
        Assert.assertTrue(result.equals("test\nstring"));
    }
}
