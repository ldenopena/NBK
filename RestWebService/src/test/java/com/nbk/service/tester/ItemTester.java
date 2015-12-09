package com.nbk.service.tester;

import org.springframework.web.client.RestTemplate;

import com.nbk.service.model.Item;

public class ItemTester {

	public static final String baseURL = "http://10.20.10.19:8080/RestWebService";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ItemTester tester = new ItemTester();
		
		System.out.println(tester.getItem("sampleItem001").toString());
		System.out.println(tester.postItem(new Item("sampleItem002", "TestPost", 1, 20.00)));
		
	}

	private Item getItem(String code){
		String urlGet = baseURL + "/get/";
		
		RestTemplate template = new RestTemplate();
		Item item = template.getForObject(urlGet + code, Item.class);
		
		return item;
	}
	
	private String postItem(Item item){
		String urlPost = baseURL + "/addItem";
		
		RestTemplate template = new RestTemplate();
		String response = template.postForObject(urlPost, item, String.class);
		
		return response;
	}
}
