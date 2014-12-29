package net.sourceforge.soshi;

import java.io.IOException;

public interface ResultsWriter
{
    public void write(Results results)
        throws IOException, InvalidValueException;
}
