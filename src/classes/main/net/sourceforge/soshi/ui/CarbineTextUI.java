package net.sourceforge.soshi.ui;

import net.sourceforge.soshi.ExcelResultsWriter;
import net.sourceforge.soshi.InvalidValueException;
import net.sourceforge.soshi.Results;

import net.sourceforge.soshi.config.ConfigurationException;
import net.sourceforge.soshi.config.ConfigurationFactory;
import net.sourceforge.soshi.config.CarbineConfigurationFactory;

import java.io.IOException;

public class CarbineTextUI extends BaseTextUI
{
    private String workbookFilename;

    public static void main(String[] args)
    {
        new CarbineTextUI().run(args);
    }
    
    public void run(String[] args)
    {
        try {
            if (args.length < 2) {
                printUsage();
            } else {
                int recCount = args.length - 1;

                String[] recFilenames = new String[recCount];

                workbookFilename = args[recCount];

                for (int i = 0; i < recCount; i++) {
                    recFilenames[i] = args[i];
                }

                ConfigurationFactory factory = new CarbineConfigurationFactory();

                runRecs(factory, recFilenames);
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    protected void writeResults(int recNo, Results results)
    {
        try {
            log("Writing to Excel file: " + workbookFilename);
            ExcelResultsWriter writer = new ExcelResultsWriter(workbookFilename);
            writer.write(results);
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
    }

    private void printUsage()
    {
        System.out.println("Usage: TextUI <rec-config-file>... <excel-output-file>");
    }
}

