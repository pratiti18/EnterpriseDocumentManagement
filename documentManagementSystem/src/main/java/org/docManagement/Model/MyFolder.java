package org.docManagement.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Component
@Scope("protocol")
@Table(name="folder")
public class MyFolder {

	@Id
	private String id;
	
	private String name;
	 private GregorianCalendar issueDate;
	 private GregorianCalendar modificationDate;
	 private String parentId;
	 
	@ElementCollection
	private List<String> role=new ArrayList<>();
	
	public MyFolder() {
		super();
	}
	public MyFolder(String id, String name, List<Doc> documentList, GregorianCalendar issueDate,
			GregorianCalendar modificationDate,String parentId,List<String> role) {
		super();
		this.id = id;
		this.name = name;
		this.issueDate = issueDate;
		this.modificationDate = modificationDate;
		this.parentId=parentId;
		this.role=role;
		
	}
	
	public List<String> getRole() {
		return role;
	}
	public void setRole(List<String> role) {
		this.role = role;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	
	public GregorianCalendar getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(GregorianCalendar issueDate) {
		this.issueDate = issueDate;
	}
	public GregorianCalendar getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(GregorianCalendar gregorianCalendar) {
		this.modificationDate = gregorianCalendar;
	}
	 
	 
	
	}


