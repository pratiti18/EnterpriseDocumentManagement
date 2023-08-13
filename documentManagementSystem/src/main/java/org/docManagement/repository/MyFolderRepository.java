package org.docManagement.repository;

import java.lang.annotation.Native;
import java.util.List;

import org.docManagement.Model.MyFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MyFolderRepository extends JpaRepository<MyFolder, String> {
	
	@Query("SELECT f FROM MyFolder f WHERE f.name = :foldname")
	public MyFolder findByName(@Param("foldname") String foldname);
	 
	@Query("SELECT f FROM MyFolder f JOIN f.role r WHERE r IN :roles")
    public List<MyFolder> findByRole(@Param("roles") List<String> roles);


}
