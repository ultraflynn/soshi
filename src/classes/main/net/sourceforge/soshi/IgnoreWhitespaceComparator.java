package net.sourceforge.soshi;

public class IgnoreWhitespaceComparator implements Comparator
{
    public boolean equals(String a, String b)
    {
        if (a == null) {
            return (b == null);
        }

        if (b == null) {
            return false;
        }

        return getCanonicalForm(a).equals(getCanonicalForm(b));
    }

    public String getCanonicalForm(String s)
    {
        if (s == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(Character.isWhitespace(c))) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
