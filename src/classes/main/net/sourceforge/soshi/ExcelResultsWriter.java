package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import java.net.MalformedURLException;
import java.net.URL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sourceforge.carbine.ParseException;
import net.sourceforge.carbine.TagLoader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.apache.regexp.RE;

public class ExcelResultsWriter implements ResultsWriter
{
    private static final String CHAR_EXCEPTIONS_CONFIG_FILE = "exceptions.xml";
    private static final int MAX_ROWS = 65536;

    private Results results;
    private Configuration config;
    private HSSFWorkbook workbook;
    private String filename;
    private String recName;
    private HSSFSheet sheet;
    private int sheetId;
    private int page;
    private int rowId;
    private Map formats;
    private Set charExceptions;

    public ExcelResultsWriter(String filename)
        throws ConfigurationException
    {
        this.filename = filename;
        
        initCharExceptions();
    }

    public synchronized void write(Results results)
        throws IOException, InvalidValueException
    {
        this.results = results;
        config = results.getConfiguration();
        recName = config.getName();

        // Read the workbook if it exists otherwise create a new one
        workbook = getWorkbook();
        setUpFormats();

        // Remove the sheet if it exists
        removeSheet(recName);

        // Add the sheet for the reconciliation
        addSheet();

        // Write the workbook back out
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.close();
    }

    private HSSFWorkbook getWorkbook()
        throws IOException
    {
        HSSFWorkbook wb;
        File file = new File(filename);

        if (file.exists()) {
            if (!file.isFile()) {
                throw new IOException(filename + " is not a normal file");
            }

            try {
                FileInputStream fis = new FileInputStream(filename);
                POIFSFileSystem fs = new POIFSFileSystem(fis);
                wb = new HSSFWorkbook(fs);
            } catch (IOException e) {
                throw new IOException(filename + " is not an Excel file");
            }
        } else {
            wb = new HSSFWorkbook();
        }

        return wb;
    }

    private void removeSheet(String name)
    {
        removeSheetWithName(name);
        String pageName;
        int pageNumber = 1;
        do {
            pageName = getNumberedSheetName(name, pageNumber);
            pageNumber++;
        } while (removeSheetWithName(pageName));
    }

    private boolean removeSheetWithName(String name)
    {
        int sheetIndex = workbook.getSheetIndex(name);

        if (sheetIndex == -1) {
            return false;
        }

        workbook.removeSheetAt(sheetIndex);

        return true;
    }

    private void addSheet()
        throws InvalidValueException
    {
        createSheet(recName);
        page = 1;
        rowId = 0;

        addTitle();
        rowId++;
        addDate();
        rowId++;
        addSummary();
        rowId++;
        addUnmatchedKeys();
        rowId++;
        addDuplicateKeys();
        rowId++;
        addExcluded();
        rowId++;
        addDataDifferencesByColumn();
        rowId++;
        addDataDifferencesByKey();
    }

    private void addTitle()
        throws InvalidValueException
    {
        addRow(recName, "title");
    }

    private void addDate()
        throws InvalidValueException
    {
        addRow("Prepared on " + new Date(), "normal");
    }

    private void addSummary()
        throws InvalidValueException
    {
        addRow("Summary", "section");
        addCounts();
        rowId++;
        addColumnDifferences();
    }

    private void addCounts()
        throws InvalidValueException
    {
        addRow("Entry count", "heading");
        addEntryCount(Side.SIDEA);
        addEntryCount(Side.SIDEB);
        rowId++;
        addMatchCount();
        rowId++;
        addDuplicateCount(Side.SIDEA);
        addDuplicateCount(Side.SIDEB);
        rowId++;
        addExcludedCount(Side.SIDEA);
        addExcludedCount(Side.SIDEB);
        rowId++;
        int total = 0;
        total += addMissingCount(Side.SIDEB);
        total += addMissingCount(Side.SIDEA);
        addTotal(total);
    }

