package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.BookStock;
import com.example.demo.entity.Cart;
import com.example.demo.entity.History;
import com.example.demo.entity.Item;
import com.example.demo.entity.Order;
import com.example.demo.exception.IlligalActionException;
import com.example.demo.exception.NoHistoryException;
import com.example.demo.repository.BookMapper;
import com.example.demo.service.BookService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookMapper bookMapper;

	@Override
	public List<BookStock> getAllBookStocks(Cart cart) throws IlligalActionException {

		return bookStocks;
	}

	@Override
	public Cart addItem(Item item, Cart cart) throws IlligalActionException {

		return cart;
	}

	//resisterメソッド中の操作が何らかの原因で中断された時、DBの操作など全てをロールバックする
	@Transactional
	@Override
	public Integer executeOrder(Order order) throws IlligalActionException {

		return latestOrderId;
	}

	@Override
	public History getHistoryByOrderId(Integer orderId) throws NoHistoryException {
		
		return history;
	}

	@Override
	public void deleteOrderByOrderId(Integer orderId) {
		
	}
}
