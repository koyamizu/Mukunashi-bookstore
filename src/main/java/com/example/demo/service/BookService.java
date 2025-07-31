package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.BookStock;
import com.example.demo.entity.Item;
import com.example.demo.exception.IlligalActionException;
import com.example.demo.form.ItemForm;

public interface BookService {

	List<BookStock> getAllBookStocks(Optional<List<Item>> itemsInCartNullable);
	
	List<Item> addItem(ItemForm itemForm, Optional<List<Item>> itemsInCartNullable) throws IlligalActionException;

}
