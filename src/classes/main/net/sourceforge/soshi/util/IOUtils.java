package net.sourceforge.soshi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class IOUtils
{
    public static String getAsString(Reader reader)
        throws IOException
    {
        BufferedReader buff = new BufferedReader(reader);
        StringBuffer sb = new StringBuffer();
        int i;
        
        while ((i = reader.read()) != -1) {
            char c = (char) i;
            sb.append(c);
        }

        return sb.toString();
    }
}
