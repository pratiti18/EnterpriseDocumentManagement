package org.docManagement.service;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.AclCapabilities;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;
import org.apache.chemistry.opencmis.commons.enums.SupportedPermissions;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.json.JSONArray;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.docManagement.Model.Doc;
import org.docManagement.Model.MyFolder;
import org.docManagement.repository.DocumentRepository;
import org.docManagement.repository.MyFolderRepository;
import org.hibernate.internal.build.AllowSysOut;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.nimbusds.jose.shaded.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class documentServiceImpl implements documentService {


	@Autowired
	public DocumentRepository documentRepository;

	@Autowired
	public MyFolderRepository myFolderRepository;
	
	@Autowired
	public ILoginService IloginService;

/*I have here considered that if anyone wants to create a document independently i.e., if not in any folder in particular, then he/she can 
 * create the folder in the Root Alfresco Folder provided by Alfresco, which just for the ease of code i have used the name "root". It can be changed as per 
 * requirement */
	@Override
	public String getDocumentByName(String foldName, String docName) {
		if (foldName.equals("root")) {
			List<Doc> list = documentRepository.findDocByName(docName);
			for (Doc doc : list) {
				if (doc.getFolder() == null)
					return doc.getId();
			}
		}
		List<Doc> list = documentRepository.findDocByName(docName);
		MyFolder fold = myFolderRepository.findByName(foldName);
		for (Doc doc : list) {
			
			if (fold.getId().equals(doc.getFolder().getId()))
				return doc.getId();
		}
		return null;
	}

/*To fetch all the documents which have the same name. The documents with same name might be present in other folders, but can fetched. */
	//Done
	@Override
	public List<Doc> getDocumentsOfSameName(String docName) {
		List<Doc> list = new ArrayList<>();
		List<Doc> resultList = new ArrayList<>();
		try {
			UserRepresentation user=IloginService.getCurrentUser();
			Keycloak keycloak=IloginService.keyCloakBuilder();
			RealmResource realmResource=keycloak.realm("emplEngagement");
			List<RoleRepresentation> groups=realmResource.users().get(user.getId()).roles().realmLevel().listAll();
			List<String> roles = groups.stream()
	                .map(RoleRepresentation::getName)
	                .collect(Collectors.toList());
			roles.remove("default-roles-emplengagement");
			List<MyFolder> foldList=roleBasedFolderList();
			list = documentRepository.findDocByName(docName);
			List<MyFolder> docIdList=list.stream().map(Doc::getFolder).collect(Collectors.toList());
			docIdList.retainAll(foldList);
			resultList=documentRepository.listOfDocs(docIdList);
			return resultList;
		} catch (Exception e) {
			return resultList;
		}
	}

/* A session here is being created. Repository Id can be very easily fetched by hitting GET:"http://localhost:8080/alfresco/api/discovery this api"*/
	//Done
	public Session createSession() {
		SessionFactory factory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<>();
		parameter.put(SessionParameter.USER, "admin");
		parameter.put(SessionParameter.PASSWORD, "admin");
		parameter.put(SessionParameter.ATOMPUB_URL,
				"http://127.0.0.1:8080/alfresco/api/-default-/public/cmis/versions/1.0/atom");
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.REPOSITORY_ID, "447a1693-583d-46ec-bcad-52b4afe94717");
		Session session = factory.getRepositories(parameter).get(0).createSession();
		return session;
	}

	// Done
	@Override
	//Done
	public String addDocumentInsideFolder(String name, Doc doc) {
//		// TODO Auto-generated method stub
		try {
			String str=findFolderByName(name);
			if(str!=null && !str.isEmpty())
			{
			Session session = createSession();
			String id = findFolderByName(name);
			MyFolder fold = myFolderRepository.findByName(name);
			
			List<String> foldRoles=fold.getRole();
			Folder folder = (Folder) session.getObject(id);
			System.out.println(folder.getName());
			Map<String, Object> properties2 = new HashMap<String, Object>();
			properties2.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties2.put(PropertyIds.NAME, doc.getName());
			properties2.put(PropertyIds.CREATED_BY, new Date());
			// properties2.put(PropertyIds.PARENT_ID,folder.getId());
			System.out.println(doc.getName());
			byte[] content = doc.getContent().getBytes();
			InputStream stream = new ByteArrayInputStream(content);
			ContentStream contentStream = new ContentStreamImpl(doc.getName(), BigInteger.valueOf(content.length),
					"text/plain", stream);
			Document document = folder.createDocument(properties2, contentStream, VersioningState.MAJOR);
			doc.setId(document.getId());
			doc.setName(document.getName());
			doc.setFolder(fold);
			doc.setIssueDate(document.getCreationDate());
			documentRepository.save(doc);
			return "Document creation successful";
			}else if(str==null){
				return "Unauthorized";
			}
			else {
				return "Exception at finding the Folder";
			}
		} catch (Exception e) {
			return "Document creation unsuccessful " + e;
		}

	}

	// Done
	@Override
	public String addDocumentOutsideFolder(Doc doc) {
		try {
			Session session = createSession();
			Folder root = session.getRootFolder();
			Map<String, Object> properties2 = new HashMap<String, Object>();
			properties2.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties2.put(PropertyIds.NAME, doc.getName());
			byte[] content = doc.getContent().getBytes();
			InputStream stream = new ByteArrayInputStream(content);
			ContentStream contentStream = new ContentStreamImpl(doc.getName(), BigInteger.valueOf(content.length),
					"text/plain", stream);
			Document document = root.createDocument(properties2, contentStream, VersioningState.MAJOR);
			doc.setId(document.getId());
			doc.setIssueDate(document.getCreationDate());
			doc.setName(document.getName());
			// doc.setFolder((MyFolder)root);
			documentRepository.save(doc);
			return "Document creation successful";
		} catch (Exception e) {
			return "Document creation unsuccessful " + e;
		}

	}

	@Override
	public String updateDocument(String foldName, String docName, Doc doc) {
		try {
			String id = getDocumentByName(foldName, docName);
			Session session = createSession();
			Doc oldDoc;
			Document document = (Document) session.getObject(id);
			Optional<Doc> opt = documentRepository.findById(id);
			if (opt.isPresent())
				oldDoc = opt.get();
			else
				oldDoc = null;
			document.rename(doc.getName());
			Map<String, Object> properties = new HashMap<>();
			properties.put(PropertyIds.LAST_MODIFICATION_DATE, new Date());
			document.updateProperties(properties);
			oldDoc.setName(document.getName());
			oldDoc.setModificationDate(document.getLastModificationDate());
			documentRepository.save(oldDoc);
			return "Update Success";
		} catch (Exception e) {
			return "Update not Successful";
		}

	}

