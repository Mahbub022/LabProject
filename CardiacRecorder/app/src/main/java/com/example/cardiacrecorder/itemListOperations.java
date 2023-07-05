package com.example.cardiacrecorder;

import java.util.ArrayList;
import java.util.List;

public class itemListOperations {
    private ArrayList<Items>items = new ArrayList<>();

    public ArrayList<Items> getItems(){
        ArrayList<Items> itemList = items;
        return itemList;
    }

    public void addItems(Items item){
        if(items.contains(item))
        {
            throw new IllegalArgumentException();
        }

        items.add(item);
    }


    public void delete(Items item)
    {
        if(!items.contains(item))
        {
            throw new IllegalArgumentException();
        }

        items.remove(item);
    }

    public void update(int position, Items item)
    {
        items.set(position, item);
    }
    public int count()
    {
        return items.size();
    }
}
