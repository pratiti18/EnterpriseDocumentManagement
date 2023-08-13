package org.docManagement.Model;



import java.util.GregorianCalendar;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.hibernate.annotations.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;




@Entity
@Component
@Scope("protocol")
@Table
public class Doc {
	
@Id
private String id;
 private String name;
 
 @ManyToOne
 private MyFolder folder;


/*Keeping a blank space in my content string, otherwise while creating a document, the content stream would through null exception.*/
private String content=" ";
 private GregorianCalendar issueDate;
 private GregorianCalendar modificationDate;
 
 
public Doc() {
	super();
}


public Doc(String id,String name, String content, GregorianCalendar issueDate, GregorianCalendar modificationDate,MyFolder folder) {
	super();
	this.name = name;
	this.content = content;
	this.issueDate = issueDate;
	this.modificationDate = modificationDate;
	this.id=id;
	this.folder=folder;
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


public String getContent() {
	return content;
}


public void setContent(String content) {
	this.content = content;
}


public GregorianCalendar getIssueDate() {
	return issueDate;
}


public void setIssueDate(GregorianCalendar gregorianCalendar) {
	this.issueDate = gregorianCalendar;
}


public GregorianCalendar getModificationDate() {
	return modificationDate;
}


public void setModificationDate(GregorianCalendar modificationDate) {
	this.modificationDate = modificationDate;
}
public MyFolder getFolder() {
	return folder;
}


public void setFolder(MyFolder folder) {
	this.folder = folder;
}
 
}
