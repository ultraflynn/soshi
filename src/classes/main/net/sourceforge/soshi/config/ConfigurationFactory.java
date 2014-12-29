package net.sourceforge.soshi.config;

import java.net.URL;

public interface ConfigurationFactory
{
    Configuration getConfiguration(URL url)
        throws ConfigurationException;
}
