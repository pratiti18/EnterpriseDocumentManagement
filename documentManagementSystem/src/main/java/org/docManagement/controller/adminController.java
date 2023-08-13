package org.docManagement.controller;

import java.util.List;

import org.docManagement.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@RestController
@RequestMapping("/admin")
public class adminController {
	
	@Autowired
	public ILoginService IloginService;

	@PostMapping("/createRole")
	public String createRoles(@RequestParam String id,@RequestParam String name) {
		return IloginService.createRoles(id,name);
	}
	
	@GetMapping("/list")
	public List<UserRepresentation> getUsers(){
		return IloginService.listUsers();
	}
	
	@GetMapping("/roles")
	public List<RoleRepresentation> getRoles(){
		return IloginService.allRoles();
	}
	
	@PutMapping("/roleAssign")
	public String assignRoles(@RequestParam String name,@RequestParam String role){
		return IloginService.assignRoles(name, role);
	}
}
