package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookStock;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Customer;
import com.example.demo.entity.History;
import com.example.demo.entity.Item;
import com.example.demo.entity.Order;
import com.example.demo.exception.IlligalActionException;
import com.example.demo.exception.NoHistoryException;
import com.example.demo.form.CustomerForm;
import com.example.demo.form.ItemForm;
import com.example.demo.helper.CustomerConverter;
import com.example.demo.helper.ItemConverter;
import com.example.demo.service.BookService;

import lombok.RequiredArgsConstructor;

//レスポンスにHTMLではなく、JSONなどのデータを返すものを「Webサービス」といいますが
//私がまだ勉強不足なもので、JSONデータの送受を解説ができないので、今回はアイテムをひと種類ずつ追加する、という仕様にしました。
@Controller
//finalが付けられたフィールドのみを引数とするコンストラクタを自動的に生成する
@RequiredArgsConstructor
//共通パス
@RequestMapping("/mukunashi-bookstore")
public class BookController {

	private final BookService bookService;

	//	エンドポイント
	@GetMapping
	public String showTopPage(Model model, HttpSession session, RedirectAttributes attributes) {
		//		sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart) session.getAttribute("cart");
		try {
			//			すべての書籍の在庫情報を取得
			List<BookStock> bookStocks = bookService.getAllBookStocks(cart);
			//			modelに格納
			model.addAttribute("bookStocks", bookStocks);
			//			在庫の取得に失敗したとき、bookSerivce.getAllBookStocks()から例外（IlligalActionException）放出
		} catch (IlligalActionException e) {
			attributes.addFlashAttribute("error_message", e.getMessage());
			return "redirect:/mukunashi-bookstore";
		}
		return "top";
	}

	@PostMapping("add")
	public String addItem(@Validated ItemForm itemForm,BindingResult bindingResult, HttpSession session,
			RedirectAttributes attributes) {
		
		if (bindingResult.hasErrors()) {
			attributes.addFlashAttribute("error_message", bindingResult.getFieldError("num").getDefaultMessage());
			return "redirect:/mukunashi-bookstore";
		}

		//		sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart) session.getAttribute("cart");

		Item item = ItemConverter.convertItem(itemForm);
		try {
			//			カートに商品を追加し、追加した後の最新版のカート情報をupdatedCartオブジェクトに格納する。
			Cart updatedCart = bookService.addItem(item, cart);
			//			セッション情報を更新
			session.setAttribute("cart", updatedCart);
			//			リダイレクト後にトップページ上で表示するメッセージを設定
			attributes.addFlashAttribute("message", "カートに商品を追加しました");
			//			在庫数以上の冊数を追加しようとした時、IlligalActionExceptionがaddItemメソッドから放出される
		} catch (IlligalActionException e) {
			attributes.addFlashAttribute("error_message", e.getMessage());
		}
		//		http://localhost:8080/mukunashi-bookstoreにリダイレクト
		return "redirect:/mukunashi-bookstore";
	}

	@GetMapping("cart")
	public String showCart(CustomerForm customerForm, Model model, HttpSession session) {
		//		sessionで保持していた、カートにある商品情報を取り出す。Null安全性を保証。
		Optional<Cart> cart = Optional.ofNullable((Cart) session.getAttribute("cart"));
		//		空のCart型のインスタンスを生成
		Cart dummy = new Cart(new HashMap<Book, Integer>());
		//		cartがnullだったとき、dummyをmodelに渡す
		model.addAttribute("cart", cart.orElse(dummy).getItems());
		model.addAttribute("customerForm", customerForm);
		return "cart";
	}

	@PostMapping("order")
	//	入力チェックをするフォームには@Validatedを付与
	public String order(@Validated CustomerForm customerForm, BindingResult bindingResult,
			RedirectAttributes attributes, HttpSession session, Model model) {
		//		入力にエラーがあったとき
		if (bindingResult.hasErrors()) {
			//sessionで保持していた、カートにある商品情報を取り出す。
			Optional<Cart> cart = Optional.ofNullable((Cart) session.getAttribute("cart"));

			Cart dummy = new Cart(new HashMap<Book, Integer>());
			model.addAttribute("cart", cart.orElse(dummy).getItems());
			model.addAttribute("customerForm", customerForm);
			return "cart";
		}

		//sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart) session.getAttribute("cart");
		Customer customer = CustomerConverter.convertCustomer(customerForm);

		Order order = new Order(customer, cart);
		try {
			Integer orderId = bookService.executeOrder(order);
			//			sessionで保持していたカートの情報を削除
			session.removeAttribute("cart");
			//			リダイレクト後にトップページ上で表示するメッセージを設定
			attributes.addFlashAttribute("message", "ご注文ありがとうございます。注文番号は" + orderId + "です。");
			//			在庫がないとき、bookSerivce.executeOrder()から例外（IlligalActionException）放出
		} catch (IlligalActionException e) {
			attributes.addFlashAttribute("error_message", e.getMessage());
		}

		return "redirect:/mukunashi-bookstore";
	}

	@GetMapping("history")
	//	「http://localhost:8080/history?order-id=〇〇」の〇〇の部分（GETのリクエストパラメータ）を取得
	public String showHistoryTop(@RequestParam("order-id") Integer orderId, Model model,
			RedirectAttributes attributes) {
		try {
			History history = bookService.getHistoryByOrderId(orderId);
			model.addAttribute("orderId", orderId);
			model.addAttribute("items", history.getItems());
			model.addAttribute("customer", history.getCustomer());
			model.addAttribute("orderDateTime", history.getOrderDateTime());
		} catch (NoHistoryException e) {
			attributes.addFlashAttribute("error_message", e.getMessage());
			return "redirect:/mukunashi-bookstore";
		}
		return "history";
	}

	@PostMapping("delete/{order-id}")
	//	URLパスの中にある変数を取得
	public String deleteOrder(@PathVariable("order-id") Integer orderId, RedirectAttributes attributes) {
		bookService.deleteOrderByOrderId(orderId);
		attributes.addFlashAttribute("message", "注文番号：" + orderId + "の注文が取り消されました。");
		return "redirect:/mukunashi-bookstore";
	}
}
