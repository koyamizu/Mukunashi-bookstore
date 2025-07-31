package com.example.demo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemForm {

	private String isbn;
	//	@Min(value=1, message = "注文数は1以上の数を入力してください")
	private int num;
	private int price;

	public Boolean isValidate(){
		return this.num > 0;
	}
}
