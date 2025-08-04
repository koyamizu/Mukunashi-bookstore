package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookStock;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Customer;
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

		List<BookStock> bookStocks = bookMapper.selectAll();

		if (Objects.equals(cart, null)) {
			return bookStocks;
		}

		Map<Book, Integer> items = cart.getItems();
		//		カートにある書籍に関しては、在庫数を減らして表示する
		for (BookStock bs : bookStocks) {
			Book book = bs.getBook();
			if (items.containsKey(book)) {
				bs.updateStockNum(items.get(book));
			}
		}
		return bookStocks;
	}

	@Override
	public Cart addItem(Item item, Cart cart) throws IlligalActionException {
		if (Objects.equals(cart, null)) {
			cart = new Cart();
		}
		String isbn = item.getBook().getIsbn();
		Book book = bookMapper.select(isbn);
		Integer stockNum = bookMapper.selectStockNum(isbn);
		Optional<Integer> cartNum = Optional.ofNullable(cart.getItems().get(book));

		if (!item.isValidate(stockNum - cartNum.orElse(0))) {
			throw new IlligalActionException("在庫数を超えています");
		}
		cart.add(book, item.getNum());

		return cart;
	}

	//resisterメソッド中の操作が何らかの原因で中断された時、DBの操作など全てをロールバックする
	@Transactional
	@Override
	public Integer executeOrder(Order order) throws IlligalActionException {
		Customer customer = order.getCustomer();
		Cart cart = order.getCart();

		Map<Book, Integer> items = cart.getItems();

		for (Map.Entry<Book, Integer> item : items.entrySet()) {
			Book book = item.getKey();
			Integer num = item.getValue();
//			購入しようとする商品の在庫数をDBから取り出す
			Integer stockNum = bookMapper.selectStockNum(book.getIsbn());
			if (stockNum < num) {
				throw new IlligalActionException("『" + book.getTitle() + "』は在庫がありません");
			}
			bookMapper.update(book.getIsbn(), stockNum - num);
		}

		bookMapper.insertCustomer(customer);
		Integer customerId = bookMapper.selectCustomerId(customer);

		bookMapper.insertOrder(customerId);
		Integer orderId = bookMapper.selectOrderId(customerId, LocalDate.now());

		bookMapper.insertItems(orderId, cart.getItems().entrySet());
		
		return orderId;
	}

	@Override
	public History getHistoryByOrderId(Integer orderId) throws NoHistoryException {
		History history=bookMapper.selectOrderHistory(orderId);
		if(Objects.equals(history, null)) {
			throw new NoHistoryException("注文が存在しません");
		}
		return history;
	}

	@Override
	public void deleteOrderByOrderId(Integer orderId) {
		bookMapper.deleteOrder(orderId);
	}
}
