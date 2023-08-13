package org.docManagement.service;

import java.io.IOException;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.http.client.ClientProtocolException;
import org.docManagement.Model.Doc;
import org.docManagement.Model.MyFolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface documentService{
	
	public String addDocumentInsideFolder(String Foldername,Doc document);
	public String addDocumentOutsideFolder(Doc document);
	public String updateDocument(String foldName,String docName,Doc document);
	public String getDocumentByName(String foldName,String docName);
	public List<Doc> getDocumentsOfSameName(String docName);
	public String deleteDocument(String id);
	public List<Doc> listDocumentsInAFolder(String name);
	public List<Doc> listAllDocs();
	public Session createSession();
	public String createFolder(MyFolder folder,String name,String inheritance);
	public String updateFolder(String name,MyFolder newFolder);
	public String findFolderByName(String name);
	public String deleteFolder(String name);
	public List<MyFolder> listFolders();
	public String setRole(String folderName,List<String> role,String name,String inheritanceEnable);
	public List<MyFolder> roleBasedFolderList();
	public List<Doc> roleBasedDocsList();
	}