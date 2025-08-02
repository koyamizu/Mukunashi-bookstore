package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.BookStock;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Item;
import com.example.demo.exception.IlligalActionException;
import com.example.demo.form.CustomerForm;
import com.example.demo.form.ItemForm;
import com.example.demo.helper.ItemConverter;
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
	public String showTopPage(Model model,HttpSession session) throws IlligalActionException {
		//sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart)session.getAttribute("cart");
//		すべての書籍の在庫情報を取得
		List<BookStock> bookStocks = bookService.getAllBookStocks(cart);		
		model.addAttribute("bookStocks", bookStocks);
		return "top";
	}

	@PostMapping("add")
	public String addItem(ItemForm itemForm,HttpSession session,
			RedirectAttributes attributes) {
		//値の正当性を判断。isValidateメソッドは独自に作成したItemFormクラスのインスタンスメソッド。
		if (!itemForm.isValidate()) {
			attributes.addFlashAttribute("error_message", "1以上の数を入力してください");
			return "redirect:/kadai/bookController";
		}

		//sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart)session.getAttribute("cart");

		Item item = ItemConverter.convertItem(itemForm);
		try {
//			カートに商品を追加し、追加した後の最新版のカート情報をupdatedItemsInCartオブジェクトに代入する。
			Cart updatedCart = bookService.addItem(item, cart);
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
		//sessionで保持していた、カートにある商品情報を取り出す。
		Cart cart = (Cart)session.getAttribute("cart");
		
		model.addAttribute("cart",cart.getItems());
		model.addAttribute("customerForm",customerForm);
		return "order";
	}
}
