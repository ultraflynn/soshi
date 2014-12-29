package net.sourceforge.soshi.util;

public class HTMLUtils
{
    public static String escape(String s)
    {
        String result = s;
        result = StringUtils.replace(result, "&", "&amp;");
        result = StringUtils.replace(result, "<", "&lt;");
        result = StringUtils.replace(result, ">", "&gt;");
        result = StringUtils.replace(result, "\"", "&quot;");
        return result;
    }
}
