package net.sourceforge.soshi;

public class IgnoreCaseAndWhitespaceComparator implements Comparator
{
    public boolean equals(String a, String b)
    {
        if (a == null) {
            return (b == null);
        }

        if (b == null) {
            return false;
        }

        return getCanonicalForm(a).equalsIgnoreCase(getCanonicalForm(b));
    }

    public String getCanonicalForm(String s)
    {
        if (s == null) {
            return null;
        }

        s = s.toUpperCase();

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
