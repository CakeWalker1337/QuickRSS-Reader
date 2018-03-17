package com.example.maxim.rss_parser.model;

/**
 * Created by Maxim on 02.03.2018.
 */

public class Card {
    private String helloMessage;
    private String description;

    public String getHelloMessage()
    {
        return helloMessage;
    }

    public void setHelloMessage(String newHelloMessage)
    {
        helloMessage = newHelloMessage;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String newDescription)
    {
        description = newDescription;
    }

    public Card(String newHelloMessage, String newDescription)
    {
        helloMessage = newHelloMessage;
        description = newDescription;
    }

}
