package com.example.demo.controller;

import com.example.demo.modem.Human;
import com.example.demo.modem.ToDoItem;
import com.example.demo.repo.ToDOItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/People/ToDoItem")
public class ToDoItemController {
    @Autowired
    ToDOItemRepo toDOItemRepo;


    @GetMapping
    public ResponseEntity<List<ToDoItem>> findAll() throws IOException {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/ToDoItem");
        String url = "http://localhost:5000/todo";

        RestTemplate restTemplate = new RestTemplate();
        List<ToDoItem> toDoItems = new ArrayList<>();
        ResponseEntity<List<ToDoItem>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ToDoItem>>() {
                    });

             toDoItems = responseEntity.getBody();

            toDOItemRepo.saveAll(toDoItems);

        } catch(HttpStatusCodeException e) {
            return ResponseEntity.status(e.getRawStatusCode()).headers(responseHeaders)
                    .body(toDOItemRepo.findAll());
        }

        if(responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(responseHeaders).body(toDOItemRepo.findAll());
        }

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(toDOItemRepo.findAll());
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ToDoItem save(@RequestBody ToDoItem toDoItem) {

        String url = "http://localhost:5000/todo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location",
                "/People/ToDoItem");
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ToDoItem> httpEntity = new HttpEntity<>(toDoItem, headers);

        ToDoItem responseItem = restTemplate.postForObject(url, httpEntity, ToDoItem.class);

        return toDOItemRepo.save(responseItem);
    }

    @GetMapping("/{toDoItemID}")
    public ResponseEntity<Optional<ToDoItem>> connectHumanToToDoList(@PathVariable Long toDoItemID)
    {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/ToDoItem/"+toDoItemID);

        if(toDOItemRepo.findById(toDoItemID).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(responseHeaders).body(toDOItemRepo.findById(toDoItemID));



        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(toDOItemRepo.findById(toDoItemID));
    }
    @PutMapping("/{toDoItemID}")
    public ResponseEntity<Optional<ToDoItem>> updateTheItemBYID(@PathVariable Long toDoItemID, @RequestBody ToDoItem toDoItem)
    {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/ToDoItem/"+toDoItemID);

        String url = "http://localhost:5000/todo/"+toDoItemID;
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ToDoItem> entity = new HttpEntity<ToDoItem>(toDoItem);
        restTemplate.exchange(url, HttpMethod.PUT, entity, ToDoItem.class);


        ToDoItem item = toDOItemRepo.findById(toDoItemID).get();
        item.setTitle(toDoItem.getTitle());
        item.setDone(toDoItem.isDone());
        toDOItemRepo.save(item);

        return  ResponseEntity.status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(toDOItemRepo.findById(toDoItemID));

    }
    @DeleteMapping("/{toDoItemID}")
    public ResponseEntity<String> deleteByID(@PathVariable Long toDoItemID)
    {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/ToDoItem/"+toDoItemID);

        if(toDOItemRepo.findById(toDoItemID).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body("{\n" +'"'+
                            "status"+'"'+":"+'"'+"not exist"+'"'+"\n}");

        String url = "http://localhost:5000/todo/"+toDoItemID;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(url);
        toDOItemRepo.deleteById(toDoItemID);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("{\n" +'"'+
                        "status"+'"'+": "+'"'+"deleted"+'"'+"\n}");
    }



}
