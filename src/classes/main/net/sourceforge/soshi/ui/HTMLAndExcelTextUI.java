package net.sourceforge.soshi.ui;

import net.sourceforge.soshi.HTMLResultsWriter;
import net.sourceforge.soshi.InvalidValueException;
import net.sourceforge.soshi.Results;
import net.sourceforge.soshi.ExcelResultsWriter;

import net.sourceforge.soshi.config.ConfigurationException;
import net.sourceforge.soshi.config.ConfigurationFactory;
import net.sourceforge.soshi.config.SAXConfigurationFactory;


import java.io.IOException;

public class HTMLAndExcelTextUI extends BaseTextUI
{
    private String workbookFilename;
    private String htmlFilename;

    public static void main(String[] args)
    {
        new HTMLAndExcelTextUI().run(args);
    }
    
    public void run(String[] args)
    {
        if (args.length != 3) {
            printUsage();
        } else {
            String recFile = args[0];
            htmlFilename = args[1];
            workbookFilename = args[2];

            String[] refFilenames = new String[1];
            refFilenames[0] = recFile;

            ConfigurationFactory factory = new SAXConfigurationFactory();

            runRecs(factory, refFilenames);
        }
    }

    protected void writeResults(int recNo, Results results)
    {
        try {
            log("Writing to Excel file: " + workbookFilename);
            ExcelResultsWriter eWriter = new ExcelResultsWriter(workbookFilename);
            eWriter.write(results);
        } catch (IOException e) {
            System.out.println("A problem occured writing " + workbookFilename + ":");
            System.out.println(e.getMessage());
        } catch (ConfigurationException e) {
            System.out.println("A problem occured writing " + workbookFilename + ":");
            System.out.println(e.getMessage());
        } catch (InvalidValueException e) {
            System.out.println("A problem occured with the data when writing " + workbookFilename + ":");
            System.out.println(e.getMessage());
        }

        try {
            log("Writing to HTML file: " + htmlFilename);
            HTMLResultsWriter hWriter = new HTMLResultsWriter(htmlFilename);
            hWriter.write(results);
        } catch (IOException e) {
            System.out.println("A problem occured writing " + htmlFilename + ":");
            System.out.println(e.getMessage());
        }
    }

    private void printUsage()
    {
        System.out.println("Usage: ExcelAndHTMLTextUI <rec-config-file> <html-output-file> <excel-output-file>");
    }
}
