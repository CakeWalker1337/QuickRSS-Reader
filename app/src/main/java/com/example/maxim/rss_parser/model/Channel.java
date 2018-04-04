package com.example.maxim.rss_parser.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;


@Root(name = "channel", strict = false)
public class Channel {

    @Element(name = "title")
    private String title;

    @Element(name = "description")
    private String description;

    @Path("link")
    @Text(required = false)
    private String link;

    @ElementList(name = "item", inline = true)
    private List<Item> items;

    @Element(name = "image")
    private Image image;

    @Element(name = "language")
    private String language;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void clearItems() {
        items.clear();
    }

    public int getItemsSize() { return items.size(); }



    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "ClassPojo [title = " + title + ", description = " + description + ", link = " + link + ", item = " + items.size() + ", image = " + image + ", language = " + language + "]";
    }

}
