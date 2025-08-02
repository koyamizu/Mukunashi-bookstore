package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.BookStock;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Item;
import com.example.demo.exception.IlligalActionException;

public interface BookService {

	List<BookStock> getAllBookStocks(Cart cart) throws IlligalActionException;
	
	Cart addItem(Item item, Cart cart) throws IlligalActionException;

}
