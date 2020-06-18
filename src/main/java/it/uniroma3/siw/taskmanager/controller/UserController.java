package it.uniroma3.siw.taskmanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.UserValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class UserController {

	@Autowired
	SessionData sessionData;
	
	@Autowired
	UserValidator userValidator;
	
	@Autowired
	UserService userService;
	
	@Autowired
	CredentialsService credentialsService;
	
	@Autowired
	ProjectService projectService;
	
	@RequestMapping(value = { "/home" }, method = RequestMethod.GET)
	public String home(Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		model.addAttribute("user", loggedUser);
		return "home";  //restituisce la vista
	}
	
	@RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
	public String admin(Model model) {
		User loggedAdmin = this.sessionData.getLoggedUser();
		model.addAttribute("admin", loggedAdmin);
		return "admin";  //restituisce la vista
	}
	
	// VISUALIZZAZIONE DEL PROFILO
	
	@RequestMapping(value = {"/users/me"}, method = RequestMethod.GET)
	public String profile(Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
		model.addAttribute("user", loggedUser);
		model.addAttribute("credentials", loggedCredentials);
		return "userProfile";
	}
	
	// MODIFICA DEL PROFILO
	
	@RequestMapping(value = { "/users/edit" }, method = RequestMethod.GET)
	public String showEditForm(Model model) {
		User user = new User();
		model.addAttribute("userForm", user);
		
		return "editUser";
	}
	
	@RequestMapping(value = { "/users/edit" }, method = RequestMethod.POST)
	public String editUser(@Valid @ModelAttribute("userForm") User user,
								BindingResult userBindingResult) {
		
		// validate user and credentials fields
		this.userValidator.validate(user, userBindingResult);
		
		// if neither of them had invalid content, store the User and the Credentials into the DB
		if(!userBindingResult.hasErrors()) {
			User loggedUser = this.sessionData.getLoggedUser();
			loggedUser.setFirstName(user.getFirstName());
			loggedUser.setLastName(user.getLastName());
			this.userService.saveUser(loggedUser);
			return "editSuccess";	
		}
		return "editUser";
	}
	
	// VISUALIZZARE E RIMUOVERE LA LISTA DI UTENTI SE ADMIN
	
	@RequestMapping(value = { "/admin/users" }, method = RequestMethod.GET)
	public String showUsersListForm(Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
		List<Credentials> allCredentials = this.credentialsService.getAllCredentials();
		
		if(!loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "accessDenied";
		}
		
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("allCredentials", allCredentials);
		
		return "allUsers";
	}
	
	@RequestMapping(value = { "/admin/users/{username}/delete" }, method = RequestMethod.POST)
	public String deleteUser(Model model, @PathVariable String username) {
		this.credentialsService.deleteCredentials(username);
		return "redirect:/admin/users";
	}
	
	// VISUALIZZARE E RIMUOVERE IL PROGETTO DI UN UTENTE SE ADMIN
	
	@GetMapping(value = "/admin/users/{userName}/projects")
    public String showUsersProjectsForm(@PathVariable String userName,
                                        Model model) {
		Credentials loggedCredentials = this.sessionData.getLoggedCredentials();

		if(!loggedCredentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "accessDenied";
		}
		
        User user = this.credentialsService.getCredentialsByUserName(userName).getUser();
        model.addAttribute("userName", userName);
        model.addAttribute("allProjects", this.projectService.getMyProjects(user));
        return "allUserProjects";
    }
	
	@PostMapping(value = "/admin/users/{userName}/projects/{projectId}/delete")
    public String showUserProject(@PathVariable String userName,
                                    @PathVariable Long projectId,
                                    Model model) {
        Project projectToDelete = this.projectService.getProject(projectId);
        if(projectToDelete != null) {
            this.projectService.deleteProject(projectToDelete);
            return "redirect:/admin/users";
        }
        return "/admin";
    }

}