    private void addEntryCount(int sideType)
        throws InvalidValueException
    {
        Side side = results.getSide(sideType);
        String name = side.getName();
        Collection keys = side.getKeys();
        int keyCount = keys.size();
        String keyCountString = Integer.toString(keyCount);
        addRow(new String[] { "Number of " + name + " entries", keyCountString }, "normal");
    }

    private void addMatchCount()
        throws InvalidValueException
    {
        Collection matched = results.getMatchedKeys();
        int matchedCount = matched.size();
        String matchedCountString = Integer.toString(matchedCount);
        addRow(new String[] { "Number of keys that match", matchedCountString }, "normal");
    }

    private void addDuplicateCount(int sideType)
        throws InvalidValueException
    {
        Side side = results.getSide(sideType);
        String name = side.getName();
        Collection keys = side.getDuplicateKeys();
        int keyCount = keys.size();
        String keyCountString = Integer.toString(keyCount);
        addRow(new String[] { "Number of keys with duplicates in " + name, keyCountString }, "normal");
    }

    private void addExcludedCount(int sideType)
        throws InvalidValueException
    {
        Side side = results.getSide(sideType);
        String name = side.getName();
        Collection keys = results.getExcluded(sideType);
        int keyCount = keys.size();
        String keyCountString = Integer.toString(keyCount);
        addRow(new String[] { "Number of keys excluded from " + name, keyCountString }, "normal");
    }

    private int addMissingCount(int sideType)
        throws InvalidValueException
    {
        String missingName = results.getSide(sideType).getName();
        String otherName = results.getSide(getOtherSide(sideType)).getName();
        Collection missing = results.getMissingKeys(sideType);
        int missingCount = missing.size();
        String missingCountString = Integer.toString(missingCount);
        addRow(new String[] { "Number of keys which exist in " + otherName + " but not " + missingName, missingCountString }, "normal");

        return missingCount;
    }

    private void addColumnDifferences()
        throws InvalidValueException
    {
        addRow(new String[] { "Column Differences" , "Count" }, "heading");

        int total = 0;
        for (Iterator i = config.getColumnNames().iterator(); i.hasNext();) {
            String column = (String) i.next();
            String descr = config.getDescription(column);
            int differenceCount = results.getDifferencesForColumn(column).size();
            total += differenceCount;
            String differenceCountString = Integer.toString(differenceCount);
            addRow(new String[] { descr, differenceCountString }, "normal");
        }

        addTotal(total);
    }

    private void addTotal(int total)
        throws InvalidValueException
    {
        String totalString = Integer.toString(total);
        addRow(new String[] { "Total" , totalString }, "total");
    }

    private void addUnmatchedKeys()
        throws InvalidValueException
    {
        addRow("Unmatched Keys", "section");
        rowId++;
        addMissingKeys(Side.SIDEB);
        rowId++;
        addMissingKeys(Side.SIDEA);
    }

    private void addDuplicateKeys()
        throws InvalidValueException
    {
        addRow("Duplicate Keys", "section");
        rowId++;
        addDuplicateKeys(Side.SIDEA);
        rowId++;
        addDuplicateKeys(Side.SIDEB);
    }

    private void addExcluded()
        throws InvalidValueException
    {
        addRow("Excluded Keys", "section");
        rowId++;
        addRow("The following were excluded because duplicate keys were found on the", "information");
        addRow("other side of the reconciliation", "information");
        rowId++;
        addExcluded(Side.SIDEA);
        rowId++;
        addExcluded(Side.SIDEB);
    }

    private void addMissingKeys(int sideType)
        throws InvalidValueException
    {
        String missingName = results.getSide(sideType).getName();
        String otherName = results.getSide(getOtherSide(sideType)).getName();
        addRow("Keys which exist in " + otherName + " but not in " + missingName, "heading");

        Collection missing = results.getMissingKeys(sideType);
        for (Iterator i = missing.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            addRow(key.toString(), "normal");
        }

        addRow(missing.size() + " item(s)", "total");
    }

