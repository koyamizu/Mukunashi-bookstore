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

		//		全ての書籍を取得
		List<BookStock> bookStocks = bookMapper.戻り値がBookStockリストで、「全ての書籍の在庫を抽出するメソッド」;
		//		カートが空の時は、そのまま書籍一覧を返す
		if (Objects.equals(cart, null)) {
			return bookStocks;
		}
		//		カートにある商品をマップとして取得
		Map<Book, Integer> items = cart.getItems();
		//		カートにすでに追加されている商品に関しては、追加した商品数を在庫数から減らす
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
		//		cartがnullだった場合、Cart型のインスタンスを初期化する。NullPointerException対策。
		if (Objects.equals(cart, null)) {
			cart = new Cart();
		}
		//		この時点では、item.bookにはisbnにのみ値が格納されている
		String isbn = item.getBook().getIsbn();
		//		bookインスタンスの全てのフィールドに値を格納して初期化
		Book book = bookMapper.戻り値がBookで、「isbnコードをもとに、書籍を抽出するメソッド」;
		Integer stockNum = bookMapper.戻り値がIntegerで、「isbnコードをもとに、在庫数を抽出するメソッド」;
		//		Null安全性を保証。NullPointerException対策。
		Optional<Integer> cartNum = Optional.ofNullable(cart.getItems().get(book));
		//		cartNumがnullだったら、cartNum = 0にする。
		//		在庫数からカートに追加してある数を引き、カートに追加しようとする数が在庫数を越してないか確かめる
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

		//		Entry<Book,Integer>にすることで、キーを取り出すことができる
		for (Map.Entry<Book, Integer> item : items.entrySet()) {
			Book book = item.getKey();
			Integer num = item.getValue();
			//			購入しようとする書籍の在庫数をDBから取り出す
			Integer stockNum = bookMapper.戻り値がIntegerで、「isbnコードをもとに、在庫数を抽出するメソッド」;
			//			購入しようとする書籍が在庫数を越していたら、例外放出
			if (stockNum < num) {
				throw new IlligalActionException("『" + book.getTitle() + "』は在庫がありません");
			}
			bookMapper.update(book.getIsbn(), stockNum - num);
		}

		Integer customerId = bookMapper.selectCustomerId(customer);
		if (Objects.equals(customerId, null)) {
			//		客の情報を登録
			bookMapper.Customerクラスのオブジェクトを挿入するメソッド;
			//		AUTO_INCREMENTによって生成された顧客IDを抽出
			customerId = bookMapper.selectCustomerId(customer);
		}

		//		注文情報を登録
		bookMapper.insertOrder(customerId);
		//		AUTO_INCREMENTによって生成された注文IDを抽出。
		//		顧客IDと日付だけだと、複数の結果が戻ってくる場合があり得るのでリストで受け取る
		List<Integer> orderIds = bookMapper.selectOrderId(customerId, LocalDate.now());
		//		最新の注文の注文IDを取り出す
		Integer latestOrderId = orderIds.get(0);

		//		注文した書籍を登録
		//		注文IDと書籍は1対多の関係なので、注文IDを先に取得する必要がある
		bookMapper.insertItems(latestOrderId, cart.getItems().entrySet());

		return latestOrderId;
	}

	@Override
	public History getHistoryByOrderId(Integer orderId) throws NoHistoryException {
		History history = bookMapper.selectOrderHistory(orderId);
		if (Objects.equals(history, null)) {
			throw new NoHistoryException("注文が存在しません");
		}
		return history;
	}

	@Override
	public void deleteOrderByOrderId(Integer orderId) {
		bookMapper.deleteOrder(orderId);
	}
}
