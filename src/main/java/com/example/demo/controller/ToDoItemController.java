package com.example.demo.controller;

import com.example.demo.modem.ToDoItem;
import com.example.demo.repo.ToDOItemRepo;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
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
    DockerClient dockerClient = DockerClientBuilder.getInstance().build();
    List<String> err = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<?>> findAll() throws IOException {

        err.clear();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/ToDoItem");
        err.add("Error ToDo service is not reachable");
        String url = "http://todolist:5000/todo";

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
        }catch (ResourceAccessException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(responseHeaders)
                    .body(err);
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
    public ResponseEntity<List<?>> save(@RequestBody ToDoItem toDoItem) {

        err.clear();
        err.add("Error ToDo service is not reachable");
        List<ToDoItem> toDoItemList = new ArrayList<>();
        String url = "http://todolist:5000/todo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location",
                "/People/ToDoItem");
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ToDoItem> httpEntity = new HttpEntity<>(toDoItem, headers);
        ToDoItem responseItem = new ToDoItem();
        try {
            responseItem = restTemplate.postForObject(url, httpEntity, ToDoItem.class);

        }catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers)
                    .body(err);
        }
        toDoItemList.add(responseItem);
        return ResponseEntity.ok()
                    .headers(headers)
                    .body(toDOItemRepo.saveAll(toDoItemList));
    }

    @GetMapping("/{toDoItemID}")
    public ResponseEntity<Optional<ToDoItem>> connectHumanToToDoList(@PathVariable Long toDoItemID)
    {
        err.clear();
        err.add("Error ToDo service is not reachable");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/ToDoItem/"+toDoItemID);

        if(toDOItemRepo.findById(toDoItemID).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(responseHeaders).body(toDOItemRepo.findById(toDoItemID));



        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(toDOItemRepo.findById(toDoItemID));
    }
    @PutMapping("/{toDoItemID}")
    public ResponseEntity<List<?>> updateTheItemBYID(@PathVariable Long toDoItemID, @RequestBody ToDoItem toDoItem)
    {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                "/People/ToDoItem/"+toDoItemID);
        err.clear();
        err.add("Error ToDo service is not reachable");
        String url = "http://todolist:5000/todo/"+toDoItemID;
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ToDoItem> entity = new HttpEntity<ToDoItem>(toDoItem);
        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, ToDoItem.class);
        }catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(responseHeaders)
                    .body(err);
        }



        ToDoItem item = toDOItemRepo.findById(toDoItemID).get();
        item.setTitle(toDoItem.getTitle());
        item.setDone(toDoItem.isDone());
        toDOItemRepo.save(item);
        List<ToDoItem> toDoItemList =new ArrayList<>();
        toDoItemList.add(toDOItemRepo.findById(toDoItemID).get());
        return  ResponseEntity.ok()
                .headers(responseHeaders)
                .body(toDoItemList);

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

        String url = "http://todolist:5000/todo/"+toDoItemID;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(url);
        toDOItemRepo.deleteById(toDoItemID);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("{\n" +'"'+
                        "status"+'"'+": "+'"'+"deleted"+'"'+"\n}");
    }



}
