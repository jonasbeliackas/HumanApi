package com.example.demo.controller;


import com.example.demo.modem.Human;
import com.example.demo.modem.ToDoItem;
import com.example.demo.repo.HumanRepo;
import com.example.demo.repo.ToDOItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/People")
public class HumanController {

    @Autowired
    HumanRepo humanRepo;
    @Autowired
    ToDOItemRepo toDOItemRepo;


    @PostMapping("/hole")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Human> humanWithToDoListsave(@RequestBody Human human) throws UnknownHostException {


        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/hole");

        String url = "http://todolist:5000/todo";

        Set<ToDoItem> toDoItems = human.getToDoItems();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ToDoItem> httpEntity;
        ToDoItem responseItem;
        Human savedHuman = new Human();
        savedHuman.setName(human.getName());
        for(ToDoItem toDoItem : toDoItems)
        {
            httpEntity = new HttpEntity<>(toDoItem, responseHeaders);

            responseItem = restTemplate.postForObject(url, httpEntity, ToDoItem.class);

            savedHuman.addToDoItem(responseItem);
            humanRepo.save(savedHuman);
            responseItem.setHumanByID(savedHuman);
            toDOItemRepo.save(responseItem);

        }

        return  ResponseEntity.status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(savedHuman);
    }

    @GetMapping
    public ResponseEntity<List<Human>> findAll() throws IOException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/");
        String url = "http://todolist:5000/todo";
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ToDoItem>>() {
                    });

        }catch(HttpStatusCodeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(responseHeaders).body(humanRepo.findAll());
        }
        catch (ResourceAccessException e)
        {
            toDOItemRepo.deleteAll();
            for(Human human : humanRepo.findAll() )
                human.getToDoItems().clear();
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(responseHeaders).body(humanRepo.findAll());
        }
        if(humanRepo.findAll().isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(responseHeaders).body(humanRepo.findAll());


        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(humanRepo.findAll());
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Human save(@RequestBody Human human) {
        return humanRepo.save(human);
    }
    @PutMapping("/{pipID}/ToDoItem/{todoID}")
    public Human connectHumanToToDoList(@PathVariable Long pipID , @PathVariable Long todoID)
    {
        Human human = humanRepo.findById(pipID).get();
        ToDoItem toDoItem = toDOItemRepo.findById(todoID).get();
        human.addToDoItem(toDoItem);
        toDoItem.setHumanByID(human);
        return humanRepo.save(human);
    }
    @GetMapping("/{pipID}")
    public ResponseEntity<Optional<Human>> getOnePip(@PathVariable Long pipID)
    {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/"+pipID);


        if(humanRepo.findById(pipID).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(responseHeaders).body(humanRepo.findById(pipID));


        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(humanRepo.findById(pipID));
    }
    @PutMapping("/{pipID}")
    public ResponseEntity<Optional<Human>> updateOneMan(@PathVariable Long pipID ,  @RequestBody Human human)
    {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/"+pipID);
        Human item = new Human();
        if(humanRepo.findById(pipID).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body(humanRepo.findById(pipID));


        item = humanRepo.findById(pipID).get();
        item.setName(human.getName());
        humanRepo.save(item);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(humanRepo.findById(pipID));

    }
    @DeleteMapping("/{pipID}")
    public ResponseEntity<String> updateOneMan(@PathVariable Long pipID )
    {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/"+pipID);
        if(humanRepo.findById(pipID).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body("{\n" +'"'+
                            "status"+'"'+": "+'"'+"not exist"+'"'+"\n}");


        humanRepo.deleteById(pipID);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("{\n" +'"'+
                        "status"+'"'+": "+'"'+"deleted"+'"'+"\n}");

    }

}
