package net.sourceforge.soshi;

public class StandardComparator implements Comparator
{
    public boolean equals(String a, String b)
    {
        if (a == null) {
            return (b == null);
        }

        if (b == null) {
            return false;
        }

        return a.equals(b);
    }

    public String getCanonicalForm(String s)
    {
        return s;
    }
}
