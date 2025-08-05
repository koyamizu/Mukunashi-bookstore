package com.example.demo.service.impl;

import com.example.demo.repository.BookMapper;
import com.example.demo.service.BookService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookMapper bookMapper;

}
