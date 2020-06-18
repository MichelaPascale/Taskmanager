package it.uniroma3.siw.taskmanager.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.taskmanager.model.Comment;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;

	// RICERCA TASK
	@Transactional
	public Task getTask(Long id) {
		Optional<Task> t = this.taskRepository.findById(id);
		return t.orElse(null);
	}

	// SALVARE/AGGIORNARE UN TASK DI UN MIO PROGETTO
	@Transactional
	public Task saveTask(Task t) {
		return this.taskRepository.save(t);
	}

	// CANCELLARE UN TASK DA UN MIO PROGETTO
	@Transactional
	public void deleteTaskById(Long id) {
		this.taskRepository.deleteById(id);
	}

	@Transactional
	public void deleteTask(Task t) {
		this.taskRepository.delete(t);
	}

	// ASSEGNARE UN TASK DI UN PROGETTO AD UN UTENTE CHE HA VISIBILITA' DEL PROGETTO
	@Transactional
	public void addTaskToUser(User u, Task t) {
		t.addUser(u);
		this.taskRepository.save(t);
	}

	// AGGIUNGERE UN TAG A UN TASK DI UN MIO PROGETTO
	@Transactional
	public void addTagToTask(Tag tag, Task task) {
		task.addTag(tag);
		this.taskRepository.save(task);
	}

	// AGGIUNGERE UN COMMENT A UN TASK DI UN MIO PROGETTO
	@Transactional
	public void addCommentToTask(Comment c, Task t) {
		t.addComment(c);
		this.taskRepository.save(t);
	}

	// ELIMINARE UN TAG DA UN TASK DI UN MIO PROGETTO
	@Transactional
	public void deleteTag(Tag tag, Task task) {
		task.removeTag(tag);
		this.taskRepository.save(task);
	}
}
