package net.sourceforge.soshi.ui;

import net.sourceforge.soshi.SQLToFile;

import net.sourceforge.soshi.util.IOUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class SQLToFileUI
{
    public static void main(String[] args)
    {
        new SQLToFileUI().run(args);
    }

    public void run(String[] args)
    {
        if (args.length < 6) {
            printUsage();
        }

        String sqlFilename = args[0];
        String driver = args[1];
        String url = args[2];
        String user = args[3];
        String password = args[4];
        String outputFilename = args[5];

        String[] queries = null;
        try {
            Reader reader = new FileReader(sqlFilename);
            String s = IOUtils.getAsString(reader);
            RE re = new RE("^[:space:]*go[:space:]*$", RE.MATCH_MULTILINE | RE.MATCH_CASEINDEPENDENT);
            queries = re.split(s);
        } catch (FileNotFoundException e) {
            System.out.println("The SQL file does not exist");
        } catch (IOException e) {
            System.out.println("A problem occured reading the SQL file");
        } catch (RESyntaxException e) {
            e.printStackTrace(System.out);
        }

        if (queries != null) {
            Connection conn = null;
            Statement statement = null;
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(url, user, password);
                statement = conn.createStatement();
                for (int i = 0; i < queries.length; i++) {
                    statement.execute(queries[i]);
                }
                
                ResultSet rs = statement.getResultSet();
                
                SQLToFile converter = new SQLToFile();
                converter.process(rs, outputFilename);
            } catch (ClassNotFoundException e) {
                System.out.println("The driver can not be loaded");
            } catch (SQLException e) {
                System.out.println("A problem occured running the SQL");
                e.printStackTrace(System.out);
            } catch (IOException e) {
                System.out.println("A problem occured writing the output file");
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }

    private void printUsage()
    {
        System.out.println("Usage: SQLToFileUI <sql-file> <driver> <url> <user> <password> <output-file>");
    }
}
