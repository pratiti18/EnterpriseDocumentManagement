package org.docManagement.controller;

import java.io.IOException;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.http.client.ClientProtocolException;
import org.docManagement.Model.Doc;
import org.docManagement.Model.MyFolder;
import org.docManagement.service.documentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
public class dMController {
	
	@Autowired
	public documentService DocumentService;
	
	//Done
	@PostMapping("/addDoc")
	public String createDocInsideFolder(@RequestParam String name,@RequestBody Doc doc) {
		return DocumentService.addDocumentInsideFolder(name,doc);
	}
	
	//Done
	@PostMapping("/addDocOut")
	public String createDocOutsideFolder(@RequestBody Doc doc) {
		return DocumentService.addDocumentOutsideFolder(doc);
		
	}
	
	//done
	@GetMapping("/getDoc")
	public String getDocument(@RequestParam String foldName,@RequestParam String docName) {
		return DocumentService.getDocumentByName(foldName,docName);
	}
	
	//done
	@GetMapping("/listDocsFolder")
	public List<Doc> getDocsFromFolder(@RequestParam String foldName){
		return DocumentService.listDocumentsInAFolder(foldName);
	}
	
	//done
	@GetMapping("/listDocs")
	public List<Doc> getList(){
		return DocumentService.listAllDocs();
	}
	
	
	//Done
	@GetMapping("/getDocSame")
	public List<Doc> getDocumentSame(@RequestParam String docName){
		return DocumentService.getDocumentsOfSameName(docName);
	}
	
	//Done
	@DeleteMapping("/deleteDoc")
	public String deleteDoc(@RequestParam String foldName,@RequestParam String docName) {
		String id=DocumentService.getDocumentByName(foldName, docName);
		return DocumentService.deleteDocument(id);
	}
	
	
	@PutMapping("/updateDoc")
	public String updateDoc(@RequestParam String foldName,@RequestParam String docName,@RequestBody Doc doc) {
	return DocumentService.updateDocument(foldName, docName, doc);
	}
	
	//Done
	@PostMapping("/addFolder")
	public String addFolder(@RequestBody MyFolder fold,@RequestParam String name,@RequestParam String inheritance) {
		
		return DocumentService.createFolder(fold,name,inheritance);
	}
	
	//Done
	@GetMapping("/listFolder")
	public List<MyFolder> listOfFolder(){
		return DocumentService.listFolders();
	}
	
	//Done
	@DeleteMapping("/deleteFolder")
	public String deleteFolder(@RequestParam String name) {
		return DocumentService.deleteFolder(name);
	}
	
	//Done
	@GetMapping("/findFolder")
	public String findFolder(@RequestParam String name) {
		return DocumentService.findFolderByName(name);
	}
	
	//Done
	@PutMapping("/updateFolder")
	public String updateFolder(@RequestParam String name,@RequestBody MyFolder newFold) {
		return DocumentService.updateFolder(name,newFold);
	}
	
	//Done
	@PutMapping("/setRole")
		public String setRole(@RequestParam String foldname,@RequestParam List<String> group,@RequestParam String name,@RequestParam String inheritanceEnable) {
			return DocumentService.setRole(foldname, group,name,inheritanceEnable);
		}
	
	//Done
	@GetMapping("/roleBasedFolder")
	public List<MyFolder> getFoldersBasedOnRoles(){
		return DocumentService.roleBasedFolderList();
	}
	}
//@GetMapping("/roleCreation")
//public String createRole(@RequestParam String roleName) {
//		return DocumentService.getGroupId(roleName);
//	}
//}

