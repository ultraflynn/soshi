package net.sourceforge.soshi;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;

import net.sourceforge.soshi.util.CollectionUtils;
import net.sourceforge.soshi.util.HTMLUtils;
import net.sourceforge.soshi.util.IOUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DiskSQLSide implements Side
{
    private static RE re;

    private Configuration config;
    private Map keys;
    private Collection duplicates;
    private Map parameters;
    private String name;
    private int type;
    private boolean debug = true;
    private RandomAccessFile rowCache;
    
    static {
        try {
            re = new RE("^[:space:]*/[:space:]*$", RE.MATCH_MULTILINE | RE.MATCH_CASEINDEPENDENT);
        } catch (RESyntaxException e) {
            e.printStackTrace(System.out);
        }
    }
    
    public DiskSQLSide()
    {
        keys = new HashMap();
        duplicates = new HashSet();
        parameters = new HashMap();
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
        String cachename = (String) parameters.get("cachename");
        String driver = (String) parameters.get("driver");
        String url = (String) parameters.get("url");
        String user = (String) parameters.get("user");
        String password = (String) parameters.get("password");

        if (filename == null || filename.equals("")) {
            throw new ConfigurationException("Filename must be specified for Disk SQL Side"); 
        }
        if (driver == null || driver.equals("")) {
            throw new ConfigurationException("Driver must be specified for Disk SQL Side"); 
        }
        if (url == null || url.equals("")) {
            throw new ConfigurationException("URL must be specified for Disk SQL Side"); 
        }
        if (user == null || user.equals("")) {
            throw new ConfigurationException("User must be specified for Disk SQL Side"); 
        }
        if (cachename == null || cachename.equals("")) {
            throw new ConfigurationException("Cache name must be specified for Disk SQL Side"); 
        }
        if (password == null) {
            password = "";
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            Reader reader = new FileReader(filename);
            String s = IOUtils.getAsString(reader);
            
            String[] queries = re.split(s);
            
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
           
            statement = connection.createStatement();
            
            boolean dataLoaded = false;

            if (debug) {
                System.out.println("queries.length: " + queries.length);
            }

            DataException e = null;
          
            for (int i = 0; i < queries.length; i++) {
                if (debug) {
                    System.out.println(queries[i]);
                }
                
                boolean hasResults = statement.execute(queries[i]);

                // Get the first resultset with data in it.
                if (hasResults && !dataLoaded) {
                    rs = statement.getResultSet();

                    try {
                        checkColumnsExist(rs, config);

                        loadData(rs, cachename);

                        dataLoaded = true;
                    } catch (DataException e1) {
                        e = e1;
                    }
                }
            }

            if (e != null) {
                throw e;
            }
        } catch (FileNotFoundException e) {
            throw new DataException(e);
        } catch (IOException e) {
            throw new DataException(e);
        } catch (ClassNotFoundException e) {
            throw new DataException(e);
        } catch (SQLException e) {
            throw new DataException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e1) {
                throw new DataException(e1);
            }
        }
    }

    private void loadData(ResultSet rs, String cachename)
        throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        Collection keyColumns = config.getKeyColumnNames();
        Collection columns = config.getColumnNames();
        Collection infoColumns = config.getInfoColumnNames(type);

        int rowCount = 0;
        int duplicateCount = 0;
        int uniqueCount = 0;
        long fileOffset = 0;

        rowCache = new RandomAccessFile(cachename, "rw");
        rowCache.setLength(0);

        while (rs.next()) {
            fileOffset = rowCache.getFilePointer();
            rowCache.writeBytes("<row>");

            rowCount++;
            Key key = new DiskKey(config, fileOffset);
            
            for (Iterator i = columns.iterator(); i.hasNext();) {
                String name = (String) i.next();
                String value = rs.getString(name);
                
                rowCache.writeBytes("<column name=\"" + name + "\" value=\"" + HTMLUtils.escape(value) + "\"/>");

                if (keyColumns.contains(name)) {
                    key.addColumn(name, value);
                }
            }
            for (Iterator i = infoColumns.iterator(); i.hasNext();) {
                String name = (String) i.next();
                String value = rs.getString(name);
                rowCache.writeBytes("<column name=\"" + name + "\" value=\"" + HTMLUtils.escape(value) + "\"/>");
            }

            DiskKey existingKey = (DiskKey) keys.get(key);
            if (existingKey == null) {
                uniqueCount++;
                keys.put(key, key);
            } else {
                duplicateCount++;
                duplicates.add(existingKey);
                existingKey.addFileOffset(fileOffset);
            }

            rowCache.writeBytes("</row>\n");
        }

        if (debug) {
            System.out.println("duplicates: " + duplicateCount);
            System.out.println("unique: " + uniqueCount);
            System.out.println("rows: " + rowCount);
        }

        for (Iterator i = duplicates.iterator(); i.hasNext();) {
            Key key = (Key) i.next();
            keys.remove(key);
        }

        rowCache.close();
        rowCache = new RandomAccessFile(cachename, "r");
    }
    
    public Row getRow(Key o)
        throws IllegalArgumentException
    {
        if (o == null) {
            throw new IllegalArgumentException("Key is null");
        }
        
        if (!(o instanceof DiskKey)) {
            throw new IllegalArgumentException("Key must be a DiskKey on a DiskSQLSide");
        }

        DiskKey key = (DiskKey) o;

        if (!(keys.containsKey(key))) {
            throw new IllegalArgumentException("DiskKey was not generated by DiskSQLSide");
        }

        long fileOffset = key.getFileOffset();
        return getRow(key, fileOffset);
         
    }

    public Collection getAllRows(Key o)
        throws IllegalArgumentException
    {
        if (o == null) {
            throw new IllegalArgumentException("Key is null");
        }
        
        if (!(o instanceof DiskKey)) {
            throw new IllegalArgumentException("Key must be a DiskKey on a DiskSQLSide");
        }

        DiskKey key = (DiskKey) o;

        Collection rows;
        if (keys.containsKey(key)) {
            Collection fileOffsets = key.getAllFileOffsets();
            rows = new ArrayList(fileOffsets.size());
            for (Iterator i = fileOffsets.iterator(); i.hasNext();) {
                Long fileOffsetObject = (Long) i.next();
                long fileOffset = fileOffsetObject.longValue();
                Row row = getRow(key, fileOffset);
                rows.add(row);
            }
        } else {
            rows = new ArrayList();
        }

        return rows;
    }

    private Row getRow(DiskKey key, long fileOffset)
    {
        Row row = new Row();

        try {
            row.setKey(key);
        
            rowCache.seek(key.getFileOffset());
            String line = rowCache.readLine();
            StringReader reader = new StringReader(line);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(reader));

            NodeList columns = document.getElementsByTagName("column");

            for (int i = 0; i < columns.getLength(); i++) {
                Node column = columns.item(i);

                NamedNodeMap attributes = column.getAttributes();
                String name = attributes.getNamedItem("name").getNodeValue();
                String value = attributes.getNamedItem("value").getNodeValue();
                row.addColumn(name, value);
            }
        } catch (IOException e) {
            System.out.println("I/O error occured due to invalid seek offset.");
        } catch (ParserConfigurationException e) {
            System.out.println("DocumentBuilder could not be created which satisfies the configuration requested.");
        } catch (SAXException e) {
            System.out.println("SAX XML parse errors encountered: " + e.getMessage());
        }

        return row;
    }
    
    public Collection getKeys()
    {
        return Collections.unmodifiableCollection(keys.keySet());
    }

    public Collection getDuplicateKeys()
    {
        return Collections.unmodifiableCollection(duplicates);
    }

    private void checkColumnsExist(ResultSet rs, Configuration config)
        throws DataException, SQLException
    {
        if (rs == null) {
            throw new DataException("SQL Query did not produce a results set");
        }

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Collection names = new HashSet(columnCount);
        
        for (int i = 1; i <= columnCount; i++) {
            String name = metaData.getColumnName(i);
            names.add(name);
        }
        
        Collection required = config.getColumnNames();
        
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
}
