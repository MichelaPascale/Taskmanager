package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Tag;

@Component
public class TagValidator implements Validator {
	
	final Integer MIN_NAME_LENGTH = 2;
	final Integer MAX_NAME_LENGTH = 100;
	final Integer MIN_COLOR_LENGTH = 2;
	final Integer MAX_COLOR_LENGTH = 50;
	final Integer MIN_DESCRIPTION_LENGTH = 4;
	final Integer MAX_DESCRIPTION_LENGTH = 1000;
	
	
	@Override
	public void validate(Object o, Errors errors) {
		
		Tag tag = (Tag) o;
		String name = tag.getName();
		String color = tag.getColor();
		String description = tag.getDescription();
		
		if(name.isEmpty())
			errors.rejectValue("name", "required");
		else if(name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH)
			errors.rejectValue("name", "size");
		
		if(color.isEmpty())
			errors.rejectValue("color", "required");
		else if(color.length() < MIN_COLOR_LENGTH || color.length() > MAX_COLOR_LENGTH)
			errors.rejectValue("color", "size");
		
		if(description.isEmpty())
			errors.rejectValue("description", "required");
		else if(description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH)
			errors.rejectValue("description", "size");
	}
	
	@Override
	public boolean supports(Class<?> tClass) {
		return Tag.class.equals(tClass); 
	}

}
