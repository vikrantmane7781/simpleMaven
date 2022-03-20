package com.example.AllSpringerBoot.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.AllSpringerBoot.dao.ContactRepository;
import com.example.AllSpringerBoot.dao.MainRepository;
import com.example.AllSpringerBoot.entity.Contact;
import com.example.AllSpringerBoot.entity.User;

@RestController
public class SearchController {
	
	@Autowired
	private ContactRepository cpr;
	
	@Autowired
	private MainRepository mry;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?>search(@PathVariable("query")String query,Principal pri){
		
		User user=this.mry.getUserByName(pri.getName());
		List<Contact>contacts=this.cpr.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}
