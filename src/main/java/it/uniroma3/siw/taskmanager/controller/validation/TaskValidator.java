package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Task;

@Component
public class TaskValidator  implements Validator {

	final Integer MIN_NAME_LENGTH = 2;
	final Integer MAX_NAME_LENGTH = 100;
	final Integer MIN_DESCRIPTION_LENGTH = 4;
	final Integer MAX_DESCRIPTION_LENGTH = 1000;


	@Override
	public void validate(Object o, Errors errors) {

		Task task = (Task) o;
		String name = task.getName();
		String description = task.getDescription();

		if(name.isEmpty())
			errors.rejectValue("name", "required");
		else if(name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH)
			errors.rejectValue("name", "size");

		if(description.isEmpty())
			errors.rejectValue("description", "required");
		else if(description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH)
			errors.rejectValue("description", "size");
	}

	@Override
	public boolean supports(Class<?> tClass) {
		return Task.class.equals(tClass); 
	}

}
