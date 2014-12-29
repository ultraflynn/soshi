package net.sourceforge.soshi;

public interface Comparator
{
    public boolean equals(String a, String b);

    public String getCanonicalForm(String o);
}
