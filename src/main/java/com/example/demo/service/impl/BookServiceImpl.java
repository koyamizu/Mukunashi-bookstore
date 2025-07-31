package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.BookStock;
import com.example.demo.entity.Item;
import com.example.demo.exception.IlligalActionException;
import com.example.demo.form.ItemForm;
import com.example.demo.repository.BookMapper;
import com.example.demo.service.BookService;
import com.example.demo.utility.ItemConverter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{

	private final BookMapper bookMapper;
	
	@Override
	public List<BookStock> getAllBookStocks(Optional<List<Item>> itemsInCartNullable) {
//		カートに商品が一つもなければ空のリストを生成（NullPointerException対策）
//		カートの商品 or 空のリストがitemsInCartに代入される
		List<Item> itemsInCart=itemsInCartNullable.orElse(new ArrayList<Item>());
		List<BookStock> bookStocks=bookMapper.selectAll();
		//itemsInCartの要素一つずつについて、isbnがbookStocksの要素のうち一致しているものがあれば、在庫を減らす。
//		return bookStocks.stream().filter(b->b.getBook().getIsbn()==)
	}
	
//	addItemメソッド中の操作が何らかの原因で中断された時、DBの操作など全てをロールバックする
	@Transactional
	@Override
	public List<Item> addItem(ItemForm itemForm,Optional<List<Item>> itemsInCartNullable) throws IlligalActionException {
		Item item = ItemConverter.convertItem(itemForm);
		
//		DBからタイトルを抽出する
//		もちろん、フォーム入力の時点で同時にタイトルを送信してもいい
		item.setTitle(bookMapper.selectTitle(item));
		
//		カートに商品が一つもなければ空のリストを生成（NullPointerException対策）
//		カートの商品 or 空のリストがitemsInCartに代入される
		List<Item> itemsInCart=itemsInCartNullable.orElse(new ArrayList<Item>());
		
//		Stream APIによるメソッドチェーン
//		「追加する商品と同じ商品が既にカートにあれば取り出し、alreadyExistsに代入。
//		なければ、nullを代入している」と考えていただければOKです
		Item alreadyExists=itemsInCart.stream()
				.filter(i->Objects.equals(i.getIsbn(),item.getIsbn()))
				.findFirst().orElse(null);
		
//		このクラス内にあるプライベートメソッド
		updateStockNum(item);
		
//		追加する商品と同じ商品がカートになければ、リストに追加する
		if(Objects.equals(alreadyExists, null)) {
			itemsInCart.add(item);
			return itemsInCart;
		}
		
//		追加する商品と同じ商品がカートにあれば、数を追加する。
		Integer index=itemsInCart.indexOf(alreadyExists);
		itemsInCart.get(index).setNum(alreadyExists.getNum()+item.getNum());
		
		return itemsInCart;
	}

	private void updateStockNum(Item item) throws IlligalActionException {
//		カートに追加しようとした商品の在庫情報をDBから取り出す
		BookStock targetBookStock=bookMapper.select(item);
//		updateStockNumは、BookStockのインスタンスメソッド
//		在庫数以上の冊数をカートに追加しようとすると、IlligalActionExceptionを放出する
//		IlligalActionExceptionは、独自に作成したオリジナルの例外クラス（Exceptionクラスを継承している）
		BookStock latestBookStock=targetBookStock.updateStockNum(item.getNum());
		bookMapper.update(latestBookStock);
	}
}
