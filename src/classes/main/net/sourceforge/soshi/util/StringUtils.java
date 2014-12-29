package net.sourceforge.soshi.util;

public class StringUtils
{
    public static String replace(String s, String original, String replacement)
    {
        StringBuffer sb = new StringBuffer();

        int last = 0;
        int pos;
        while ((pos = s.indexOf(original, last)) != -1) {
            sb.append(s.substring(last, pos));
            sb.append(replacement);
            last = pos + original.length();
        }

        if (last < s.length()) {
            sb.append(s.substring(last));
        }

        return sb.toString();
    }
}
