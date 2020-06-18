package it.uniroma3.siw.taskmanager.service;

import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.repository.TagRepository;

@Service
public class TagService {

	@Autowired
	TagRepository tagRepository;

	// RICERCA DI UN TAG DATO L'ID
	@Transactional
	public Tag getTag(Long id) {
		Optional<Tag> t = this.tagRepository.findById(id);
		return t.orElse(null);
	}
	
	// CANCELLARE TAG
	@Transactional
	public void deleteTagById(Long id) {
		this.tagRepository.deleteById(id);
	}
}
