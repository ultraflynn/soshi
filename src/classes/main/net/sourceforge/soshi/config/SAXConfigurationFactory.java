package net.sourceforge.soshi.config;

import net.sourceforge.soshi.Comparator;
import net.sourceforge.soshi.Side;

import java.net.URL;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import org.dom4j.io.SAXReader;

public class SAXConfigurationFactory implements ConfigurationFactory
{
    private static final String STANDARD_COMPARATOR = "net.sourceforge.soshi.StandardComparator";

    public Configuration getConfiguration(URL url)
        throws ConfigurationException
    {
        Configuration config = new Configuration();
        SAXReader sr;
        Document doc;

        if (url == null || url.equals("")) {
            throw new ConfigurationException("No XML configuration file specified");
        }

        try {
            sr = new SAXReader();
            doc = sr.read(url);
        } catch (DocumentException e) {
            throw new ConfigurationException(e, "XML configuration file is invalid");
        }

        addName(config, doc);
        addColumns(config, doc);
        addSides(config, doc);
        addInfoColumns(config, doc);

        return config;
    }

    private void addName(Configuration config, Document doc)
        throws ConfigurationException
    {
        String name = doc.valueOf("/rec/name");
        if (name == null || name == "") {
            throw new ConfigurationException("A name must be specified for the rec");
        }
        config.setName(name);
    }

    private void addColumns(Configuration config, Document doc)
        throws ConfigurationException
    {
        String name = null;
        String comparatorClass = null;

        try {
            List nodes = doc.selectNodes("/rec/columns/column");
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Node node = (Node) i.next();
                name = node.valueOf("@name");
                String key = node.valueOf("@key");
                comparatorClass = node.valueOf("@comparator");
                String descr = node.valueOf("@description");

                if (name == null || name.equals("")) {
                    throw new ConfigurationException("Column found with no name");
                }
                
                boolean isKey = (key != null && key.equals("true"));

                if (comparatorClass == null || comparatorClass.equals("")) {
                    comparatorClass = STANDARD_COMPARATOR;
                }

                Class c = Class.forName(comparatorClass);
                Comparator comparator = (Comparator) c.newInstance();

                config.addColumnName(name, isKey, comparator, descr);
            }
        } catch (ClassNotFoundException e) {
            String error = "Comparator class " + comparatorClass + " not found for column " + name;
            throw new ConfigurationException(e, error);
        } catch (InstantiationException e) {
            throw new ConfigurationException(e, "Comparator class could not be instantiated for column " + name);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(e, "Comparator class could not be instantiated for column " + name);
        }
    }

    private void addSides(Configuration config, Document doc)
        throws ConfigurationException
    {
        List nodes = doc.selectNodes("/rec/sides/side");
        if (nodes.size() != 2) {
            throw new ConfigurationException("You must specify two and only two sides");
        }

        int sideType = Side.SIDEA;
        String name = null;

        try {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Node node = (Node) i.next();
                name = node.valueOf("@name");
                String classname = node.valueOf("@class");

                if (name == null || name.equals("")) {
                    throw new ConfigurationException("Side found with no name");
                }
                if (classname == null || classname.equals("")) {
                    throw new ConfigurationException("No class specified on side " + name);
                }
                    
                Class c = Class.forName(classname);
                Side side = (Side) c.newInstance();

                side.setConfiguration(config);
                side.setName(name);
                side.setSideType(sideType);
                addParameters(side, node);
                config.addSide(sideType, side);
                sideType = Side.SIDEB;
            }
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(e, "Side class not found for side " + name);
        } catch (InstantiationException e) {
            throw new ConfigurationException(e, "Side class could not be instantiated for side " + name);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(e, "Side class could not be instantiated for side " + name);
        }
    }

    private void addParameters(Side side, Node node)
        throws ConfigurationException
    {
        List nodes = node.selectNodes("parameter");
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            Node parameter = (Node) i.next();
            String name = parameter.valueOf("@name");   
            String value = parameter.valueOf("@value");   

            if (name == null || name.equals("")) {
                throw new ConfigurationException("Parameter name is not specified for side " + node.valueOf("@name"));
            }
            if (value == null) {
                value = "";
            }

            side.addParameter(name, value);
        }
    }

    private void addInfoColumns(Configuration config, Document doc)
        throws ConfigurationException
    {
        List nodes = doc.selectNodes("/rec/info-column");

        for (Iterator i = nodes.iterator(); i.hasNext();) {
            Node node = (Node) i.next();
            String side = node.valueOf("@side");
            String name = node.valueOf("@name");
            String description = node.valueOf("@description");

            if (side == null || side.equals("")) {
                throw new ConfigurationException("Info columns found with no side");
            }
            if (name == null || name.equals("")) {
                throw new ConfigurationException("Info columns found with no name");
            }
            if (description == null || description.equals("")) {
                description = name;
            }

            description = side + "." + description;

            Side sideA = config.getSide(Side.SIDEA);
            Side sideB = config.getSide(Side.SIDEB);
            String sideNameA = sideA.getName();
            String sideNameB = sideB.getName();

            if (side.equals(sideNameA)) {
                config.addInfoColumnName(Side.SIDEA, name, description);
            } else if (side.equals(sideNameB)) {
                config.addInfoColumnName(Side.SIDEB, name, description);
            } else {
                throw new ConfigurationException("Info column with name: " + name + " has an invalid side");
            }
        }
    }
}
