package net.sourceforge.soshi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SimpleResultsWriter implements ResultsWriter
{
    private PrintWriter out;

    public SimpleResultsWriter(OutputStream os)
    {
        this(new OutputStreamWriter(os));
    }

    public SimpleResultsWriter(Writer writer)
    {
        this.out = new PrintWriter(writer);
    }

    public void write(Results results)
        throws IOException
    {
        Side sideA = results.getSide(Side.SIDEA);
        Side sideB = results.getSide(Side.SIDEB);
        writeMissingKeys(out, results, sideA, Side.SIDEA);
        writeMissingKeys(out, results, sideB, Side.SIDEB);
        out.println("Differences in matching keys:");
        writeRowDifferences(out, results);

        if (out.checkError()) {
            throw new IOException();
        }
    }

    private void writeMissingKeys(PrintWriter out, Results results, Side side, int sideType)
    {
        String name = side.getName();
        Collection missing = results.getMissingKeys(sideType);
        if (missing.size() == 0) {
            out.println(name + " had no missing keys");
        } else {
            out.println(name + " had the following keys missing:");
            for (Iterator i = missing.iterator(); i.hasNext();) {
                Key key = (Key) i.next();
                out.println(key.toString());
            }
        }
    }

    private void writeRowDifferences(PrintWriter out, Results results)
    {
        Map differences = results.getDifferencesByRow();
        for (Iterator i = differences.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Key key = (Key) entry.getKey();
            Set rowDifference = (Set) entry.getValue();
            out.println(key.toString() + " has the following differences");
            writeDifferences(out, rowDifference);
        }
    }

    private void writeDifferences(PrintWriter out, Set rowDifference)
    {
        for (Iterator i = rowDifference.iterator(); i.hasNext();) {
            Difference difference = (Difference) i.next();
            StringBuffer sb = new StringBuffer();
            sb.append(difference.getColumn());
            sb.append(": ");
            sb.append("<");
            sb.append(difference.getValueA());
            sb.append(">");
            sb.append(" - ");
            sb.append("<");
            sb.append(difference.getValueB());
            sb.append(">");
            out.println(sb.toString());
        }
    }
}
