package com.nortal.treasurehunt.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MemberController {
  @GetMapping("/register")
  public ResponseEntity<Object> register() {
    return ResponseEntity.ok().build();
  }
}
