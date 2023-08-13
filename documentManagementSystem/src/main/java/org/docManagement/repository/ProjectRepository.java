package org.docManagement.repository;

import org.docManagement.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long>{

	public Project findByName(String name);
}
