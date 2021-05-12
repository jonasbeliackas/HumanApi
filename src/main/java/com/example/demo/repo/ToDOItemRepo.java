package com.example.demo.repo;

import com.example.demo.modem.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDOItemRepo extends JpaRepository<ToDoItem, Long> {

}
