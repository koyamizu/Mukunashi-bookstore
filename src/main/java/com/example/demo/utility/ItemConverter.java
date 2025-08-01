package com.example.demo.utility;

import com.example.demo.entity.Book;
import com.example.demo.entity.Item;
import com.example.demo.form.ItemForm;

public class ItemConverter {

	public static Item convertItem(ItemForm form) {
		Item item=new Item();
		Book book=new Book();
		book.setIsbn(form.getIsbn());
		book.setPrice(form.getPrice());
		book.setTitle(form.getTitle());
		item.setBook(book);
		item.setNum(form.getNum());
		return item;
	}
}
