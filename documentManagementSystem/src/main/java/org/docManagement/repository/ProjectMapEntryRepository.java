package org.docManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.docManagement.Model.Members;

public interface ProjectMapEntryRepository extends JpaRepository<Members, Long>{

}
