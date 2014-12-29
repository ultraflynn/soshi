package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import net.sourceforge.soshi.util.HashMultiMap;
import net.sourceforge.soshi.util.MultiMap;

import java.io.File;
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

import org.apache.poi.hssf.eventusermodel.AbortableHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.HSSFUserException;

import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelSide extends AbortableHSSFListener implements Side
{
    public static final int STRING_VALUE = 1;
    public static final int DOUBLE_VALUE = 2;

    private Configuration config;
    private Map parameters;
    private Map rows;
    private MultiMap allRows;
    private Collection duplicates;

    private boolean debug = false;
    private String name;
    private int type;
    private String initialSheet = null;

    private boolean processData = false;
    private boolean processFirstSheetFlag = false;
    private boolean sheetContinuation = false;

    private Map sheets;
    private int currentSheetIndex = 0;

    private SSTRecord sstrec;

    private ExcelChunk chunk;

    public ExcelSide()
    {
        parameters = new HashMap();
        sheets = new HashMap();
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

    public Row getRow(Key o)
        throws IllegalArgumentException
    {
        if (o == null) {
            throw new IllegalArgumentException("Key is null");
        }
        
        if (!(o instanceof NormalKey)) {
            throw new IllegalArgumentException("Key must be a NormalKey on a ExcelSide");
        }

        return (Row) rows.get(o);
    }

    public Collection getAllRows(Key key)
        throws IllegalArgumentException
    {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        
        if (!(key instanceof NormalKey)) {
            throw new IllegalArgumentException("Key must be a NormalKey on a ExcelSide");
        }

        Collection result = allRows.get(key);

        if (result == null) {
            result = new ArrayList();
        }

        return result;
    }

    public Collection getKeys()
    {
        return Collections.unmodifiableCollection(rows.keySet());
    }

    public Collection getDuplicateKeys()
    {
        return Collections.unmodifiableCollection(duplicates);
    }

    public void setConfiguration(Configuration config)
    {
        this.config = config;
    }

    public void addParameter(String name, String value)
    {
        parameters.put(name, value);

        if (name.equals("debug") && value.equals("true")) {
            debug = true;
        }
    }

    public void load()
        throws DataException, ConfigurationException
    {
        if (config == null) {
            throw new ConfigurationException("The configuration must be set");
        }

        String filename = (String) parameters.get("filename");
        initialSheet = (String) parameters.get("initialSheet");
        String sheetContinuationParm = (String) parameters.get("sheetContinuation");

        if (filename == null || filename.equals("")) {
            throw new ConfigurationException("Filename must be specified for Excel Side"); 
        }
        if (initialSheet == null || initialSheet.equals("")) {
            processFirstSheetFlag = true;
            processData = true;
        }

        sheetContinuation = (!(sheetContinuationParm == null || sheetContinuationParm.equals("no") || sheetContinuationParm.equals("false")));

        File file = new File(filename);

        chunk = new ExcelChunk(type, config, this);

        if (file.exists()) {
            if (!file.isFile()) {
                throw new DataException(filename + " is not a normal file");
            }

            try {
                InputStream fin = new FileInputStream(filename);
                POIFSFileSystem poifs = new POIFSFileSystem(fin);
                InputStream din = poifs.createDocumentInputStream("Workbook");
                HSSFRequest req = new HSSFRequest();
                req.addListenerForAllRecords(this);
                HSSFEventFactory factory = new HSSFEventFactory();
                factory.abortableProcessEvents(req, din);
                fin.close();
                din.close();

                /*
                 * If no data has been processed and we weren't processing the first sheet, i.e we were
                 * given a specific sheet to process then we didn't find that sheet and so we throw
                 * an exception.
                 */
                if (processData == false && processFirstSheetFlag == false) {
                    throw new ConfigurationException("The file " + filename + " does not contain sheet " + initialSheet);
                }

                chunk.finished();

                for (Iterator i = duplicates.iterator(); i.hasNext();) {
                    Key key = (Key) i.next();
                    rows.remove(key);
                }
            } catch (FileNotFoundException e) {
                throw new DataException(e);
            } catch (IOException e) {
                throw new DataException(e);
            } catch (HSSFUserException e) {
                throw new DataException(e);
            }
        }
    }

    public short abortableProcessRecord(Record record)
        throws HSSFUserException
    {
        try {
            int sid = record.getSid();

            if (sid == BoundSheetRecord.sid) {
                BoundSheetRecord bsr = (BoundSheetRecord) record;

                Integer i = new Integer(sheets.size());
                String name = bsr.getSheetname();

                sheets.put(i, name);
            } if (sid == BOFRecord.sid) {
                BOFRecord bof = (BOFRecord) record;
                if (bof.getType() == bof.TYPE_WORKSHEET) {
                    if (processData && (!processFirstSheetFlag)) {
                        if (!sheetContinuation) {
                            return 1;
                        }
                        processFirstSheetFlag = false;
                    } else {
                        String sheetName = (String) sheets.get(new Integer(currentSheetIndex));

                        if (sheetName.equals(initialSheet)) {
                            processData = true;
                        }
                    }

                    currentSheetIndex++;
                }
            } if (sid == NumberRecord.sid) {
                if (processData) {
                    NumberRecord numrec = (NumberRecord) record;

                    int row = numrec.getRow();
                    int column = numrec.getColumn();
                    double value = numrec.getValue();

                    chunk.put(row, column, value);
                }
            } if (sid == SSTRecord.sid) {
                sstrec = (SSTRecord) record;
            } if (sid == LabelSSTRecord.sid) {
                if (processData) {
                    LabelSSTRecord lrec = (LabelSSTRecord) record;

                    int row = lrec.getRow();
                    int column = lrec.getColumn();
                    String value = sstrec.getString(lrec.getSSTIndex());

                    chunk.put(row, column, value);
                }
            }

            return 0;
        } catch (DataException e) {
            throw new HSSFUserException(e.getMessage());
        }
    }

    public void addRow(Key key, Row row)
    {
        allRows.put(key, row);
    }

    public boolean containsRowKey(Key key)
    {
        return rows.containsKey(key);
    }

    public void addDuplicate(Key key)
    {
        duplicates.add(key);
    }

    public void putRow(Key key, Row row)
    {
        rows.put(key, row);
    }
}
