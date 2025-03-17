package com.tus.musicapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Ensure index is mapped to 'Home'
@Controller
public class HomeController {
	@GetMapping("/")
	public String home() {
	    return "forward:/index.html";  
	}
}
