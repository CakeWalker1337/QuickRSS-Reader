package com.example.maxim.rss_parser.model;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Класс, содержащий информацию об изображении.
 * Внутри класса расставлены аннотации для парсинга по XML
 */
@Root(name = "enclosure", strict = false)
public class Enclosure {
    @Attribute(name = "length", required = false)
    private String lengthg;

    @Attribute(name = "type", required = false)
    private String type;

    @Attribute(name = "url", required = false)
    private String url;

    public String getLength() {
        return lengthg;
    }

    public void setLength(String lengthg) {
        this.lengthg = lengthg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ClassPojo [length = " + lengthg + ", type = " + type + ", url = " + url + "]";
    }
}