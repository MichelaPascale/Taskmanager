package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.taskmanager.model.Project;


@Component
public class ProjectValidator implements Validator {

	final Integer MIN_NAME_LENGTH = 2;
	final Integer MAX_NAME_LENGTH = 100;
	final Integer MAX_DESCRIPTION_LENGTH = 1000;
	
	@Override
	public void validate(Object o, Errors errors) {
		
		Project project = (Project) o;
		String name = project.getName().trim();

		// per i campi vuoti e lunghezze minori o superiori
		if(name.isEmpty())  //.isBlank() ??
			errors.rejectValue("name", "required");  // con messaggio di errrore "required"
		else if(name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH)
			errors.rejectValue("name", "size");	
	}
	
	@Override
	// Specifica che la classe che andiamo ad utilizzare è la classe User
	public boolean supports(Class<?> aClass) {
		return Project.class.equals(aClass);
	}
}
