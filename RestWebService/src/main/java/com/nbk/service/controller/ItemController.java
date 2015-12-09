package com.nbk.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nbk.service.model.Item;


@RestController
public class ItemController {

	/*
	 * Get Method
	 */
	@RequestMapping(value = "/get/{code}", method = RequestMethod.GET, headers = "Accept=application/json")
	public Item getItem(@PathVariable("code") String code){
		
		// Database transaction here
		Item item = new Item(code, "NameFor" + code, 999, 200.00); // dummy code
		
		return item;
	}
	
	/*
	 * Post Method
	 */
	@ResponseBody
	@RequestMapping(value = "/addItem", method = RequestMethod.POST, headers = "Accept=application/json")
	public Item addItem(@RequestBody Item item){

		// Database transaction here
		
		return item;
	}
}
