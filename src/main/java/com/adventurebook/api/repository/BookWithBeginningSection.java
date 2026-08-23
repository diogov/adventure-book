package com.adventurebook.api.repository;

import com.adventurebook.api.model.Book;

public record BookWithBeginningSection(Book book, Integer beginningSectionNumber) {}
