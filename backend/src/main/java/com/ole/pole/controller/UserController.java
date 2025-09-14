package com.ole.pole.controller;

import com.ole.pole.service.PollManager;
import com.ole.pole.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private PollManager pollManager;

    @Autowired
    public UserController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        Long id = pollManager.createUser(user);
        user.setId(id);
        return user;
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return pollManager.getUser(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        pollManager.updateUser(user);
    }
}