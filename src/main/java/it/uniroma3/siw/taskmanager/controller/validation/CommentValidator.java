package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Comment;

@Component
public class CommentValidator implements Validator {

	final Integer MIN_COMMENT_LENGTH = 6;
	final Integer MAX_COMMENT_LENGTH = 1000;

	@Override
	public void validate(Object o, Errors e) {
		
		Comment comment = (Comment) o;
		String c = comment.getComment();

		if(c.isEmpty())
			e.rejectValue("comment", "required");
		else if(c.length() < MIN_COMMENT_LENGTH || c.length() > MAX_COMMENT_LENGTH)
			e.rejectValue("comment", "size");
	}

	@Override
	public boolean supports(Class<?> cClass) {
		return Comment.class.equals(cClass);
	}

}
