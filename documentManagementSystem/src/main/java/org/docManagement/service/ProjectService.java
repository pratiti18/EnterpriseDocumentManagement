package org.docManagement.service;

import java.util.List;

import org.docManagement.Model.Project;
import org.docManagement.Model.Members;

public interface ProjectService {
	
	public String createProject(String name,Members members);
	public String updateProject(String oldName,String newName);
	public String deleteProject(String name);
	public Project findProjectByName(String name);
	public List<Project> listAllProjects();

}
