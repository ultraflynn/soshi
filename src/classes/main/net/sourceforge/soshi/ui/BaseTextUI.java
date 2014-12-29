package net.sourceforge.soshi.ui;

import net.sourceforge.soshi.DataException;
import net.sourceforge.soshi.Reconciler;
import net.sourceforge.soshi.Results;
import net.sourceforge.soshi.Side;

import net.sourceforge.soshi.config.Configuration;
import net.sourceforge.soshi.config.ConfigurationException;
import net.sourceforge.soshi.config.ConfigurationFactory;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

public abstract class BaseTextUI
{
    protected abstract void writeResults(int recNo, Results results);

    protected void runRecs(ConfigurationFactory factory, String[] recFilenames)
    {
        for (int i = 0; i < recFilenames.length; i++) {
            String recFilename = recFilenames[i];
            Results results = null;
            try {
                results = runRec(factory, recFilename);
            } catch (MalformedURLException e) {
                System.out.println(recFilename + " is a badly formatted filename");
            } catch (ConfigurationException e) {
                System.out.println(recFilename + " was not a valid configuration file:");
                System.out.println(e.getMessage());
            } catch (DataException e) {
                System.out.println("The " + recFilename + " reconciliation had a problem while loading data:");
                System.out.println(e.getMessage());
            }

            if (results != null) {
                writeResults(i, results);
            }
        }
            
        log("Finished");
    }

    protected void log(String message)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getDateString("hh:mm:ss"));
        sb.append(": ");
        sb.append(message);
        System.out.println(sb.toString());
    }
    
    private Results runRec(ConfigurationFactory factory, String configFilename)
        throws MalformedURLException, ConfigurationException, DataException
    {
        log("Loading rec configuration: " + configFilename);
        File configFile = new File(configFilename);
        URL configFileURL = configFile.toURL();
        Configuration config = factory.getConfiguration(configFileURL);
        
        Side sideA = config.getSide(Side.SIDEA);
        Side sideB = config.getSide(Side.SIDEB);
        
        log("Loading side A");
        sideA.load();
        
        log("Loading side B");
        sideB.load();
        
        log("Running reconciliation: " + config.getName());
        Reconciler reconciler = new Reconciler(config);
        Results results = reconciler.reconcile(sideA, sideB);
        
        return results;
    }

    private String getDateString(String format)
    {
        DateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();

        return formatter.format(date);
    }
}