//Done
	@Override
	public String deleteDocument(String id) {
		// TODO Auto-generated method stub
		try {
			Session session = createSession();
			Document document = (Document) session.getObject(id);
			Optional<Doc> doc = documentRepository.findById(id);

			if (doc.isPresent() && document != null)
				documentRepository.deleteById(id);
			document.delete(true);
			return "Delete successful";
		} catch (Exception e) {
			return "Delete Not Successful";
		}

	}


	@Override
	public List<Doc> listDocumentsInAFolder(String foldName) {
		// TODO Auto-generated method stub
		MyFolder fold = myFolderRepository.findByName(foldName);
		List<Doc> list = new ArrayList<>();
		list = documentRepository.listOfDocsInAFolder(fold);
		return list;
	}

	// Done
	@Override
	public String createFolder(MyFolder folder,String name,String inheritance) {
		UserRepresentation user=IloginService.getCurrentUser();
		Keycloak keycloak=IloginService.keyCloakBuilder();
		RealmResource realmResource=keycloak.realm("emplEngagement");
		//String groupName="";
		List<RoleRepresentation> groupNames=realmResource.users().get(user.getId()).roles().realmLevel().listAll();
		List<String> roleNames = groupNames.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
		roleNames.remove("default-roles-emplengagement");
		
		try {
			Session session = createSession();
			Folder root = session.getRootFolder();
			Map<String, Object> properties = new HashMap<>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			properties.put(PropertyIds.CREATION_DATE, new Date());
			properties.put(PropertyIds.NAME, folder.getName());
			properties.put(PropertyIds.PARENT_ID, root.getId());
			Folder oneFolder = root.createFolder(properties);
			folder.setId(oneFolder.getId());
			folder.setName(oneFolder.getName());
			folder.setParentId(root.getId());
			folder.setIssueDate(oneFolder.getCreationDate());
			myFolderRepository.save(folder);
			String statusCode=setRole(folder.getName(),roleNames,name,inheritance);
			
			return "Successfully created  "+statusCode;
			
		} catch (Exception e) {
			return "Creation unsuccessful"+e;
		}
	}
	//Done
	@Override
