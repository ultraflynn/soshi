package net.sourceforge.soshi;

import net.sourceforge.soshi.util.TraceableException;

public class InvalidValueException extends TraceableException
{
    public InvalidValueException()
    {
        super();
    }

    public InvalidValueException(String message)
    {
        super(message);
    }

    public InvalidValueException(Exception exception)
    {
        super(exception);
    }

    public InvalidValueException(Exception exception, String message)
    {
        super(exception, message);
    }
}
