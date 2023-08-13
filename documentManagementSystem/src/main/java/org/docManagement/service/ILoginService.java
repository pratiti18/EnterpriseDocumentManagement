package org.docManagement.service;

import java.util.List;

import org.apache.http.HttpRequest;
import org.docManagement.Model.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.servlet.http.HttpServletRequest;
public interface ILoginService {
	
	public String signUp(User user);
	public String signIn(String username,String password);
	public String assignRoles(String username,String role);
	public String createRoles(String id,String role);
	public List<UserRepresentation> listUsers(); 
public List<RoleRepresentation> allRoles();
public UserRepresentation getCurrentUser();
public Keycloak keyCloakBuilder();
}
