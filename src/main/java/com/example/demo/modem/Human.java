package com.example.demo.modem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Human {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany (mappedBy = "human")
    Set<ToDoItem> toDoItems = new HashSet<>();

    public Set<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public void addToDoItem(ToDoItem toDoItem) {

        toDoItems.add(toDoItem);

    }
}
