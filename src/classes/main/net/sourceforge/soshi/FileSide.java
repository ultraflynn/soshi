package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import net.sourceforge.soshi.util.HashMultiMap;
import net.sourceforge.soshi.util.MultiMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FileSide implements Side
{
    private String name;
    private int type;
    private Configuration config;
    private Map rows;
    private MultiMap allRows;
    private Collection duplicates;
    private String filename;

    public FileSide()
    {
        rows = new HashMap();
        allRows = new HashMultiMap();
        duplicates = new HashSet();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getSideType()
    {
        return type;
    }

    public void setSideType(int type)
    {
        this.type = type;
    }

    public void setConfiguration(Configuration config)
    {
        this.config = config;
    }

    public void addParameter(String name, String value)
    {
        if (name != null) {
            if (name.equals("filename")) {
                filename = value;
            }
        }
    }

    public void load()
        throws DataException, ConfigurationException
    {
        if (config == null) {
            throw new ConfigurationException("The configuration must be set");
        }
        if (filename == null || filename.equals("")) {
            throw new ConfigurationException("Filename must be specified");
        }

        try {
            InputStream is = new FileInputStream(filename);
            Digester digester = new FileSideDigester();
            digester.parse(is);

            for (Iterator i = duplicates.iterator(); i.hasNext();) {
                Key key = (Key) i.next();
                rows.remove(key);
            }
        } catch (FileNotFoundException e) {
            throw new DataException("File specified can not be found");
        } catch (IOException e) {
            throw new DataException("There was a problem reading the file");
        } catch (SAXException e) {
            throw new DataException("There was a problem parsing the file");
        }
    }

    public Row getRow(Key key)
    {
        if (key == null) {
            throw new NullPointerException("Key is null");
        }

        if (!(key instanceof NormalKey)) {
            throw new IllegalArgumentException("Key must be a NormalKey");
        }

        return (Row) rows.get(key);
    }

    public Collection getAllRows(Key key)
    {
        if (key == null) {
            throw new NullPointerException("Key is null");
        }

        if (!(key instanceof NormalKey)) {
            throw new IllegalArgumentException("Key must be a NormalKey");
        }

        Collection results = allRows.get(key);
        if (results == null) {
            results = new ArrayList();
        }

        return results;
    }

    public Collection getKeys()
    {
        return Collections.unmodifiableCollection(rows.keySet());
    }

    public Collection getDuplicateKeys()
    {
        return Collections.unmodifiableCollection(duplicates);
    }

    private class FileSideDigester extends Digester
    {
        public FileSideDigester()
        {
            addRule("*/row", new RowRule());
            addRule("*/row/column", new ColumnRule());
        }

        private class RowRule extends Rule
        {
            public void begin(Attributes attributes)
            {
                Key key = new NormalKey(config);
                Row row = new Row();
                row.setKey(key);
                digester.push(row);
            }

            public void end()
            {
                Row row = (Row) digester.pop();
                Key key = row.getKey();
                allRows.put(key, row);
                if (rows.containsKey(key)) {
                    duplicates.add(key);
                } else {
                    rows.put(key, row);
                }
            }
        }

        private class ColumnRule extends Rule
        {
            private Collection keyColumns;
            private Collection columns;
            private Collection infoColumns;

            public ColumnRule()
            {
                keyColumns = config.getKeyColumnNames();
                columns = config.getColumnNames();
                infoColumns = config.getInfoColumnNames(type);
            }

            public void begin(Attributes attributes)
            {
                Row row = (Row) digester.peek();
                Key key = row.getKey();

                String name = attributes.getValue("name");
                String value = attributes.getValue("value");

                if (columns.contains(name) || infoColumns.contains(name)) {
                    if (keyColumns.contains(name)) {
                        key.addColumn(name, value);
                    }
                    row.addColumn(name, value);
                }
            }
        }
    }
}
