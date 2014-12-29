package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase; 
import junit.framework.TestSuite;

import junit.textui.TestRunner;


public class ResultsTest extends TestCase
{
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    public ResultsTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ResultsTest.class);
    }
    
    private Results results1;
    private Results results2;
    private Configuration config1;
    private Configuration config2;    
    private KeyPair key1;
    private KeyPair key2;
    private KeyPair key3;
    private Side side1;
    private Side side2;
    
    public void setUp()
        throws Exception
    {
        results1 = new Results();
        results2 = new Results();  
        config1 = new Configuration();
        results1.setConfiguration(config1);
        key1 = new KeyPair(new NormalKey(config1), new NormalKey(config1));
        key2 = new KeyPair(new NormalKey(config1), new NormalKey(config1));
        key3 = new KeyPair(new NormalKey(config1), new NormalKey(config1));
        side1 = new DummySide();
    
        config1.addColumnName("cola", true, new StandardComparator(), null);
        config1.addColumnName("colb", true, new StandardComparator(), null);
    
        key1 = new KeyPair(new NormalKey(config1), new NormalKey(config1));
        key1.getKey(Side.SIDEA).addColumn("cola", "1");
        key1.getKey(Side.SIDEB).addColumn("cola", "1");
        key1.getKey(Side.SIDEA).addColumn("colb", "2");
        key1.getKey(Side.SIDEB).addColumn("colb", "2");
    
        key2 = new KeyPair(new NormalKey(config1), new NormalKey(config1));
        key2.getKey(Side.SIDEA).addColumn("cola", "3");
        key2.getKey(Side.SIDEB).addColumn("cola", "3");
        key2.getKey(Side.SIDEA).addColumn("colb", "4");
        key2.getKey(Side.SIDEB).addColumn("colb", "4");
    
        key3 = new KeyPair(new NormalKey(config1), new NormalKey(config1));
        key3.getKey(Side.SIDEA).addColumn("cola", "5");
        key3.getKey(Side.SIDEB).addColumn("cola", "5");
        key3.getKey(Side.SIDEA).addColumn("colb", "6");
        key3.getKey(Side.SIDEB).addColumn("colb", "6");

    }

    public void testGetConfiguration()
        throws Exception
    {
        results1.setConfiguration(config1);
        config2 = results1.getConfiguration();
        Assert.assertTrue(config1.equals(config2));
    }
    
    public void testGetSide()
        throws Exception
    {
        results1.setSide(Side.SIDEA, side1);
        side2 = results1.getSide(Side.SIDEA);
        Assert.assertTrue(side2.equals(side1));
        
        results1.setSide(Side.SIDEB, side1);
        side2 = results1.getSide(Side.SIDEB);
        Assert.assertTrue(side2.equals(side1));
    
    try {
        int wrongtype = 12;
        results1.getSide(wrongtype);
        fail("getSide accepted an invalid sideType");
    } catch (IllegalArgumentException e) {
    }

    try {
        results1.setSide(12, side1);
        fail("setSide accepted an invalid sideType");
    } catch (IllegalArgumentException e) {
    }      
      
    // Test that null value returned if getSide called before setSide    
    Assert.assertTrue(results2.getSide(Side.SIDEA) == null);
        
    }

    public void testGetMatchedKeys()
    throws Exception
    {
        Assert.assertTrue(results2.getMatchedKeys().isEmpty());

        results1.addMatchedKey(key1);
        Assert.assertTrue(results1.getMatchedKeys().size() == 1); 
        Assert.assertTrue(results1.getMatchedKeys().contains(key1));    
        results1.addMatchedKey(key2);
        Assert.assertTrue(results1.getMatchedKeys().size() == 2); 
        Assert.assertTrue(results1.getMatchedKeys().contains(key2));        
    }

    public void testGetMissingKeys()
        throws Exception
    {
        results1.addMissingKey(Side.SIDEA, key1.getKey(Side.SIDEA));
        Collection missingAKeys = results1.getMissingKeys(Side.SIDEA);
        Assert.assertTrue(missingAKeys.size() == 1); 
        Assert.assertTrue(missingAKeys.contains(key1.getKey(Side.SIDEA)));
        
        results1.addMissingKey(Side.SIDEB, key2.getKey(Side.SIDEB));
        Collection missingBKeys = results1.getMissingKeys(Side.SIDEB);
        Assert.assertTrue(missingBKeys.size() == 1); 
        Assert.assertTrue(missingBKeys.contains(key2.getKey(Side.SIDEB))); 
    }

    public void testAddDifference()
        throws Exception
    {
        results1.addDifference(key1, "cola", "diff1", "diff2");
        results1.addDifference(key2, "cola", null, "diff3");
        
        Map differencesByRow = results1.getDifferencesByRow();
        Assert.assertTrue(differencesByRow.size() == 2);
        Assert.assertTrue(differencesByRow.containsKey(key1));   
        Assert.assertTrue(differencesByRow.containsKey(key2));    
        
        Set set = results1.getDifferencesForRow(key1);
        Assert.assertTrue(set.size() == 1);
        for (Iterator i = set.iterator(); i.hasNext();) {
            Difference diff = (Difference) i.next();
            Assert.assertTrue(diff.getKeyPair().equals(key1));
            Assert.assertTrue(diff.getColumn().equals("cola"));   
            Assert.assertTrue(diff.getValueA().equals("diff1"));   
            Assert.assertTrue(diff.getValueB().equals("diff2"));   
        } 
    
        set = results1.getDifferencesForRow(key2);
        Assert.assertTrue(set.size() == 1);
        for (Iterator i = set.iterator(); i.hasNext();) {
            Difference diff = (Difference) i.next();
            Assert.assertTrue(diff.getKeyPair().equals(key2));
            Assert.assertTrue(diff.getColumn().equals("cola"));   
            Assert.assertTrue(diff.getValueA() == null);   
            Assert.assertTrue(diff.getValueB().equals("diff3"));   
        } 
        
        Map differencesByColumn = results1.getDifferencesByColumn();
        Assert.assertTrue(differencesByColumn.size() == 1);
        Assert.assertTrue(differencesByColumn.containsKey("cola")); 
        
        set = results1.getDifferencesForColumn("cola");
        Assert.assertTrue(set.size() == 2);
        for (Iterator i = set.iterator(); i.hasNext();) {
            Difference diff = (Difference) i.next();
                if (diff.getKeyPair().equals(key1)) {
            Assert.assertTrue(diff.getColumn().equals("cola"));   
            Assert.assertTrue(diff.getValueA().equals("diff1"));   
            Assert.assertTrue(diff.getValueB().equals("diff2"));   
            } else if (diff.getKeyPair().equals(key2)) {
            Assert.assertTrue(diff.getColumn().equals("cola"));   
            Assert.assertTrue(diff.getValueA() == null);   
            Assert.assertTrue(diff.getValueB().equals("diff3")); 
            }
        } 
        
        try {
            set = results1.getDifferencesForColumn("invalid string");
            fail("getDifferences for Column accepted an invalid argument");
        } catch (IllegalArgumentException e) {
        }

    }

    private boolean contains(Collection p, Key keyA, Key keyB)
    {
        boolean result = false;

        for (Iterator i = p.iterator(); i.hasNext();) {
            KeyPair o = (KeyPair) i.next();

            if (o.getKey(Side.SIDEA).equals(keyA) && o.getKey(Side.SIDEB).equals(keyB)) {
                result = true;
                break;
            }
        }

        return result;
    }    
}
