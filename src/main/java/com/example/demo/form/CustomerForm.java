package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerForm {

	@NotBlank(message="入力してください")
	private String name;
	
	@NotBlank(message="入力してください")
	@Pattern(regexp="^0[34]0-[0-9]{4}-[0-9]{4}$",
	message="電話番号は030または040から始まり、「3桁-4桁-4桁」の形式で入力してください")
	private String tel;
	
	@NotBlank(message="入力してください")
	@Pattern(regexp="^[0-9]{3}-[0-9]{4}$",
	message="郵便番号は「3桁-4桁」の形式で入力してください")
	private String postNum;
	
	@NotBlank(message="入力してください")
	@Pattern(regexp = "^[\\p{IsHan}]{2,3}[都|道|府|県]"
			+ "[\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}]{1,7}[市|区|町|村].+$"
	,message="住所を正しく入力してください")
	private String address;
}
