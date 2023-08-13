package org.docManagement.service;


import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoAspects;
import org.alfresco.cmis.client.impl.AlfrescoAspectsImpl;
import org.alfresco.cmis.client.impl.AlfrescoAspectsUtils;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.impl.RepositoryServiceImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.jaxb.CmisAccessControlEntryType;
import org.apache.chemistry.opencmis.commons.impl.jaxb.RepositoryService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.docManagement.Model.User;
import org.hibernate.internal.build.AllowSysOut;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;



@Service
public class ILoginServiceImpl implements ILoginService{
	
	
/*The client secret written here, changes if you create a new Keycloak container. If due to any problems you are having to restart setting up
 * keycloak through cmd, then client secret will change. How to opt the client secret is mentioned clearly in the Documentation.
 * Also Note that 8081 is my port Number for running keycloak, yours maybe different.*/
	@Override
	public Keycloak keyCloakBuilder() {
		 Keycloak keycloak = KeycloakBuilder.builder()
                 .serverUrl("http://localhost:8081/")
                 .realm("emplEngagement")
                 .clientId("empl-rest-api")
                 .clientSecret("6GyhkRgv8pA43AQ7zpZne0ifxPoDLqO2")
                 .username("admin") 
                 .password("admin") 
                 .build();
		 return keycloak;
	}
	 
        @Override
	    public UserRepresentation getCurrentUser() {
        	try {
        		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Keycloak keycloak=keyCloakBuilder();
        		String username=authentication.getName();
        		 RealmResource realmResource = keycloak.realm("emplEngagement");
        		UserRepresentation user=realmResource.users().get(username).toRepresentation();
        		 return user;
        	}catch(Exception e) {
                	return null;
                }
                
}
	    
	
	/*In here I have created a User class in my model package, and the object i am passing in my signUp method is of the User class that
	 * I created. The inbuilt UserResource class could also have been used.
	 */
	@Override
	public String signUp(User user) {
		
		 Keycloak keycloak = keyCloakBuilder();
		 RealmResource realmResource = keycloak.realm("emplEngagement");
         UsersResource usersResource = realmResource.users();
   UserRepresentation newUser=new UserRepresentation();
       
		newUser.setUsername(user.getUserName());
		newUser.setEmail(user.getEmail());
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setEnabled(true);

		 CredentialRepresentation passwordCred = new CredentialRepresentation();
         passwordCred.setType(CredentialRepresentation.PASSWORD);
         passwordCred.setValue(user.getPassword());
         newUser.setCredentials(Collections.singletonList(passwordCred));
         
         try (Response response = usersResource.create(newUser)) {
             if(response.getStatus() == 201) {
               return "Created";
             } else {
               return ""+response.getStatus();
             }
           } catch (Exception e) {
        	   
             return "failed "+e;
           }
	}
	@Override
	public String signIn(String username, String password) {
		// TODO Auto-generated method stub
		try {
			AccessTokenResponse accessTokenResponse=authenticateUser(username, password);
			accessTokenResponse.setExpiresIn(3600);
			Keycloak keycloak=keyCloakBuilder();
			 RealmResource realmResource = keycloak.realm("emplEngagement");
			 
			 UserRepresentation user = realmResource.users().search(username).get(0);
			 RoleMappingResource roleMappingResource = realmResource.users()
		                .get(user.getId())
		                .roles();
			return "Successfully logged  in "+accessTokenResponse.getToken();
		}catch(Exception e)
		{
			return "Login failed";
		}
	
	}
	
	private AccessTokenResponse authenticateUser(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8081/")
                .realm("emplEngagement")
                .clientId("empl-rest-api")
                .clientSecret("6GyhkRgv8pA43AQ7zpZne0ifxPoDLqO2")
                .username(username)
                .password(password)
                .grantType("password")
                .build();

        return keycloak.tokenManager().getAccessToken();
    }

	@Override
	public String assignRoles(String username, String role) {
		try {
		Keycloak keycloak = keyCloakBuilder();
		 RealmResource realmResource = keycloak.realm("emplEngagement");
		 UserRepresentation user = realmResource.users().search(username).get(0);
	        

	        RoleMappingResource roleMappingResource = realmResource.users()
	                .get(user.getId())
	                .roles();

	        RoleRepresentation roleRepresentation = realmResource.roles().get(role).toRepresentation();
	        roleMappingResource.realmLevel().add(Collections.singletonList(roleRepresentation)); 
	        return "Role assigned";
		}
		catch(Exception e) {
			return "Assign Failed"+e;
		}
	}

	@Override
	public String createRoles(String id,String role) {
		// TODO Auto-generated method stub
		 HttpClient httpClient = HttpClients.createDefault();
	        String createGroupUrl = "http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/groups";
	        String adminUsername="admin";
	        String adminPassword="admin";
	        HttpPost httpPost = new HttpPost(createGroupUrl);

	        // Prepare the group JSON data
	        //String groupData = "{\"displayName\":\"" + roleName + "\",\"parentIds\":\"GROUP_ALFRESCO_ADMINISTRATORS\"}";
	        String requestBody = "{\"id\": \"" + id + "\",\"displayName\":\"" + role + "\"}";
	        
	        HttpEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
	        httpPost.setEntity(entity);
	        // Set Basic Authentication
	        String auth = adminUsername + ":" + adminPassword;
	        String authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes()); 
	        httpPost.setHeader("Authorization", authHeader);
	       
		Keycloak keycloak=keyCloakBuilder();
		
		try {
			HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
		RealmResource realmResource = keycloak.realm("emplEngagement");
		RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role);
        realmResource.roles().create(roleRepresentation);
        if (statusCode == 201) {
            return "Successful";
        } else {
            return "Failed, Status Code: "+statusCode;
        }
		}catch(Exception e) {
			return "Creation Failed "+e;
		}
	}

	@Override
	public List<UserRepresentation> listUsers() {
		Keycloak keycloak=keyCloakBuilder();
		RealmResource realmResource = keycloak.realm("emplEngagement");
		return realmResource.users().list();
		
	}

	@Override
	public List<RoleRepresentation> allRoles() {
		// TODO Auto-generated method stub
		Keycloak keycloak=keyCloakBuilder();
		RealmResource realmResource = keycloak.realm("emplEngagement");
		return realmResource.roles().list();
	}
	
	 private boolean hasRole(String role) {
	        // Get the Keycloak access token from the authentication context
//	        KeycloakPrincipal principal = (KeycloakPrincipal) AuthenticationUtil.getRunAsUser();
//	        if (principal != null) {
//	            AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
//	            if (accessToken != null) {
//	                // Check if the user has the specified role
//	                return accessToken.getRealmAccess().isUserInRole(role);
//	            }
//	        }
		 UserRepresentation user=getCurrentUser();
			Keycloak keycloak=keyCloakBuilder();
			 RealmResource realmResource = keycloak.realm("emplEngagement");
		 List<RoleRepresentation> roles = realmResource.users().get(user.getId()).roles().realmLevel().listAll();
		 if(role.contains(role))
			 return true;
		 else
	        return false;
	    }
}
