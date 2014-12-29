package net.sourceforge.soshi;

public class IgnoreCaseComparator implements Comparator
{
    public boolean equals(String a, String b)
    {
        if (a == null) {
            return (b == null);
        }

        if (b == null) {
            return false;
        }

        return a.equalsIgnoreCase(b);
    }

    public String getCanonicalForm(String s)
    {
        if (s == null) {
            return null;
        }

        return s.toUpperCase();
    }
}
