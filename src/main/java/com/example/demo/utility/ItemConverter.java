package com.example.demo.utility;

import com.example.demo.entity.Item;
import com.example.demo.form.ItemForm;

public class ItemConverter {

	public static Item convertItem(ItemForm form) {
		Item item=new Item();
		item.setIsbn(form.getIsbn());
		item.setNum(form.getNum());
		item.setPrice(form.getPrice());
		return item;
	}
}
