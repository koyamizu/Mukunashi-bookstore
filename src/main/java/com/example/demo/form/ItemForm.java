package com.example.demo.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemForm {

	private String isbn;
//	private String title;
	private int num;
//	private int price;

	public Boolean isValidate() {
		return this.num > 0;
	}

}
