package com.example.maxim.rss_parser.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Класс, содержащий информацию о версии запроса к каналу.
 * Внутри класса расставлены аннотации для парсинга по XML
 */
@Root(name = "rss", strict = false)
public class Article {

    @Element(name = "channel")
    private Channel channel;

    @Attribute(name = "version")
    private String version;


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ClassPojo [channel = " + channel + "]";
    }

}
