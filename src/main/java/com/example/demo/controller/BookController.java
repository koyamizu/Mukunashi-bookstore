package com.example.demo.controller;

import java.util.List;

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

@Controller
//finalが付けられたフィールドのみを引数とするコンストラクタを自動的に生成する
@RequiredArgsConstructor
//共通パス
@RequestMapping("/mukunashi-bookstore")
public class BookController {

	private final BookService bookService;

	//	エンドポイント
	@GetMapping
	public String showTopPage(Model model, HttpSession session, RedirectAttributes attributes)
			throws IlligalActionException {
		//		sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart) session.getAttribute("cart");
		//			すべての書籍の在庫情報を取得
		List<BookStock> bookStocks = bookService.cartの情報をもとに、全ての書籍を取得するメソッド;
		//			modelに格納
		model.addAttribute("bookStocks", bookStocks);
		return "top";
	}

	@PostMapping("add")
	public String addItem(ItemForm itemForm, HttpSession session,
			RedirectAttributes attributes) throws IlligalActionException {
		//		sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart) session.getAttribute("cart");

		Item item = ItemConverter.convertItem(itemForm);
		//			カートに商品を追加し、追加した後の最新版のカート情報をupdatedCartオブジェクトに格納する。
		Cart updatedCart = bookService.cartとitemをもとに、商品を追加するメソッド;
		//			セッション情報を更新
		session.setAttribute("cart", updatedCart);
		//			リダイレクト後にトップページ上で表示するメッセージを設定
		attributes.addFlashAttribute("message", "カートに商品を追加しました");
		//			在庫数以上の冊数を追加しようとした時、IlligalActionExceptionがaddItemメソッドから放出される

		//		http://localhost:8080/mukunashi-bookstoreにリダイレクト
		return "redirect:/mukunashi-bookstore";
	}

	@GetMapping("cart")
	public String showCart(@Validated CustomerForm customerForm,BindingResult bindingResult,
			Model model, HttpSession session) throws IlligalActionException {
		
		if(bindingResult.hasErrors()) {
			Cart cart = (Cart) session.getAttribute("cart");
			List<BookStock> bookStocks = bookService.cartの情報をもとに、全ての書籍を取得するメソッド;
			model.addAttribute("bookStocks", bookStocks);
			return "top";
		}
		//		sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart) session.getAttribute("cart");
		model.addAttribute("cart", cart);
		model.addAttribute("customerForm", customerForm);
		return "cart";
	}

	@PostMapping("order")
	//	入力チェックをするフォームには@Validatedを付与
	public String order(@Validated CustomerForm customerForm, BindingResult bindingResult,
			RedirectAttributes attributes, HttpSession session, Model model) throws IlligalActionException {
		//		入力にエラーがあったとき
		if (bindingResult.hasErrors()) {
			Cart cart = (Cart) session.getAttribute("cart");
			model.addAttribute("cart", cart);
			model.addAttribute("customerForm", customerForm);
			return "cart";
		}

		//sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart) session.getAttribute("cart");
		Customer customer = CustomerConverter.convertCustomer(customerForm);

		Order order = new Order(customer, cart);
		Integer orderId = bookService.orderをもとに、注文を実行するメソッド;
		//			sessionで保持していたカートの情報を削除
		session.removeAttribute("cart");
		//			リダイレクト後にトップページ上で表示するメッセージを設定
		attributes.addFlashAttribute("message", "ご注文ありがとうございます。注文番号は" + orderId + "です。");
		//			在庫がないとき、bookSerivce.executeOrder()から例外（IlligalActionException）放出

		return "redirect:/mukunashi-bookstore";
	}

	@GetMapping("history")
	//	「http://localhost:8080/history?order-id=〇〇」の〇〇の部分（GETのリクエストパラメータ）を取得
	public String showHistoryTop(@RequestParam("order-id") Integer orderId, Model model,
			RedirectAttributes attributes) throws NoHistoryException {
		History history = bookService.orderIdをもとに、注文履歴を取得するメソッド;
		model.addAttribute("orderId", orderId);
		model.addAttribute("items", history.getItems());
		model.addAttribute("customer", history.getCustomer());
		model.addAttribute("orderDateTime", history.getOrderDateTime());
		return "history";
	}

	@GetMapping("history/{order-id}/delete")
	//	URLパスの中にある変数を取得
	public String deleteOrder(@PathVariable("order-id") Integer orderId, RedirectAttributes attributes) {
		bookService.orderIdをもとに、注文を取り消すメソッド;
		attributes.addFlashAttribute("message", "注文番号：" + orderId + "の注文が取り消されました。");
		return "redirect:/mukunashi-bookstore";
	}
}
