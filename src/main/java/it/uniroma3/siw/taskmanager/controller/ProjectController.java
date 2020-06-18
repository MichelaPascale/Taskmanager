package it.uniroma3.siw.taskmanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.ProjectValidator;
import it.uniroma3.siw.taskmanager.controller.validation.TagValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TagService;
import it.uniroma3.siw.taskmanager.service.TaskService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class ProjectController {

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	ProjectValidator projectValidator;

	@Autowired
	TagService tagService;

	@Autowired
	TaskService taskService;

	@Autowired
	TagValidator tagValidator;

	@Autowired
	SessionData sessionData;

	// CREARE UN NUOVO PROJECT

	@RequestMapping(value = {"projects/add"}, method = RequestMethod.GET)
	public String showProjectForm(Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", new Project());
		return "createProject";
	}

	@RequestMapping(value = {"projects/add"}, method = RequestMethod.POST)
	public String addNewProject(@Valid @ModelAttribute("project") Project project,
			BindingResult projectBindingResult,
			Model model) {

		User loggedUser = this.sessionData.getLoggedUser();

		this.projectValidator.validate(project, projectBindingResult);

		if(!projectBindingResult.hasErrors()) {
			project.setOwner(loggedUser);
			this.projectService.saveProject(project);
			return "redirect:/projects/" + project.getId();
		}
		model.addAttribute("loggedUser", loggedUser);
		return "createProject";
	}

	// VISUALIZZARE I MIEI PROGETTI

	@RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
	public String showMyProjects(Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		List<Project> myProjects = this.projectService.getMyProjects(loggedUser);
		model.addAttribute("myProjects", myProjects);
		model.addAttribute("loggedUser", loggedUser);
		return "showMyProjects";
	}


	// VISUALIZZARE UN PROGETTO

	@RequestMapping(value = {"/projects/{projectId}"}, method = RequestMethod.GET)
	public String showProject(Model model,
			@PathVariable Long projectId) {

		User loggedUser = this.sessionData.getLoggedUser();

		Project project = this.projectService.getProject(projectId);
		if(project == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		List<User> members = this.userService.getMembers(project);
		if(!project.getOwner().equals(loggedUser) && !members.contains(loggedUser))
			return "accessDenied";

		// se owner o membro
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("members", members);
		model.addAttribute("project", project);
		return "project";  //vista con singolo project
	}

	// VISUALIZZARE I PROGETTI CONDIVISI CON ME

	@RequestMapping(value = {"/projects/shared"}, method = RequestMethod.GET)
	public String sharedProjects(Model model) {

		User loggedUser = this.sessionData.getLoggedUser();
		List<Project> visibleProjects = this.projectService.getAllVisibleProjects(loggedUser);

		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("visibleProjects", visibleProjects);
		return "showVisibleProjects";  //vista con singolo project
	}

	// AGGIORNARE UN MIO PROGETTO

	@RequestMapping(value = {"/projects/{projectId}/edit"}, method = RequestMethod.GET)
	public String showEditProjectForm(@PathVariable Long projectId, Model model) {

		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();

		Project project = this.projectService.getProject(projectId);
		if(project == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!project.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "accessDenied";

		model.addAttribute("projectForm", new Project());
		model.addAttribute("projectId", projectId);
		return "editProjectForm";
	}

	@RequestMapping(value = {"/projects/{projectId}/edit"}, method = RequestMethod.POST)
	public String editProject(@Valid @ModelAttribute("projectForm") Project project,
			@PathVariable Long projectId,						   
			BindingResult projectBindingResult,
			Model model) {

		this.projectValidator.validate(project, projectBindingResult);		

		Project currentProject = this.projectService.getProject(projectId);
		if(!projectBindingResult.hasErrors()) {
			currentProject.setName(project.getName());
			this.projectService.saveProject(currentProject);
			return "editSuccess";	
		}
		return "editProjectForm";
	}

	// CANCELLARE UN MIO PROGETTO

	@RequestMapping(value = {"/projects/{projectId}/delete"}, method = RequestMethod.GET)
	public String showDeleteProjectForm(@PathVariable Long projectId, Model model) {

		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();

		Project p = this.projectService.getProject(projectId);
		if(p == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!p.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "accessDenied";

		Project project = this.projectService.getProject(projectId);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", project);
		model.addAttribute("projectId", projectId);
		return "deleteProjectForm";
	}

	@RequestMapping(value = {"/projects/{projectId}/delete"}, method = RequestMethod.POST)
	public String deleteProject(@PathVariable Long projectId) {
		this.projectService.deleteProjectById(projectId);
		return "deleteSuccess";	
	}

	// CONDIVIDERE UN MIO PROGETTO CON UN ALTRO UTENTE

	@RequestMapping(value = {"/projects/{projectId}/share"}, method = RequestMethod.GET)
	public String shareProjectForm(@PathVariable Long projectId, Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
		Project p = this.projectService.getProject(projectId);
		if(p == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!p.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "accessDenied";
		
		Credentials c = new Credentials();
		model.addAttribute("credentials", c);
		model.addAttribute("projectId", projectId);
		return "shareProjectForm";  //form che mi deve mostrare l'inserimento di uno username
	}

	@RequestMapping(value = {"/projects/{projectId}/share"}, method = RequestMethod.POST)
	public String shareProject(@Valid @ModelAttribute("credentials") Credentials credentials,
			@PathVariable Long projectId) {

		String userName = credentials.getUserName();
		String ownerUserName = this.sessionData.getLoggedCredentials().getUserName();
		User user = this.credentialsService.getCredentialsByUserName(userName).getUser();
		Project p = this.projectService.getProject(projectId);

		if(!userName.equals(ownerUserName) && this.credentialsService.getCredentialsByUserName(userName)!=null) {
			this.projectService.shareProjectWithUser(user, p);
			return "shareWithSuccess";
		}
		return "shareProjectForm";
	}

	// AGGIUNGERE UN TAG A UN MIO PROGETTO

	@RequestMapping(value = {"/projects/{projectId}/newTag"}, method = RequestMethod.GET)
	public String showAddNewTagForm(@PathVariable Long projectId, Model model) {
		Project p = this.projectService.getProject(projectId);
		User loggedUser = this.sessionData.getLoggedUser();

		if(!p.getOwner().equals(loggedUser))
			return "accessDenied";

		Tag tagForm = new Tag();
		model.addAttribute("tagForm", tagForm);
		model.addAttribute("projectId", projectId);
		return "createNewProjectTag";
	}

	@RequestMapping(value = {"/projects/{projectId}/newTag"}, method = RequestMethod.POST)
	public String addNewTagForm(@Valid @ModelAttribute("tagForm") Tag tag,
			@PathVariable Long projectId,
			BindingResult tagBindingResult,
			Model model) {

		this.tagValidator.validate(tag, tagBindingResult);

		Project p = this.projectService.getProject(projectId);
		if(!tagBindingResult.hasErrors()) {
			this.projectService.addTag(p, tag);
			return "tagAddedSuccessful";
		}
		return "createNewProjectTag";
	}

	// RIMOZIONE TAG DAL PROGETTO E DAI TASK CHE CE L'HANNO
	
	@RequestMapping(value = {"/projects/{projectId}/deleteTag/{tagId}"}, method = RequestMethod.GET)
	public String deleteTagForm(@PathVariable Long projectId, @PathVariable Long tagId, Model model) {
		Project p = this.projectService.getProject(projectId);
		Tag t = this.tagService.getTag(tagId);
		User loggedUser = this.sessionData.getLoggedUser();

		if(!p.getOwner().equals(loggedUser))
			return "accessDenied";

		model.addAttribute("tag", t);
		model.addAttribute("tagId", tagId);
		model.addAttribute("projectId", projectId);
		return "deleteTag";
	}

	// ricerco il tag nei task del project e quelli prescelti li metto in una lista, scrorro la lista e elimino il tag e poi lo oelimino dal project
	@RequestMapping(value = {"/projects/{projectId}/deleteTag/{tagId}"}, method = RequestMethod.POST)
	public String deleteProjectTag(@PathVariable Long projectId, @PathVariable Long tagId, Model model) {
		Project p = this.projectService.getProject(projectId);
		Tag tag = this.tagService.getTag(tagId);
		User loggedUser = this.sessionData.getLoggedUser();

		if(!p.getOwner().equals(loggedUser))
			return "redirect:/projects";
		
		List<Task> tasks = p.getTasks();
		for(Task t: tasks) {
			List<Tag> tags= t.getTags();
			if(tags.contains(tag)) {
				this.taskService.deleteTag(tag, t);
			}
		}

		List<Tag> pTags = p.getTags();
		if(pTags.contains(tag)) {
			this.projectService.deleteTag(tag, p);
		}
		
		this.tagService.deleteTagById(tagId);
		
		return "redirect:/projects/{projectId}";
	}

}
