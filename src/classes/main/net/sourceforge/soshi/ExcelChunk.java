package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import net.sourceforge.soshi.util.CollectionUtils;

import java.text.DecimalFormat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExcelChunk
{
    private int type;
    private Configuration config;
    private ExcelSide side;

    private int currentRow = 0;
    private Map columnNames;
    private boolean firstHeaders = true;
    private Map data;
    private boolean columnsChecked = false;

    private Collection keyColumns;
    private Collection columns;
    private Collection infoColumns;

    public ExcelChunk(int type, Configuration config, ExcelSide side)
    {
        data = new HashMap();

        this.type = type;
        this.config = config;
        this.side = side;

        keyColumns = config.getKeyColumnNames();
        columns = config.getColumnNames();
        infoColumns = config.getInfoColumnNames(type);
    }

    public void put(int row, int column, String value)
        throws DataException
    {
        if (row == 0) {
            if (currentRow >= 1) {
                process();
            }

            if (firstHeaders || columnNames == null) {
                if (columnNames == null) {
                    columnNames = new HashMap();
                }

                columnNames.put(value, new Integer(column));
            } else {
                checkAdditionalColumnName(column, value);
            }
        } else {
            if (row > currentRow) {
                firstHeaders = false;

                if (currentRow >= 1) {
                    process();
                }
            }

            data.put(new Integer(column), value);
        }

        currentRow = row;
    }

    public void put(int row, int column, double value)
        throws DataException
    {
        String s = null;

        /*
         * If you can round the value and get the same value then there are no decimal places and we can convert
         * the value to # format. Otherwise just convert the decimal to a string keeping the decimal places.
         */
        if (Math.round(value) == value) {
            DecimalFormat df = new DecimalFormat("#");
            s = df.format(value);
        } else {
            s = Double.toString(value);
        }
        
        put(row, column, s);
    }

    public void finished()
        throws DataException
    {
        process();
    }

    private void process()
        throws DataException
    {
        if (!columnsChecked) {
            checkColumnsExist(columnNames, config);
            columnsChecked = true;
        }

        Key key = new NormalKey(config);
        Row row = new Row();

        for (Iterator i = columns.iterator(); i.hasNext();) {
            String name = (String) i.next();
            String value = getValue(name);
            
            if (keyColumns.contains(name)) {
                /*
                 * We trim the value going into the string because it wont constitute the actual key. On a SQLSide
                 * this would be a varchar and therefore wouldn't have trailing spaces.
                 */
                if (value != null) {
                    value = value.trim();
                }

                key.addColumn(name, value);
            }

            row.addColumn(name, value);
        }

        for (Iterator i = infoColumns.iterator(); i.hasNext();) {
            String name = (String) i.next();
            String value = getValue(name);
            row.addColumn(name, value);
        }
        
        side.addRow(key, row);

        if (side.containsRowKey(key)) {
            side.addDuplicate(key);
        } else {
            row.setKey(key);
            side.putRow(key, row);
        }

        data.clear();
    }

    private String getValue(String columnName)
    {
        Integer a = (Integer) columnNames.get(columnName);
        return (String) data.get(a);
    }

    private void checkColumnsExist(Map columnNames, Configuration config)
        throws DataException
    {
        Collection names = columnNames.keySet();
        Collection required = config.getColumnNames();

        for (Iterator i = names.iterator(); i.hasNext();) {
            String name = (String) i.next();
        }
        
        Collection missing = CollectionUtils.subtract(required, names);
        if (missing.size() > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("Data source did not contain required columns. (");

            boolean isFirst = true;            
            for (Iterator i = missing.iterator(); i.hasNext();) {
                String s = (String) i.next();
                
                if (!isFirst) {
                    sb.append(", ");
                } else {
                    isFirst = false;
                }
                
                sb.append(s);
            }
            sb.append(")");

            throw new DataException(sb.toString());
        }
    }

    /*
     * This check is performed when continuing from one sheet to the next. It
     * makes sure that the second sheet has the correct columns.
     */ 
    private void checkAdditionalColumnName(int column, String name)
        throws DataException
    {
        Integer columnId = (Integer) columnNames.get(name);

        if (columnId == null) {
            throw new DataException("Could not find column " + name);
        }
        if (column != columnId.intValue()) {
            throw new DataException("Column " + name + " has an invalid id " + column + ". It should have id " + columnId.intValue() + ".");
        }
    }
}
