package com.example.demo.form;

import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemForm {

	private String isbn;
	@Min(value = 1,message="1以上の数を入力してください")
	private Integer num;

}
