package com.example.demo.modem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ToDoItem {
    @Id
    private long    id;
    private String  title;
    private boolean done;
    @JsonIgnore
    @ManyToMany
    @JoinColumn(name="human_id")
    private Set<Human> human = new HashSet<Human>();

    public Set<Human> getHuman() {
        return human;
    }

    public void setHuman(Set<Human> human) {
            this.human = human;
    }

    public ToDoItem(long id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public ToDoItem() {

    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


    public void addJsonData(StringBuffer content) {

    }

    public void setHumanByID(Human human) {
        this.human.add(human);
    }
}
