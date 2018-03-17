package com.example.maxim.rss_parser.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class Item
{

    @Element(name = "guid")
    private String guid;

    @Element(name = "pubDate")
    private String pubDate;

    @Element(name = "title")
    private String title;

    @Element(name = "category")
    private String category;

    @Element(name = "enclosure")
    private Enclosure enclosure;

    @Element(name = "description")
    private String description;

    @Element(name = "link")
    private String link;


    public String getGuid ()
    {
        return guid;
    }

    public void setGuid (String guid)
    {
        this.guid = guid;
    }

    public String getPubDate ()
    {
        return pubDate;
    }

    public void setPubDate (String pubDate)
    {
        this.pubDate = pubDate;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getCategory ()
    {
        return category;
    }

    public void setCategory (String category)
    {
        this.category = category;
    }

    public Enclosure getEnclosure ()
    {
        return enclosure;
    }

    public void setEnclosure (Enclosure enclosure)
    {
        this.enclosure = enclosure;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getLink ()
    {
        return link;
    }

    public void setLink (String link)
    {
        this.link = link;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [guid = "+guid+", pubDate = "+pubDate+", title = "+title+", category = "+category+", enclosure = "+enclosure+", description = "+description+", link = "+link+"]";
    }
}