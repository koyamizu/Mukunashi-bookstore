package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookStock;
import com.example.demo.entity.Item;

@Mapper
public interface BookMapper {
	
	List<BookStock> selectAll();
	
	BookStock select(Item Cart);
	
	String selectTitle(Book book);
	
	void update(BookStock bookStock);
}