public String setRole(String folderName,List<String> groupNames,String name,String inheritanceEnable) {
	try {
	MyFolder oneFolder=myFolderRepository.findByName(folderName);
	
	String id=oneFolder.getId();
	
	
	String baseUrl="http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/{id}";
	String url = baseUrl.replace("{id}", id);
	HttpClient httpClient = HttpClients.createDefault();
	HttpPut httpPut = new HttpPut(url);
	//String requestBody = "{\"authorityId\": \"" + groupId  + "\",\"name\":\"Consumer\", \"accessStatus\": \"ALLOWED\"}";
	Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> permissions = new HashMap<>();
    List<Map<String, String>> locallySet = new ArrayList<>();
  
    
    for(String group:groupNames) {
    String groupId="GROUP_"+group;
    Map<String, String> permissionSetting = new HashMap<>();
    permissionSetting.put("authorityId", groupId);
    permissionSetting.put("name", name);
    permissionSetting.put("accessStatus", "ALLOWED");
    locallySet.add(permissionSetting);
    }
 
    
    permissions.put("isInheritanceEnabled", inheritanceEnable);
    permissions.put("locallySet", locallySet);
    requestBody.put("permissions", permissions);
    HttpEntity entity = new StringEntity(new Gson().toJson(requestBody), ContentType.APPLICATION_JSON);
    httpPut.setEntity(entity);
    // HttpEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
    // httpPut.setEntity(entity);

String authHeader=getBasicAuthHeader();
httpPut.setHeader("Authorization", authHeader);
HttpResponse response=httpClient.execute(httpPut);
oneFolder.setRole(groupNames);
myFolderRepository.save(oneFolder);
//int statusCode=response.getStatusLine().getStatusCode();
//if (statusCode == 200) {
//    return statusCode+"";
//} else {
//    return statusCode+"";
//}
	return " done";
}catch(Exception e) {
	return ""+e;
}
}
//Done
private static String getBasicAuthHeader() {
    String credentials = "admin" + ":" + "admin";
    String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
    return "Basic " + encodedCredentials;
}

	// Done
	@Override
	public String updateFolder(String name, MyFolder newFolder) {
		try {
		Session session = createSession();
		String oldId = findFolderByName(name);
		MyFolder fold;
		Folder folder = (Folder) session.getObject(oldId);
		if (oldId != null) {
			Optional<MyFolder> opt = myFolderRepository.findById(oldId);
			if (opt.isPresent())
				fold = opt.get();
			else
				fold = null;
			folder.rename(newFolder.getName());
			Map<String, Object> properties = new HashMap<>();
			properties.put(PropertyIds.LAST_MODIFICATION_DATE, new Date());
			folder.updateProperties(properties);
			fold.setName(folder.getName());
			fold.setModificationDate(folder.getLastModificationDate());
			myFolderRepository.save(fold);
			return "Update Successful " + folder.getName();
		}
		return "Update unsuccessFull";
		}catch(Exception e) {
			return "Failed "+e;
		}
	}

	// Done
	@Override
	public String findFolderByName(String name) {
		UserRepresentation user=IloginService.getCurrentUser();
		Keycloak keycloak=IloginService.keyCloakBuilder();
		RealmResource realmResource=keycloak.realm("emplEngagement");
		List<RoleRepresentation> groups=realmResource.users().get(user.getId()).roles().realmLevel().listAll();
		List<String> roleNames = groups.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
		roleNames.remove("default-roles-emplengagement");
		Session session=createSession();
		MyFolder folder=myFolderRepository.findByName(name);
		List<String> folderRoles=folder.getRole();
		roleNames.retainAll(folderRoles);
		try {
			if(roleNames.isEmpty()==false)
			{
				return folder.getId();
			}
			else
				return null;
				
		} catch (Exception e) {
			return "";
		}
	}

	// Done
	@Override
	public String deleteFolder(String foldname) {
		// TODO Auto-generated method stub
		Session session = createSession();
		UserRepresentation user=IloginService.getCurrentUser();
		Keycloak keycloak=IloginService.keyCloakBuilder();
		RealmResource realmResource=keycloak.realm("emplEngagement");
		List<RoleRepresentation> groups=realmResource.users().get(user.getId()).roles().realmLevel().listAll();
		String role="";
		if(groups.get(0).toString().equals("default-roles-emplengagement"))
		role=groups.get(1).toString();
		else
			role=groups.get(0).toString();
		MyFolder fold = myFolderRepository.findByName(foldname);
		Folder folder = null;
		try {
			folder = (Folder) session.getObject(fold.getId());
		} catch (Exception e) {
			return "Folder Not present";
		}

		folder.delete(true);
		myFolderRepository.delete(fold);
		return "Delete Successfull";

	}

	// Done
	@Override
	public List<MyFolder> listFolders() {
		List<MyFolder> list = myFolderRepository.findAll();
		return list;
	}

	// Done
	@Override
	public List<Doc> listAllDocs() {
		List<Doc> list = documentRepository.findAll();
		return list;
	}

	@Override
	public List<MyFolder> roleBasedFolderList() {
		// TODO Auto-generated method stub
		try {
		UserRepresentation user=IloginService.getCurrentUser();
		Keycloak keycloak=IloginService.keyCloakBuilder();
		RealmResource realmResource=keycloak.realm("emplEngagement");
		List<RoleRepresentation> groups=realmResource.users().get(user.getId()).roles().realmLevel().listAll();
		List<String> roles = groups.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
		roles.remove("default-roles-emplengagement");
		
		List<MyFolder> list=myFolderRepository.findByRole(roles);
		return list;
		}catch(Exception e) {
			
			return null;
		}
	}

	@Override
	public List<Doc> roleBasedDocsList() {
		// TODO Auto-generated method stub
		
		return null;
	}
	
}