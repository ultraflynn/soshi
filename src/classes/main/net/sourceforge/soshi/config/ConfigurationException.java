package net.sourceforge.soshi.config;

import net.sourceforge.soshi.util.TraceableException;

public class ConfigurationException extends TraceableException
{
    public ConfigurationException()
    {
        super();
    }

    public ConfigurationException(String message)
    {
        super(message);
    }

    public ConfigurationException(Exception exception)
    {
        super(exception);
    }

    public ConfigurationException(Exception exception, String message)
    {
        super(exception, message);
    }
}