    private void addDuplicateKeys(int sideType)
        throws InvalidValueException
    {
        String name = results.getSide(sideType).getName();
        addRow("Keys in " + name + " which are duplicated", "heading");

        Collection duplicates = results.getDuplicateKeys(sideType);
        for (Iterator i = duplicates.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            addRow(key.toString(), "normal");
        }

        addRow(duplicates.size() + " item(s)", "total");
    }

    private void addExcluded(int sideType)
        throws InvalidValueException
    {
        String thisName = results.getSide(sideType).getName();
        String otherName = results.getSide(getOtherSide(sideType)).getName();
        addRow("Keys which have been excluded from " + thisName + " because duplicates exist in " + otherName, "heading");

        Collection excluded = results.getExcluded(sideType);
        for (Iterator i = excluded.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            addRow(key.toString(), "normal");
        }

        addRow(excluded.size() + " item(s)", "total");
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
        throws InvalidValueException
    {
        addRow("Data Differences Breakdown By Column", "section");
        rowId++;

        Collection columns = config.getNonKeyColumnNames();

        for (Iterator i = columns.iterator(); i.hasNext();) {
            String column = (String) i.next();
            String descr = config.getDescription(column);
            addRow("Entries where " + descr + " is different", "heading");

            String heading1 = "Key";
            String heading2 = "";
            String heading3 = results.getSide(Side.SIDEA).getName() + " value";
            String heading4 = results.getSide(Side.SIDEB).getName() + " value";
            addRow(new String[] { heading1, heading2, heading3, heading4 }, "heading");

            addDataDifferencesForColumn(column);

            if (i.hasNext()) {
                rowId++;
            }
        }
    }

    private void addDataDifferencesForColumn(String column)
        throws InvalidValueException
    {
        Collection differences = results.getDifferencesForColumn(column);
        for (Iterator i = differences.iterator(); i.hasNext();) {
            Difference difference = (Difference) i.next();
            String key = difference.getKeyPair().toString(Side.SIDEA);
            String valueA = difference.getValueA();
            String valueB = difference.getValueB();

            addRow(new String[] { key, "", valueA, valueB }, "normal", true);
        }
        addRow(differences.size() + " item(s)", "total");
    }

    private void addDataDifferencesByKey()
        throws InvalidValueException
    {
        addRow("Data Differences Breakdown By Key", "section");
        rowId++;

        for (Iterator i = results.getMatchedKeys().iterator(); i.hasNext();) {
            KeyPair keys = (KeyPair) i.next();
            Key keyA = keys.getKey(Side.SIDEA);

            Collection differences = results.getDifferencesForRow(keys);
            if (differences.size() > 0) {
                String heading1 = keyA.toString();
                String heading2 = "Column";
                String heading3 = results.getSide(Side.SIDEA).getName() + " value";
                String heading4 = results.getSide(Side.SIDEB).getName() + " value";
                addRow(new String[] { heading1, heading2, heading3, heading4 }, "heading");

                addDataDifferencesForKey(differences);

                if (i.hasNext()) {
                    rowId++;
                }
            }
        }
    }

    private void addDataDifferencesForKey(Collection differences)
        throws InvalidValueException
    {
        for (Iterator i = differences.iterator(); i.hasNext();) {
            Difference difference = (Difference) i.next();
            String column = difference.getColumn();
            String valueA = difference.getValueA();
            String valueB = difference.getValueB();
            String descr = config.getDescription(column);
            addRow(new String[] { "", descr, valueA, valueB }, "normal", true);
        }
    }

    private HSSFRow addRow(String firstValue, String format)
        throws InvalidValueException
    {
        String[] array = new String[1];
        array[0] = firstValue;
        return addRow(array, format);
    }

    private HSSFRow addRow(String[] values, String format)
        throws InvalidValueException
    {
        return addRow(values, format, false);
    }

    private HSSFRow addRow(String[] values, String format, boolean validate)
        throws InvalidValueException
    {
        HSSFRow row = sheet.createRow(rowId);

        for (short i = 0; i < values.length; i++) {
            String value = values[i];

            if (validate) {
                validateValue(value);
            }

            if (value != null) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(value);
                setFormat(cell, format);
            }
        }

        if (rowId == MAX_ROWS) {
            String newSheetName;
            rowId = 0;
            if (page == 1) {
                newSheetName = getNumberedSheetName(recName, page);
                workbook.setSheetName(sheetId, newSheetName);
            }
            page++;
            newSheetName = getNumberedSheetName(recName, page);
            createSheet(newSheetName);
        } else {
            rowId++;
        }
        
        return row;
    }

