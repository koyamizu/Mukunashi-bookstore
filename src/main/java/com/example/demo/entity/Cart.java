package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

	private List<Map<Book, Integer>> items = new ArrayList<Map<Book, Integer>>();

	public void add(Book book,Integer num) {

//		Book targetBook = item.getBook();

		Optional<Map<Book, Integer>> matched = items.stream().filter(i -> i.containsKey(book)).findFirst();

		if (matched.isPresent()) {
			Integer presentNum = matched.get().get(book);
			Integer updatedNum = presentNum + num;
			items.stream().filter(i -> i.containsKey(book))
					.findFirst().get().replace(book, updatedNum);
		} else {
			items.add(Map.of(book, num));
		}
	}
}
