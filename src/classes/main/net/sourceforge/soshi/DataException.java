package net.sourceforge.soshi;

import net.sourceforge.soshi.util.TraceableException;

public class DataException extends TraceableException
{
    public DataException()
    {
        super();
    }

    public DataException(String message)
    {
        super(message);
    }

    public DataException(Exception exception)
    {
        super(exception);
    }

    public DataException(Exception exception, String message)
    {
        super(exception, message);
    }
}
