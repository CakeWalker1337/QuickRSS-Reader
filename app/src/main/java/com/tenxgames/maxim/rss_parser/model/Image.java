package com.tenxgames.maxim.rss_parser.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Класс, содержащий информацию об изображении.
 * Внутри класса расставлены аннотации для парсинга по XML
 */
@Root(name = "image", strict = false)
public class Image {

    @Element(name = "title", required = false)
    private String title;

    @Element(name = "height", required = false)
    private String height;

    @Element(name = "link", required = false)
    private String link;

    @Element(name = "width", required = false)
    private String width;

    @Element(name = "url", required = false)
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ClassPojo [title = " + title + ", height = " + height + ", link = " + link + ", width = " + width + ", url = " + url + "]";
    }
}