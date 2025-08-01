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
	
//	TODO カートにある点数分だけ在庫数を減らす操作
	@Override
	public List<BookStock> getAllBookStocks(Optional<List<Item>> itemsInCartNullable) throws IlligalActionException {
//		カートに商品が一つもなければ空のリストを生成（NullPointerException対策）
//		カートの商品 or 空のリストがitemsInCartに代入される
		List<Item> cart=itemsInCartNullable.orElse(new ArrayList<Item>());
		List<BookStock> bookStocks=bookMapper.selectAll();
		//itemsInCartの要素一つずつについて、isbnがbookStocksの要素のうち一致しているものがあれば、在庫を減らす。
		for(int i=0;i<bookStocks.size();i++) {
			for(int j=0;j<cart.size();j++) {
				if(Objects.equals(bookStocks.get(i).getBook().getIsbn(),cart.get(j).getBook().getIsbn())){
					bookStocks.get(i).updateStockNum(cart.get(i).getNum());
				}
			}
		}
		return bookStocks;
	}
	
//	addItemメソッド中の操作が何らかの原因で中断された時、DBの操作など全てをロールバックする
	@Transactional
	@Override
	public List<Item> addItem(ItemForm cartForm,Optional<List<Item>> itemsInCartNullable) throws IlligalActionException {
		Item cart = ItemConverter.convertCart(cartForm);
		
//		カートに商品が一つもなければ空のリストを生成（NullPointerException対策）
//		カートの商品 or 空のリストがitemsInCartに代入される
		List<Item> itemsInCart=itemsInCartNullable.orElse(new ArrayList<Item>());
		
		itemsInCart.add(cart);
		
		return itemsInCart;
	}

}
