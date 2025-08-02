package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookStock;

@Mapper
public interface BookMapper {
	
	List<BookStock> selectAll();
	
	Book select(String isbn);
	
	Integer selectStockNum(String isbn);
	
	void update(BookStock bookStock);
}
