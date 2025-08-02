package com.example.demo.helper;

import com.example.demo.entity.Book;
import com.example.demo.entity.Item;
import com.example.demo.form.ItemForm;

public class ItemConverter {

	public static Item convertItem(ItemForm form) {
		Book book=new Book();
		book.setIsbn(form.getIsbn());
//		book.setPrice(form.getPrice());
//		book.setTitle(form.getTitle());
//		Map<Book,Integer> item=Map.of(book,form.getNum());
//		return new Item(item);
		return new Item(book,form.getNum());
	}
}
