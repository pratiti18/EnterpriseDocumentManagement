package org.docManagement.repository;

import java.util.List;

import org.docManagement.Model.Doc;
import org.docManagement.Model.MyFolder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Doc, String>{
	
	@Query("SELECT f FROM Doc f WHERE f.name = :docName")
	public List<Doc> findDocByName(@Param("docName") String docName);
	
	@Query("SELECT f FROM Doc f WHERE f.folder= :fold")
public List<Doc> listOfDocsInAFolder(@Param("fold") MyFolder fold);
	
	@Query("SELECT f FROM Doc f WHERE f.folder IN :docIdList")
	public List<Doc> listOfDocs(@Param("docIdList") List<MyFolder> docIdList);
}
