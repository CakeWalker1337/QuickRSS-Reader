package com.example.maxim.rss_parser.model;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "enclosure", strict = false)
public class Enclosure
{
    @Attribute(name = "length", required = false)
    private String length;

    @Attribute(name = "type")
    private String type;

    @Attribute(name = "url")
    private String url;

    public String getLength ()
    {
        return length;
    }

    public void setLength (String length)
    {
        this.length = length;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [length = "+length+", type = "+type+", url = "+url+"]";
    }
}