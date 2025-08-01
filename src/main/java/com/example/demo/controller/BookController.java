package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.BookStock;
import com.example.demo.entity.Item;
import com.example.demo.exception.IlligalActionException;
import com.example.demo.form.ItemForm;
import com.example.demo.form.CustomerForm;
import com.example.demo.service.BookService;

import lombok.RequiredArgsConstructor;

//レスポンスにHTMLではなく、JSONなどのデータを返すものを「Webサービス」といいますが
//私がまだ勉強不足なもので、JSONデータの送受を解説ができないので、今回はアイテムをひと種類ずつ追加する、という仕様にしました。
@Controller
@RequiredArgsConstructor
@RequestMapping("/kadai/bookController")
public class BookController {

	private final BookService bookService;

	@GetMapping
	public String showTimeSale(Model model,HttpSession session) throws IlligalActionException {
		//sessionで保持していた、カートにある商品情報を取り出す。Optional.ofNullableメソッドでNull安全性を付与することで、
		//仮にitemsInCartがnullだったとしても、NullPointerExceptionにはならない。
		@SuppressWarnings("unchecked")
		Optional<List<Item>> cart = Optional.ofNullable((List<Item>) session.getAttribute("cart"));
//		すべての書籍の在庫情報を取得
		List<BookStock> bookStocks = bookService.getAllBookStocks(cart);
//		カートに入れる商品情報を入力するフォームを作成
		ItemForm cartForm = new ItemForm();
		
		model.addAttribute("bookStocks", bookStocks);
		model.addAttribute("cartForm", cartForm);
		return "time-sale";
	}

	@PostMapping("add")
	public String addItem(ItemForm cartForm,HttpSession session,
			RedirectAttributes attributes) {
		//値の正当性を判断。isValidateメソッドは独自に作成したItemFormクラスのインスタンスメソッド。
		if (!cartForm.isValidate()) {
			attributes.addFlashAttribute("error_message", "1以上の数を入力してください");
			return "redirect:/kadai/bookController";
		}

		//sessionで保持していた、カートにある商品情報を取り出す。Optional.ofNullableメソッドでNull安全性を付与することで、
		//仮にitemsInCartがnullだったとしても、NullPointerExceptionにはならない。
		@SuppressWarnings("unchecked")
		Optional<List<Item>> cart = Optional.ofNullable((List<Item>) session.getAttribute("cart"));

		try {
//			カートに商品を追加し、追加した後の最新版のカート情報をupdatedItemsInCartオブジェクトに代入する。
			List<Item> updatedCart = bookService.addItem(cartForm, cart);
//			セッション情報を更新
			session.setAttribute("cart", updatedCart);
//			リダイレクト後にトップページ上で表示するメッセージを設定
			attributes.addFlashAttribute("message", "カートに商品を追加しました");
//			在庫数以上の冊数を追加しようとした時、IlligalActionExceptionがaddItemメソッドから放出される
		} catch (IlligalActionException e) {
			attributes.addFlashAttribute("error_message", e.getMessage());
		}

		return "redirect:/kadai/bookController";
	}

	@GetMapping("order")
	public String showOrderForm(CustomerForm customerForm, Model model, HttpSession session) {
		//sessionで保持していた、カートにある商品情報を取り出す。Optional.ofNullableメソッドでNull安全性を付与することで、
		//仮にitemsInCartがnullだったとしても、NullPointerExceptionにはならない。
		@SuppressWarnings("unchecked")
		Optional<List<Item>> cartNullable = Optional.ofNullable((List<Item>) session.getAttribute("cart"));
//		カートに商品が一つもなければ空のリストを生成（NullPointerException対策）
//		カートの商品 or 空のリストがitemsInCartに代入される
		List<Item> cart=cartNullable.orElse(new ArrayList<Item>());
		
		Map<Item,Integer> itemsMap=new HashMap<Item,Integer>();
//		itemsMap.
		
		
		model.addAttribute("cart",cart);
		model.addAttribute("customerForm",customerForm);
		return "order";
	}
}
