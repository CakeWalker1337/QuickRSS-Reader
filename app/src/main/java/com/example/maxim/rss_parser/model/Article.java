package com.example.maxim.rss_parser.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class Article {

    @Element(name = "channel")
    private Channel channel;

    public Channel getChannel ()
    {
        return channel;
    }

    public void setChannel (Channel channel)
    {
        this.channel = channel;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [channel = "+channel+"]";
    }

}
