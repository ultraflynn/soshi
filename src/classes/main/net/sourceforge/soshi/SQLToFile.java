package net.sourceforge.soshi;

import net.sourceforge.soshi.util.StringUtils;

import java.io.FileWriter;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class SQLToFile
{
    public void process(ResultSet rs, String filename)
        throws IOException, SQLException
    {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        Collection columns = new ArrayList(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            String column = metaData.getColumnName(i);
            columns.add(column);
        }

        DocumentFactory factory = DocumentFactory.getInstance();
        Document doc = factory.createDocument();
        Element sideElement = doc.addElement("side");
        while (rs.next()) {
            Element rowElement = sideElement.addElement("row");
            for (Iterator i = columns.iterator(); i.hasNext();) {
                String column = (String) i.next();
                String value = rs.getString(column);
                if (value == null) {
                    value = "";
                }
                value = StringUtils.replace(value, "£", "&#163;");
                Element columnElement = rowElement.addElement("column");
                columnElement.addAttribute("name", column);
                columnElement.addAttribute("value", value);
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        FileWriter fileWriter = new FileWriter(filename);
        XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
        xmlWriter.write(doc);
        xmlWriter.flush();
    }
}
