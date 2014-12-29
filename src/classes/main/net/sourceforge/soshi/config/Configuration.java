package net.sourceforge.soshi.config;

import net.sourceforge.soshi.Comparator;
import net.sourceforge.soshi.Side;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Configuration
{
    private String name;
    private Map comparators;
    private Collection columns;
    private Collection keyColumns;
    private Collection nonKeyColumns;
    private Collection sideAInfoColumns;
    private Collection sideBInfoColumns;
    private Map sideAInfoColumnDescriptions;
    private Map sideBInfoColumnDescriptions;
    private Map descriptions;
    private Side sideA;
    private Side sideB;

    public Configuration()
    {
        comparators = new HashMap();
        columns = new ArrayList();
        keyColumns = new HashSet();
        nonKeyColumns = new HashSet();
        descriptions = new HashMap();
        sideAInfoColumns = new ArrayList();
        sideBInfoColumns = new ArrayList();
        sideAInfoColumnDescriptions = new HashMap();
        sideBInfoColumnDescriptions = new HashMap();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Comparator getComparator(String column)
    {
        return (Comparator) comparators.get(column);
    }

    public Collection getColumnNames()
    {
        return Collections.unmodifiableCollection(columns);
    }

    public void addColumnName(String column, boolean isKey, Comparator comparator, String descr)
    {
        columns.add(column);
        comparators.put(column, comparator);
        if (isKey) {
            keyColumns.add(column);
        } else {
            nonKeyColumns.add(column);
        }
        if (descr != null) {
            descriptions.put(column, descr);
        }
    }

    public Collection getKeyColumnNames()
    {
        return Collections.unmodifiableCollection(keyColumns);
    }

    public Collection getNonKeyColumnNames()
    {
        return Collections.unmodifiableCollection(nonKeyColumns);
    }

    public String getDescription(String column)
    {
        String result = (String) descriptions.get(column);

        if (result == null) {
            result = column;
        }

        return result;
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

    public void addSide(int sideType, Side side)
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

    public void addInfoColumnName(int sideType, String infoColumn, String description)
    {
        switch (sideType) {
            case Side.SIDEA:
                sideAInfoColumns.add(infoColumn);
                sideAInfoColumnDescriptions.put(infoColumn, description);
                break;
            case Side.SIDEB:
                sideBInfoColumns.add(infoColumn);
                sideBInfoColumnDescriptions.put(infoColumn, description);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Collection getInfoColumnNames(int sideType)
    {
        Collection result;

        switch (sideType) {
            case Side.SIDEA:
                result = Collections.unmodifiableCollection(sideAInfoColumns);
                break;
            case Side.SIDEB:
                result = Collections.unmodifiableCollection(sideBInfoColumns);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return result;
    }

    public String getInfoColumnDescription(int sideType, String column)
    {
        String result;

        switch (sideType) {
            case Side.SIDEA:
                result = (String) sideAInfoColumnDescriptions.get(column);
                break;
            case Side.SIDEB:
                result = (String) sideBInfoColumnDescriptions.get(column);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return result;
    }
}
