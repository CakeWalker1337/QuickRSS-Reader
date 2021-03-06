package com.tenxgames.maxim.rss_parser.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

/**
 * Класс, содержащий информацию о канале.
 * Внутри класса расставлены аннотации для парсинга по XML
 */
@Root(name = "channel", strict = false)
public class Channel {

    private int id = 0;
    private boolean validity = true;

    @Element(name = "title", required = false)
    private String title = "";

    @Element(name = "description", required = false)
    private String description = "";

    @Path("link")
    @Text(required = false)
    private String link = "";

    @ElementList(name = "item", inline = true)
    private List<Item> items;

    @Element(name = "image", required = false)
    private Image image;

    @Element(name = "language", required = false)
    private String language = "";

    public boolean isValid() {
        return validity;
    }

    public void setValidity(boolean newValidity) {
        validity = newValidity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getItemsSize() {
        return items.size();
    }

    public void refreshData(Article newArticle) {
        clearItems();
        for (int newItemIndex = 0; newItemIndex < newArticle.getChannel().getItemsSize(); newItemIndex++) {
            addItem(newArticle.getChannel().getItem(newItemIndex));
        }
    }

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
