package com.example.demo.entity;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

	private Map<Book, Integer> items = new HashMap<Book, Integer>();

	public void add(Book book,Integer num) {

		if (items.containsKey(book)) {
			Integer presentNum = items.get(book);
			Integer updatedNum = presentNum + num;
			items.replace(book, updatedNum);
		} else {
			items.put(book, num);
		}
	}
}
