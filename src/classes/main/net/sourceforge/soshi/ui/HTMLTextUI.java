package net.sourceforge.soshi.ui;

import net.sourceforge.soshi.HTMLResultsWriter;
import net.sourceforge.soshi.Results;

import net.sourceforge.soshi.config.ConfigurationFactory;
import net.sourceforge.soshi.config.SAXConfigurationFactory;

import java.io.IOException;

public class HTMLTextUI extends BaseTextUI
{
    private String htmlFilename;

    public static void main(String[] args)
    {
        new HTMLTextUI().run(args);
    }
    
    public void run(String[] args)
    {
        if (args.length != 2) {
            printUsage();
        } else {
            String recFile = args[0];
            htmlFilename = args[1];

            String[] refFilenames = new String[1];
            refFilenames[0] = recFile;

            ConfigurationFactory factory = new SAXConfigurationFactory();

            runRecs(factory, refFilenames);
        }
    }

    protected void writeResults(int recNo, Results results)
    {
        try {
            log("Writing to HTML file: " + htmlFilename);
            HTMLResultsWriter writer = new HTMLResultsWriter(htmlFilename);
            writer.write(results);
        } catch (IOException e) {
            System.out.println("A problem occured writing " + htmlFilename + ":");
            System.out.println(e.getMessage());
        }
    }

    private void printUsage()
    {
        System.out.println("Usage: HTMLTextUI <rec-config-file> <html-output-file>");
    }
}
