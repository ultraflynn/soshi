package net.sourceforge.soshi.config;

import java.net.URL;

import net.sourceforge.carbine.ParseException;
import net.sourceforge.carbine.TagLoader;

public class CarbineConfigurationFactory implements ConfigurationFactory
{
    private static final String CARBINE_TAG_FILE = "net/sourceforge/soshi/config/carbine-tags.xml";
    private URL additionalTags;

    public CarbineConfigurationFactory()
        throws ConfigurationException
    {
        Class clazz = this.getClass();
        ClassLoader classLoader = clazz.getClassLoader();
        additionalTags = classLoader.getResource(CARBINE_TAG_FILE);

        if (additionalTags == null) {
            throw new ConfigurationException("Could not find carbine tag file");
        }
    }

    public Configuration getConfiguration(URL url)
        throws ConfigurationException
    {
        try {
            TagLoader loader = new TagLoader();
            loader.addAdditionalTags(additionalTags);

            return (Configuration) loader.parse(url);
        } catch (ParseException e) {
            throw new ConfigurationException(e);
        }
    }
}
