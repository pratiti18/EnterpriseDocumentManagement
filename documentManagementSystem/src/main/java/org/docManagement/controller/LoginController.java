package org.docManagement.controller;

import org.apache.http.HttpRequest;
import org.docManagement.Model.User;
import org.docManagement.service.ILoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.keycloak.representations.idm.UserRepresentation;


@RestController
@RequestMapping("/sign")
public class LoginController {
	
	@Autowired
	public ILoginService IloginService;
	
	@PostMapping("/up")
	public String UserSignUp(@RequestBody User user) {
		return IloginService.signUp(user);
	}
	@GetMapping("/In")
	public String UserSignIn(@RequestParam String username,String password) {
		return IloginService.signIn(username, password);
	}
	@GetMapping("/currentUser")
    public String getUser() {
    return IloginService.getCurrentUser().getEmail();
    }
}
