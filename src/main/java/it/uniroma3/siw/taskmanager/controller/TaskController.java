package it.uniroma3.siw.taskmanager.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import it.uniroma3.siw.taskmanager.controller.validation.CommentValidator;
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Comment;
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
public class TaskController {

	@Autowired
	TaskService taskService;

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	TagService tagService;

	@Autowired
	TaskValidator taskValidator;

	@Autowired
	CommentValidator commentValidator;

	@Autowired
	SessionData sessionData;

	// VISUALIZZARE UN TASK DI UN PROGETTO

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}"}, method = RequestMethod.GET)
	public String showTask(Model model,
			@PathVariable Long projectId,
			@PathVariable Long taskId) {
		User loggedUser = this.sessionData.getLoggedUser();
		Task task = this.taskService.getTask(taskId);
		Project project = this.projectService.getProject(projectId);
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();

		if(project == null)
			return "redirect:/projects";

		List<User> members = this.userService.getMembers(project);
		// user che non può accedere al progetto
		if(!project.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE) && !members.contains(loggedUser))
			return "accessDenied";

		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectOwner", project.getOwner());
		model.addAttribute("projectId", projectId);
		model.addAttribute("project", project);
		model.addAttribute("task", task);
		return "task";  //vista con singolo project
	}

	// AGGIUNGERE UN NUOVO TASK A UN PROGETTO

	@RequestMapping(value = {"/projects/{projectId}/newTask"}, method = RequestMethod.GET)
	public String showAddNewTaskForm(@PathVariable Long projectId, Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Project p = this.projectService.getProject(projectId);
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();

		if(p == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!p.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "accessDenied";

		Task taskForm = new Task();
		model.addAttribute("taskForm", taskForm);
		model.addAttribute("projectId", projectId);
		model.addAttribute("project", p);
		return "createNewTask";
	}

	@RequestMapping(value = {"/projects/{projectId}/newTask"}, method = RequestMethod.POST)
	public String addNewTaskForm(@Valid @ModelAttribute("taskForm") Task task,
			@PathVariable Long projectId,
			BindingResult taskBindingResult) {

		this.taskValidator.validate(task, taskBindingResult);

		Project p = this.projectService.getProject(projectId);
		if(!taskBindingResult.hasErrors()) {
			this.projectService.addTask(p, task);
			return "taskAddedSuccessful";
		}
		return "createNewTask";
	}

	// AGGIORNARE UN TASK DI UN MIO PROGETTO


	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/edit"}, method = RequestMethod.GET)
	public String showEditTaskForm(@PathVariable Long projectId, @PathVariable Long taskId, Model model) {

		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
		Project p = this.projectService.getProject(projectId);

		if(p == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!p.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "accessDenied";

		model.addAttribute("taskForm", new Task());
		model.addAttribute("taskId", taskId);
		return "editTaskForm";
	}

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/edit"}, method = RequestMethod.POST)
	public String editTask(@Valid @ModelAttribute("taskForm") Task task,
			@PathVariable Long projectId,
			@PathVariable Long taskId,						   
			BindingResult tasktBindingResult,
			Model model) {
		this.taskValidator.validate(task, tasktBindingResult);

		Task currentTask = this.taskService.getTask(taskId);
		if(!tasktBindingResult.hasErrors()) {
			currentTask.setName(task.getName());
			currentTask.setDescription(task.getDescription());
			this.taskService.saveTask(currentTask);
			return "editSuccess";	
		}
		return "editTaskForm";
	}

	// CANCELLARE UN TASK DA UN MIO PROGETTO

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/delete"}, method = RequestMethod.GET)
	public String showDeleteTaskForm(@PathVariable Long projectId, @PathVariable Long taskId, Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
		Task task = this.taskService.getTask(taskId);
		Project p = this.projectService.getProject(projectId);

		if(p == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!p.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "accessDenied";

		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", p);
		model.addAttribute("task", task);
		model.addAttribute("taskId", taskId);
		return "deleteTaskForm";
	}

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/delete"}, method = RequestMethod.POST)
	public String deleteTask(@PathVariable Long taskId) {
		this.taskService.deleteTaskById(taskId);
		return "deleteSuccess";	
	}

	// ASSEGNARE UN TASK AD UN UTENTE SELEZIONANDO LO USERNAME

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/assign"}, method = RequestMethod.GET)
	public String showAssignTaskToUserForm(@PathVariable Long projectId, @PathVariable Long taskId, Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
		Task task = this.taskService.getTask(taskId);
		Project p = this.projectService.getProject(projectId);

		if(p == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!p.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE) && !task.getAssignedUser().equals(loggedUser))
			return "accessDenied";

		model.addAttribute("projectId", projectId);
		model.addAttribute("task", task);
		model.addAttribute("taskId", taskId);
		model.addAttribute("currentCredentials", new Credentials());
		return "assignTaskForm";
	}

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/assign"}, method = RequestMethod.POST)
	public String assignTaskToUser(@Valid @ModelAttribute("currentCredentials") Credentials credentials,
			@PathVariable Long projectId, @PathVariable Long taskId) {

		String userName = credentials.getUserName();
		String loggedUserName = this.sessionData.getLoggedCredentials().getUserName();
		User user = this.credentialsService.getCredentialsByUserName(userName).getUser();
		Project p = this.projectService.getProject(projectId);

		List<User> members = this.userService.getMembers(p);
		Task t = this.taskService.getTask(taskId);
		if(!userName.equals(loggedUserName) && this.credentialsService.getCredentialsByUserName(userName)!=null && members.contains(user)) {
			this.taskService.addTaskToUser(user, t);
			return "assignedSuccessfully";
		}
		return "assignTaskForm";
	}

	// AGGIUNGERE UN TAG A UN TASK DI UN MIO PROGETTO

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/addTag"}, method = RequestMethod.GET)
	public String showAddTagToTaskForm(@PathVariable Long projectId, @PathVariable Long taskId, Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
		Task task = this.taskService.getTask(taskId);
		Project p = this.projectService.getProject(projectId);

		if(p == null)
			return "redirect:/projects";

		// user che non può accedere al progetto
		if(!p.getOwner().equals(loggedUser) && !loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE) && !task.getAssignedUser().equals(loggedUser))
			return "accessDenied";

		List<Tag> allProjectTags = this.projectService.getProject(projectId).getTags();
		List<Tag> validTags = allProjectTags.stream()
				.filter(tag -> !task.getTags().contains(tag))
				.collect(Collectors.toList());

		model.addAttribute("validTags", validTags);
		model.addAttribute("project", p);
		model.addAttribute("projectId", projectId);
		model.addAttribute("task", task);
		model.addAttribute("taskId", taskId);
		return "addTagToTaskForm";
	}

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/addTag/{tagId}"}, method = RequestMethod.POST)
	public String addTagToTask(@PathVariable Long projectId, @PathVariable Long taskId, @PathVariable Long tagId, Model model) {

		Task task = this.taskService.getTask(taskId);
		Tag tag = this.tagService.getTag(tagId);
		model.addAttribute("tag", tag);
		model.addAttribute("task", task);

		if(!task.getTags().contains(tag)) {
			this.taskService.addTagToTask(tag, task);
			return "tagAddedToTask";
		}
		return "redirect:/projects/{projectId}/tasks/{taskId}";
	}


	// AGGIUNGERE UN COMMENTO A UN TASK DI CUI HO VISIBILITA'

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/addComment"}, method = RequestMethod.GET)
	public String showAddCommentToTaskForm(@PathVariable Long projectId, @PathVariable Long taskId, Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Task task = this.taskService.getTask(taskId);
		Project p = this.projectService.getProject(projectId);

		if(task == null)
			return "redirect:/projects";

		List<User> members = this.userService.getMembers(p);
		// user che non può accedere al progetto
		if(!members.contains(loggedUser))
			return "redirect:/projects/{projectId}/tasks/{taskId}";

		model.addAttribute("comment", new Comment());
		model.addAttribute("project", p);
		model.addAttribute("projectId", projectId);
		model.addAttribute("task", task);
		model.addAttribute("taskId", taskId);
		return "addCommentForm";
	}

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/addComment"}, method = RequestMethod.POST)
	public String addCommentToTaskForm(@Valid @ModelAttribute("comment") Comment comment,
			BindingResult commentBindingResult,
			@PathVariable Long projectId, 
			@PathVariable Long taskId, 
			Model model) {	
		User loggedUser = this.sessionData.getLoggedUser();
		Task t = this.taskService.getTask(taskId);

		this.commentValidator.validate(comment, commentBindingResult);

		if(!commentBindingResult.hasErrors()) {
			comment.setUser(loggedUser);
			this.taskService.addCommentToTask(comment, t);
			return "editSuccess";  //da fare e aggiungere anche il commento al task.html e da chi è stato aggiunto
		}
		model.addAttribute("task", t);
		model.addAttribute("loggedUser", loggedUser);
		return "addCommentForm";
	}

	// RIMOZIONE TAG DAL TASK

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/deleteTag/{tagId}"}, method = RequestMethod.GET)
	public String deleteTagFromTaskForm(@PathVariable Long projectId,
			@PathVariable Long taskId, 
			@PathVariable Long tagId, 
			Model model) {
		Project p = this.projectService.getProject(projectId);
		Tag t = this.tagService.getTag(tagId);
		User loggedUser = this.sessionData.getLoggedUser();

		if(!p.getOwner().equals(loggedUser))
			return "accessDenied";

		model.addAttribute("tag", t);
		model.addAttribute("tagId", tagId);
		model.addAttribute("projectId", projectId);
		return "deleteTagFromTask";
	}

	// ricerco il tag nei task del project e quelli prescelti li metto in una lista, scrorro la lista e elimino il tag e poi lo oelimino dal project
	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/deleteTag/{tagId}"}, method = RequestMethod.POST)
	public String deleteTaskTag(@PathVariable Long projectId,
								@PathVariable Long taskId, 
								@PathVariable Long tagId) {
		Task task = this.taskService.getTask(taskId);
		Tag tag = this.tagService.getTag(tagId);

		this.taskService.deleteTag(tag, task);

		return "redirect:/projects/{projectId}/tasks/{taskId}";
	}
}
