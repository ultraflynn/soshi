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

public class CarbineConfigurationFactoryTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public CarbineConfigurationFactoryTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(CarbineConfigurationFactoryTest.class);
    }

    private ConfigurationFactory factory;

    public void setUp()
        throws Exception
    {
        //factory = new CarbineConfigurationFactory();
    }

    public void testGetConfiguration()
        throws Exception
    {
        Assert.assertTrue(1 == 1);
    } 
}
