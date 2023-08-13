package org.docManagement.controller;

import java.util.List;

import org.docManagement.Model.Project;
import org.docManagement.Model.Members;
import org.docManagement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	public ProjectService projectService;
	
	@PostMapping("/create")
	public String createProject(@RequestParam String name,@RequestBody Members members) {
		return projectService.createProject(name,members);
	}
	
	@PostMapping("/update")
	public String updateProject(@RequestParam String oldName,@RequestParam String newName) {
		return projectService.updateProject(oldName,newName);
	}
	
	@DeleteMapping("/delete")
	public String deleteProject(@RequestParam String name) {
		return projectService.deleteProject(name);
	}
	
	@GetMapping("/getProject")
	public Project findByName(@RequestParam String name) {
		return projectService.findProjectByName(name);
	}
	
	@GetMapping("/getAll")
	public List<Project> listOfAll(){
		return projectService.listAllProjects();
	}
}
