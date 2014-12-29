package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Results
{
    private Configuration config;
    private Side sideA;
    private Side sideB;
    private Collection matchedKeys;
    private Collection aMissing;
    private Collection bMissing;
    private Map rowDifferences;
    private Map columnDifferences;
    private Collection aDuplicates;
    private Collection bDuplicates;
    private Collection aExcluded;
    private Collection bExcluded;
    
    public Results()
    {
        matchedKeys = new HashSet();
        aMissing = new HashSet();
        bMissing = new HashSet();
        rowDifferences = new HashMap();
        columnDifferences = new HashMap();
        aDuplicates = new HashSet();
        bDuplicates = new HashSet();
        aExcluded = new HashSet();
        bExcluded = new HashSet();
    }

    public Configuration getConfiguration()
    {
        return config;
    }

    public void setConfiguration(Configuration config)
    {
        this.config = config;
    }

    public Side getSide(int sideType)
    {
        Side side;

        switch (sideType) {
        case Side.SIDEA:
            side = sideA;
            break;
        case Side.SIDEB:
            side = sideB;
            break;
        default:
            throw new IllegalArgumentException();
        }

        return side;
    }

    public void setSide(int sideType, Side side)
    {
        switch (sideType) {
        case Side.SIDEA:
            sideA = side;
            break;
        case Side.SIDEB:
            sideB = side;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public Collection getMatchedKeys()
    {
        return Collections.unmodifiableCollection(matchedKeys);
    }

    public void addMatchedKey(KeyPair pair)
    {
        matchedKeys.add(pair);
    }

    public Collection getMissingKeys(int sideType)
    {
        Collection results;

        switch (sideType) {
            case Side.SIDEA:
                results = Collections.unmodifiableCollection(aMissing);
                break;
            case Side.SIDEB:
                results = Collections.unmodifiableCollection(bMissing);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return results;
    }

    public void addMissingKey(int sideType, Key key)
    {
        switch (sideType) {
            case Side.SIDEA:
                aMissing.add(key);
                break;
            case Side.SIDEB:
                bMissing.add(key);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public Collection getDuplicateKeys(int sideType)
    {
        Collection results;

        switch (sideType) {
            case Side.SIDEA:
                results = Collections.unmodifiableCollection(aDuplicates);
                break;
            case Side.SIDEB:
                results = Collections.unmodifiableCollection(bDuplicates);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return results;
    }

    public void addDuplicateKey(int sideType, Key key)
    {
        switch (sideType) {
            case Side.SIDEA:
                aDuplicates.add(key);
                break;
            case Side.SIDEB:
                bDuplicates.add(key);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public Collection getExcluded(int sideType)
    {
        Collection results;

        switch (sideType) {
            case Side.SIDEA:
                results = Collections.unmodifiableCollection(aExcluded);
                break;
            case Side.SIDEB:
                results = Collections.unmodifiableCollection(bExcluded);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return results;
    }

    public void addExclusion(int sideType, Key key)
    {
        switch (sideType) {
            case Side.SIDEA:
                aExcluded.add(key);
                break;
            case Side.SIDEB:
                bExcluded.add(key);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public Map getDifferencesByRow()
    {
        return Collections.unmodifiableMap(rowDifferences);
    }

    public Set getDifferencesForRow(KeyPair keys)
    {
        Set result = (Set) rowDifferences.get(keys);
        if (result == null) {
            result = new HashSet();
        }

        return Collections.unmodifiableSet(result);
    }

    public Map getDifferencesByColumn()
    {
        return Collections.unmodifiableMap(columnDifferences);
    }

    public Set getDifferencesForColumn(String column)
    {
        if (config == null) {
            throw new IllegalStateException("Configuration has not been set");
        } else {
            if (!(config.getColumnNames().contains(column))) {
                throw new IllegalArgumentException("Unknown column: " + column);
            }
        }
        
        Set result = (Set) columnDifferences.get(column);
        if (result == null) {
            result = new HashSet();
        }

        return Collections.unmodifiableSet(result);
    }

    public void addDifference(KeyPair keys, String column, String valueA, String valueB)
    {
        if (config == null) {
            throw new IllegalStateException("Configuration has not been set");
        } else {
            if (!(config.getColumnNames().contains(column))) {
                throw new IllegalArgumentException("Unknown column: " + column);
            }
        }
        
        Set rowDifference = (Set) rowDifferences.get(keys);
        if (rowDifference == null) {
            rowDifference = new HashSet();
            rowDifferences.put(keys, rowDifference);
        }

        Set columnDifference = (Set) columnDifferences.get(column);
        if (columnDifference == null) {
            columnDifference = new HashSet();
            columnDifferences.put(column, columnDifference);
        }

        Difference difference = new Difference(keys, column, valueA, valueB);
        rowDifference.add(difference);
        columnDifference.add(difference);
    }
}
