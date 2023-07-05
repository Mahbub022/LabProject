package com.example.cardiacrecorder;

import org.junit.Test;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class itemUnitTest {
    @Test
    public void sizeTest()
    {
        Items item1 = new Items("150","80", "72", "healthy", "6-07-2023", "12:38", "10" );
        Items item2 = new Items("170","60", "62", "Not quite healthy", "6-07-2023", "12:45", "23" );
        itemListOperations items = new itemListOperations();
        items.addItems(item1);
        items.addItems(item2);
        assertEquals(2,items.count());


    }

//    @Test
//    public void testGetItems()
//    {
//        itemListOperations itemList = new itemListOperations();
//        Items item1 = new Items("150","80", "72", "healthy", "6-07-2023", "12:38", "10" );
//        itemList.addItems(item1);
//
//        Items item2 = new Items("170","60", "62", "Not quite healthy", "6-07-2023", "12:45", "23" );
//        itemList.addItems(item2);
//
//        ArrayList<Items> expectedList = new ArrayList<>();
//        expectedList.add(new Items("150", "80", "72", "healthy", "6-07-2023", "12:38", "10"));
//        expectedList.add(new Items("170", "60", "62", "Not quite healthy", "6-07-2023", "12:45", "23"));
//
//        ArrayList<Items> actualList = itemList.getItems();
//
//        assertEquals(expectedList.size(), actualList.size());
////        assertEquals(expectedList.get(0), actualList.get(0));
////        for (int i = 0; i < expectedList.size(); i++) {
////            assertEquals(expectedList.get(i), actualList.get(i));
////        }
//
//    }

    @Test
    public void testAdd()
    {
        itemListOperations items = new itemListOperations();

        Items item1 = new Items("150","80", "72", "healthy", "6-07-2023", "12:38", "10" );
        items.addItems(item1);

        Items item2 = new Items("170","60", "62", "Not quite healthy", "6-07-2023", "12:45", "23" );
        items.addItems(item2);

        assertEquals(2, items.getItems().size());
        assertTrue(items.getItems().contains(item1));

    }

    @Test
    public void testAddException(){
        itemListOperations items = new itemListOperations();

        Items item1 = new Items("150","80", "72", "healthy", "6-07-2023", "12:38", "10" );
        items.addItems(item1);

        assertThrows(IllegalArgumentException.class, ()->{
            items.addItems(item1);
        });
    }

    @Test
    public void testDelete()
    {
        itemListOperations items = new itemListOperations();

        Items item1 = new Items("150","80", "72", "healthy", "6-07-2023", "12:38", "10" );
        items.addItems(item1);

        Items item2 = new Items("170","60", "62", "Not quite healthy", "6-07-2023", "12:45", "23" );
        items.addItems(item2);

        items.delete(item1);

        assertEquals(1, items.getItems().size());
        assertFalse(items.getItems().contains(item1));
    }

    @Test
    public void testDeleteException()
    {
        itemListOperations items = new itemListOperations();

        Items item1 = new Items("150","80", "72", "healthy", "6-07-2023", "12:38", "10" );
        items.addItems(item1);

        Items item2 = new Items("170","60", "62", "Not quite healthy", "6-07-2023", "12:45", "23" );
        items.addItems(item2);

        items.delete(item1);

        assertThrows(IllegalArgumentException.class, ()->{
            items.delete(item1);
        });
    }

    @Test
    public void testUpdate()
    {
        itemListOperations items = new itemListOperations();

        Items item1 = new Items("150","80", "72", "healthy", "6-07-2023", "12:38", "10" );
        items.addItems(item1);

        Items item2 = new Items("170","60", "62", "Not quite healthy", "6-07-2023", "12:45", "23" );
        items.update(0, item2);

        assertFalse(items.getItems().contains(item1));
        assertTrue(items.getItems().contains(item2));




    }

}
