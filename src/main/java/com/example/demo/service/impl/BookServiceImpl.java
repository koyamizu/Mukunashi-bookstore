package com.example.demo.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookStock;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Item;
import com.example.demo.exception.IlligalActionException;
import com.example.demo.repository.BookMapper;
import com.example.demo.service.BookService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookMapper bookMapper;

	//	TODO カートにある点数分だけ在庫数を減らす操作
	@Override
	public List<BookStock> getAllBookStocks(Cart cart) throws IlligalActionException {

		List<BookStock> bookStocks = bookMapper.selectAll();
		
		if(Objects.equals(cart, null)) {
			return bookStocks;
		}
//		カートにある書籍に関しては、在庫数を減らして表示する
		for (BookStock bs : bookStocks) {
			for (Map<Book, Integer> item : cart.getItems()) {
				if (item.containsKey(bs.getBook())) {
					bs.updateStockNum(item.get(bs.getBook()));
				}
			}
		}
		return bookStocks;
	}

	//	addItemメソッド中の操作が何らかの原因で中断された時、DBの操作など全てをロールバックする
	@Transactional
	@Override
	public Cart addItem(Item item, Cart cart) throws IlligalActionException {
		if(Objects.equals(cart, null)) {
			cart=new Cart();
		}
		String isbn=item.getBook().getIsbn();
//		item.setBookInformation(bookMapper.select(isbn));
		Book book=bookMapper.select(isbn);
		
		Integer stockNum = bookMapper.selectStockNum(isbn);
		if (!item.isValidate(stockNum)) {
			throw new IlligalActionException("在庫数を超えています");
		}
		cart.add(book,item.getNum());
		
		return cart;
	}

}
