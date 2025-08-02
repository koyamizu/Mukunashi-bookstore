package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

//	private Map<Book,Integer> item;
	private Book book;
	private Integer num;
	
	public Boolean isValidate(int stockNum) {
		return this.num <= stockNum;
	}
	
//	public void setBookInformation(Book book) {
//		this.book.setPrice(book.getPrice());
//		this.book.setPublished(book.getPublished());
//		this.book.setPublisher(book.getPublisher());
//		this.book.setTitle(book.getTitle());
//	}
}
