package it.uniroma3.siw.taskmanager.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	// SALVARE/AGGIORNARE UN PROJECT NEL DB
	@Transactional
	public Project saveProject(Project project) {
		return this.projectRepository.save(project);
	}

	// RICERCA DEI PROJECT DI UN UTENTE
	@Transactional
	public Project getProject(Long id) {
		Optional<Project> p = this.projectRepository.findById(id);
		return p.orElse(null);
	}

	// VISUALIZZARE I MIEI PROGETTI
	@Transactional
	public List<Project> getMyProjects(User user) {
		return this.projectRepository.findByOwner(user);
	}

	// VISUALIZZARE I PROGETTI CONDIVISI CON ME
	public List<Project> getAllVisibleProjects(User user) {
		return this.projectRepository.findByMembers(user);
	}

	// CANCELLARE UN MIO PROGETTO
	@Transactional
	public void deleteProjectById(Long id) {
		this.projectRepository.deleteById(id);
	}

	@Transactional
	public void deleteProject(Project p) {
		this.projectRepository.delete(p);;
	}

	// CONDIVIDERE UN PROGETTO CON UN ALTRO UTENTE
	@Transactional
	public void shareProjectWithUser(User user, Project p) {
		p.addMember(user);
		this.projectRepository.save(p);
	}

	// AGGIUNGERE UN TAG A UN MIO PROGETTO
	@Transactional
	public void addTag(Project p, Tag t) {
		p.addTag(t);
		this.projectRepository.save(p);
	}

	// AGGIUNGERE UN TASK A UN MIO PROGETTO
	@Transactional
	public void addTask(Project p, Task t) {
		p.addTask(t);
		this.projectRepository.save(p);
	}
	
	
	// ELIMINARE UN TAG DA UN TASK DI UN MIO PROGETTO
		@Transactional
		public void deleteTag(Tag tag, Project p) {
			p.removeTag(tag);
			this.projectRepository.save(p);
		}
}