    private void setFormat(HSSFCell cell, String format)
    {
        HSSFCellStyle style = (HSSFCellStyle) formats.get(format);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void setUpFormats()
    {
        formats = new HashMap();

        HSSFCellStyle title = workbook.createCellStyle();
        title.setFont(getFont("Verdana", 10, HSSFFont.COLOR_RED, true, false));
        formats.put("title", title);

        HSSFCellStyle section = workbook.createCellStyle();
        section.setFont(getFont("Verdana", 8, HSSFFont.COLOR_NORMAL, true, true));
        formats.put("section", section);

        HSSFCellStyle heading = workbook.createCellStyle();
        heading.setFont(getFont("Verdana", 8, HSSFFont.COLOR_NORMAL, true, false));
        formats.put("heading", heading);

        HSSFCellStyle total = workbook.createCellStyle();
        total.setFont(getFont("Verdana", 8, HSSFFont.COLOR_NORMAL, false, true));
        formats.put("total", total);

        HSSFCellStyle information = workbook.createCellStyle();
        information.setFont(getFont("Verdana", 8, HSSFFont.COLOR_NORMAL, false, true));
        formats.put("information", information);

        HSSFCellStyle normal = workbook.createCellStyle();
        normal.setFont(getFont("Verdana", 8, HSSFFont.COLOR_NORMAL, false, false));
        formats.put("normal", normal);
    }
    
    private HSSFFont getFont(String name, int size, short colour, boolean bold, boolean italic)
    {
        HSSFFont font = workbook.createFont();

        font.setFontName(name);
        font.setFontHeight((short) (size * 20));
        font.setColor(colour);
        if (bold) {
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        }
        font.setItalic(italic);

        return font;
    }

    private void createSheet(String name)
    {
        sheet = workbook.createSheet(name);
        sheetId = workbook.getSheetIndex(name);
        sheet.setColumnWidth((short) 0, (short) 11920);
        sheet.setColumnWidth((short) 1, (short) 3840);
        sheet.setColumnWidth((short) 2, (short) 11520);
        sheet.setColumnWidth((short) 3, (short) 11520);
    }

    private String getNumberedSheetName(String name, int page)
    {
        return name + " (Page " + page + ")";
    }

    private void validateValue(String value)
        throws InvalidValueException
    {
        if (charExceptions != null) {
            for (Iterator i = charExceptions.iterator(); i.hasNext();) {
                ExceptionValue exeception = (ExceptionValue) i.next();
                String expression = exeception.getValue();

                RE re = new RE(expression);
                if (re.match(value)) {
                    throw new InvalidValueException(value);
                }
            }
        }
    }

    private void initCharExceptions()
        throws ConfigurationException
    {
        try {
            File file = new File(CHAR_EXCEPTIONS_CONFIG_FILE);

            if (file.exists()) {
                URL url = file.toURL();
                TagLoader loader = new TagLoader();
                charExceptions = (Set) loader.parse(url);
            }
        } catch (MalformedURLException e) {
            throw new ConfigurationException(e);
        } catch (ParseException e) {
            throw new ConfigurationException(e);
        }
    }
}
