package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;

import net.sourceforge.soshi.util.ResourceStringManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class HTMLResultsWriter implements ResultsWriter
{
    private Results results;
    private Configuration config;
    private String filename;
    private PrintWriter output;

    public HTMLResultsWriter(String filename)
    {
        this.filename = filename;
    }

    public synchronized void write(Results results)
        throws IOException
    {
        this.results = results;
        config = results.getConfiguration();

        FileOutputStream fos = new FileOutputStream(filename);
        output = new PrintWriter(fos);
        outputToStream();
        output.close();
    }

    private void outputToStream()
    {
        output.println("<html>");
        output.println("<head>");
        output.print("<title>");
        output.print(config.getName());
        output.println("</title>");
        addTooltipHeaders();
        output.println("</head>");
        output.println("<body>");
        addTitle();
        addDate();
        addSectionLinks();
        addSummary();
        addUnmatchedKeys();
        addDuplicateKeys();
        addExcluded();
        addDataDifferencesByColumn();
        addDataDifferencesByKey();
        addTooltipDiv();
        output.println("</body>");
        output.println("</html>");
    }

    private void addTitle()
    {
        output.println("<h1><font color=\"red\">");
        output.println(config.getName());
        output.println("</font></h1>");
    }

    private void addSectionHeading(String s)
    {
        output.println("<h2>");
        output.println(s);
        output.println("</h2>");
    }

    private void addDate()
    {
        output.println("<p>");
        output.println("Prepared on " + new Date());
        output.println("</p>");
    }

    private void addSectionLinks()
    {
        output.println("<table width=\"100%\">");
        output.println("<tr>");
        addSectionLink("Column Counts");
        addSectionLink("Keys not in " + results.getSide(Side.SIDEB).getName());
        addSectionLink("Keys not in " + results.getSide(Side.SIDEA).getName());
        addSectionLink("Duplicate keys");
        addSectionLink("Excluded keys");
        addSectionLink("Column breakdown");
        addSectionLink("Key breakdown");
        output.println("</tr>");
        endTable();
    }

    private void addSectionLink(String s)
    {
        output.print("<td>");
        output.print("<a href=\"#");
        output.print(s);
        output.print("\">");
        output.print(s);
        output.print("</a>");
        output.print("</td>");
    }

    private void addSectionLinkName(String s)
    {
        output.print("<a name=\"");
        output.print(s);
        output.print("\"\\>");
    }

    private void addSummary()
    {
        addSectionHeading("Summary");
        addCounts();
        addColumnDifferences();
    }

    private void addCounts()
    {
        startTable();
        addRow("<b>Entry count</b>", 2);
        addEntryCount(Side.SIDEA);
        addEntryCount(Side.SIDEB);
        addBlankRow(2);
        addMatchCount();
        addBlankRow(2);
        addDuplicateCount(Side.SIDEA);
        addDuplicateCount(Side.SIDEB);
        addBlankRow(2);
        addExcludedCount(Side.SIDEA);
        addExcludedCount(Side.SIDEB);
        addBlankRow(2);
        int total = 0;
        total += addMissingCount(Side.SIDEB);
        total += addMissingCount(Side.SIDEA);
        addTotal(total);
        endTable();
    }

    private void addEntryCount(int sideType)
    {
        Side side = results.getSide(sideType);
        String name = side.getName();
        Collection keys = side.getKeys();
        int keyCount = keys.size();
        String keyCountString = Integer.toString(keyCount);
        addRow(new String[] { "Number of " + name + " entries", keyCountString });
    }

    private void addMatchCount()
    {
        Collection matched = results.getMatchedKeys();
        int matchedCount = matched.size();
        String matchedCountString = Integer.toString(matchedCount);
        addRow(new String[] { "Number of keys that match", matchedCountString });
    }

    private void addDuplicateCount(int sideType)
    {
        Side side = results.getSide(sideType);
        String name = side.getName();
        Collection keys = side.getDuplicateKeys();
        int keyCount = keys.size();
        String keyCountString = Integer.toString(keyCount);
        addRow(new String[] { "Number of keys with duplicates in " + name, keyCountString });
    }

    private void addExcludedCount(int sideType)
    {
        Side side = results.getSide(sideType);
        String name = side.getName();
        Collection keys = results.getExcluded(sideType);
        int keyCount = keys.size();
        String keyCountString = Integer.toString(keyCount);
        addRow(new String[] { "Number of keys excluded from " + name, keyCountString });
    }

    private int addMissingCount(int sideType)
    {
        String missingName = results.getSide(sideType).getName();
        String otherName = results.getSide(getOtherSide(sideType)).getName();
        Collection missing = results.getMissingKeys(sideType);
        int missingCount = missing.size();
        String missingCountString = Integer.toString(missingCount);
        addRow(new String[] { "Number of keys which exist in " + otherName + " but not " + missingName, missingCountString });

        return missingCount;
    }

    private void addColumnDifferences()
    {
        startTable();
        addSectionLinkName("Column Counts");
        addRow(new String[] { "<b>Column Differences</b>" , "<b>Count</b>" });

        int total = 0;
        for (Iterator i = config.getColumnNames().iterator(); i.hasNext();) {
            String column = (String) i.next();
            String descr = config.getDescription(column);
            int differenceCount = results.getDifferencesForColumn(column).size();
            total += differenceCount;
            String differenceCountString = Integer.toString(differenceCount);
            addRow(new String[] { descr, differenceCountString });
        }

        addTotal(total);
        endTable();
    }

    private void addTotal(int total)
    {
        String totalString = Integer.toString(total);
        addRow(new String[] { "<i>Total</i>" , "<i>" + totalString + "</i>" });
    }

    private void addUnmatchedKeys()
    {
        addSectionHeading("Unmatched Keys");
        addMissingKeys(Side.SIDEB);
        addMissingKeys(Side.SIDEA);
    }

    private void addDuplicateKeys()
    {
        addSectionHeading("Duplicate Keys");
        addDuplicateKeys(Side.SIDEA);
        addDuplicateKeys(Side.SIDEB);
    }

    private void addExcluded()
    {
        addSectionHeading("Excluded Keys");
        output.println("The following were excluded because duplicate keys were found on the other side of the reconciliation");
        addExcluded(Side.SIDEA);
        addExcluded(Side.SIDEB);
    }

    private void addMissingKeys(int sideType)
    {
        String missingName = results.getSide(sideType).getName();
        String otherName = results.getSide(getOtherSide(sideType)).getName();
        addSectionLinkName("Keys not in " + missingName);
        startTable();
        addRow("<b>Keys which exist in " + otherName + " but not in " + missingName + "</b>", 1);

        Collection missing = results.getMissingKeys(sideType);
        for (Iterator i = missing.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            addRow(getKeyString(key), 1);
        }

        addRow("<i>" + missing.size() + " item(s)</i>", 1);
        endTable();
    }

    private void addDuplicateKeys(int sideType)
    {
        addSectionLinkName("Duplicate keys");
        startTable();
        String name = results.getSide(sideType).getName();
        addRow("<b>Keys in " + name + " which are duplicated</b>", 1);

        Collection duplicates = results.getDuplicateKeys(sideType);
        for (Iterator i = duplicates.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            addRow(getKeyString(key), 1);
        }

        addRow("<i>" + duplicates.size() + " item(s)</i>", 1);
        endTable();
    }

    private void addExcluded(int sideType)
    {
        addSectionLinkName("Excluded keys");
        startTable();
        String thisName = results.getSide(sideType).getName();
        String otherName = results.getSide(getOtherSide(sideType)).getName();
        addRow("<b>Keys which have been excluded from " + thisName + " because duplicates exist in " + otherName + "</b>", 1);

        Collection excluded = results.getExcluded(sideType);
        for (Iterator i = excluded.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            addRow(getKeyString(key), 1);
        }

        addRow("<i>" + excluded.size() + " item(s)</i>", 1);
        endTable();
    }

    private int getOtherSide(int sideType)
    {
        int otherSide;

        switch (sideType) {
        case Side.SIDEA:
            otherSide = Side.SIDEB;
            break;
        case Side.SIDEB:
            otherSide = Side.SIDEA;
            break;
        default:
            throw new IllegalArgumentException();
        }

        return otherSide;
    }

    private void addDataDifferencesByColumn()
    {
        addSectionLinkName("Column breakdown");
        addSectionHeading("Data Differences Breakdown By Column");

        Collection columns = config.getNonKeyColumnNames();

        startTable();
        for (Iterator i = columns.iterator(); i.hasNext();) {
            String column = (String) i.next();
            String descr = config.getDescription(column);
            addRow("Entries where " + descr + " is different", 4);

            String heading1 = "<b>Key</b>";
            String heading2 = "&nbsp;";
            String heading3 = "<b>" + results.getSide(Side.SIDEA).getName() + " value</b>";
            String heading4 = "<b>" + results.getSide(Side.SIDEB).getName() + " value</b>";
            addRow(new String[] { heading1, heading2, heading3, heading4 });

            addDataDifferencesForColumn(column);
        }
        endTable();
    }

    private void addDataDifferencesForColumn(String column)
    {
        Collection differences = results.getDifferencesForColumn(column);
        for (Iterator i = differences.iterator(); i.hasNext();) {
            Difference difference = (Difference) i.next();
            Key key = difference.getKeyPair().getKey(Side.SIDEA);
            String valueA = difference.getValueA();
            String valueB = difference.getValueB();
            addRow(new String[] { getKeyString(key), "&nbsp;", valueA, valueB });
        }
        addRow("<i>" + differences.size() + " item(s)</i>", 4);
    }

    private void addDataDifferencesByKey()
    {
        addSectionLinkName("Key breakdown");
        addSectionHeading("Data Differences Breakdown By Key");

        startTable();
        for (Iterator i = results.getMatchedKeys().iterator(); i.hasNext();) {
            KeyPair keys = (KeyPair) i.next();
            Key keyA = keys.getKey(Side.SIDEA);

            Collection differences = results.getDifferencesForRow(keys);
            if (differences.size() > 0) {
                String heading1 = "<b>" + getKeyString(keyA) + "</b>";
                String heading2 = "<b>Column</b>";
                String heading3 = "<b>" + results.getSide(Side.SIDEA).getName() + " value</b>";
                String heading4 = "<b>" + results.getSide(Side.SIDEB).getName() + " value</b>";
                addRow(new String[] { heading1, heading2, heading3, heading4 });

                addDataDifferencesForKey(differences);

                addBlankRow(4);
            }
        }
        endTable();
    }

    private void addDataDifferencesForKey(Collection differences)
    {
        for (Iterator i = differences.iterator(); i.hasNext();) {
            Difference difference = (Difference) i.next();
            String column = difference.getColumn();
            String valueA = difference.getValueA();
            String valueB = difference.getValueB();
            String descr = config.getDescription(column);
            addRow(new String[] { "&nbsp;", descr, valueA, valueB });
        }
    }

    private void startTable()
    {
        output.println("<table>");
    }

    private void endTable()
    {
        output.println("</table>");
    }

    private void addBlankRow(int width)
    {
        addRow("&nbsp;", width);
    }

    private void addRow(String value, int width)
    {
        String[] array = new String[width];
        array[0] = value;
        for (int i = 1; i < width; i++) {
            array[i] = "&nbsp;";
        }
        addRow(array);
    }

    private void addRow(String[] values)
    {
        output.print("<tr>");
        for (int i = 0; i < values.length; i++) {
            output.print("<td>");
            output.print(values[i]);
            output.print("</td>");
        }
        output.println("</tr>");
    }

    private void addTooltipHeaders()
    {
        ResourceStringManager rsm = ResourceStringManager.getInstance();
        String tooltipJavascript = rsm.getString("com/wlbp/rec/tooltip.js");
        output.println(tooltipJavascript);
        output.println("<style>");
        output.println("#tipBox {");
        output.println("position: absolute;");
        output.println("z-index: 100;");
        output.println("background: lightgrey;");
        output.println("border: 1pt black solid;");
        output.println("visibility: hidden;");
        output.println("}");
        output.println("</style>");
    }

    private void addTooltipDiv()
    {
        output.println("<div id=\"tipBox\"/>");
    }

    private String getKeyString(Key key)
    {
        StringBuffer sb = new StringBuffer();

        Collection infoColsA = config.getInfoColumnNames(Side.SIDEA);
        Collection infoColsB = config.getInfoColumnNames(Side.SIDEB);
        int infoColCount = infoColsA.size() + infoColsB.size();
        if (infoColCount > 0) {
            Side sideA = results.getSide(Side.SIDEA);
            Side sideB = results.getSide(Side.SIDEB);
            Collection rowsA = sideA.getAllRows(key);
            Collection rowsB = sideB.getAllRows(key);
            if ((rowsA.size() > 0 && infoColsA.size() > 0) || (rowsB.size() > 0 && infoColsB.size() > 0)) {
                sb.append("<span onmouseover=\"this._tip='");
                writeTooltip(key, sb);
                sb.append("'\">");
                sb.append(key.toString());
                sb.append("</span>");
            } else {
                sb.append(key.toString());
            }
        } else {
            sb.append(key.toString());
        }

        return sb.toString();
    }

    private void writeTooltip(Key key, StringBuffer sb)
    {
        sb.append("<table>");
        writeInfoColumns(key, Side.SIDEA, sb);
        writeInfoColumns(key, Side.SIDEB, sb);
        sb.append("</table>");
    }

    private void writeInfoColumns(Key key, int sideType, StringBuffer sb)
    {
        Side side = results.getSide(sideType);
        Collection rows = side.getAllRows(key);

        Collection sideInfoColumns = config.getInfoColumnNames(sideType);
        for (Iterator i = rows.iterator(); i.hasNext();) {
            Row row = (Row) i.next();
            writeInfoRow(row, sideType, sideInfoColumns, sb);
            if (i.hasNext()) {
                sb.append("<tr>");
                sb.append("<td>");
                sb.append("&nbsp;");
                sb.append("</td>");
                sb.append("<td>");
                sb.append("&nbsp;");
                sb.append("</td>");
                sb.append("</tr>");
            }
        }
    }

    private void writeInfoRow(Row row, int sideType, Collection sideInfoColumns, StringBuffer sb)
    {
        for (Iterator i = sideInfoColumns.iterator(); i.hasNext();) {
            String column = (String) i.next();
            String description = config.getInfoColumnDescription(sideType, column);
            String value = row.getColumnValue(column);
            sb.append("<tr>");
            sb.append("<td>");
            sb.append("<b>");
            sb.append(description);
            sb.append(":");
            sb.append("</b>");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(value);
            sb.append("</td>");
            sb.append("</tr>");
        }
    }
}
