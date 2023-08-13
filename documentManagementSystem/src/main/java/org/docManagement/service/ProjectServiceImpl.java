package org.docManagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.docManagement.Model.Project;
import org.docManagement.Model.Members;
import org.docManagement.repository.ProjectMapEntryRepository;
import org.docManagement.repository.ProjectRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	public ProjectRepository projectRepository;
	
	@Autowired
	public ProjectMapEntryRepository projectMapEntryRepository;
	
	@Autowired
	public ILoginService IloginService;

	public boolean hasRole() {
		UserRepresentation user=IloginService.getCurrentUser();
		Keycloak keycloak=IloginService.keyCloakBuilder();
		RealmResource realmResource=keycloak.realm("emplEngagement");
		List<RoleRepresentation> groups=realmResource.users().get(user.getId()).roles().realmLevel().listAll();
		List<String> roles = groups.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
		if(roles.contains("Manager") || roles.contains("realm-admin"))
			return true;
		else 
			return false;
	}
	
	@Override
	public String createProject(String name,Members members) {
		// TODO Auto-generated method stub
		Project pro=new Project();
		List<Members> member=pro.getMembers();
		try {
		if(hasRole()) {
			if(findProjectByName(name)==null)
			{
		    pro.setName(name);
			member.add(members);
			projectMapEntryRepository.save(members);
			pro.setMembers(member);
			projectRepository.save(pro);
		   }
		    else{
			Project project1=findProjectByName(name);
			List<Members> list=project1.getMembers();
			projectMapEntryRepository.save(members);
			list.add(members);
			project1.setMembers(list);
			projectRepository.save(project1);
		      }
			return "Successfully Created";
		}else {
			return "Unauthorized";
		}
		}catch(Exception e) {
			return e.toString();
		}
	}

	@Override
	public String updateProject(String old,String newName) {
		// TODO Auto-generated method stub
		try {
		if(hasRole())
		{
			if(findProjectByName(old)!=null)
			{
				Project pro=findProjectByName(old);
				pro.setName(newName);
				projectRepository.save(pro);
			}
			
			return "Successfully Updated";
		}
		else
			return "unauthorized";
		}catch(Exception e) {
		return e.toString();
		}
	}

	@Override
	public String deleteProject(String name) {
		// TODO Auto-generated method stub
		try {
		Project project=projectRepository.findByName(name);
		if(project!=null && hasRole())
		{
			projectRepository.delete(project);
			return "Delete Successful";
		}
		else 
		return "Unauthorized";
		}
		catch(Exception e) {
			return ""+e;
		}
	}

	@Override
	public Project findProjectByName(String name) {
		// TODO Auto-generated method stub
		Project project=projectRepository.findByName(name);
		if(project!=null && hasRole())
		return projectRepository.findByName(name);
		else 
			return null;
	}

	@Override
	public List<Project> listAllProjects() {
		// TODO Auto-generated method stub
		if(hasRole())
		return projectRepository.findAll();
		else
			return null;
	}

}
