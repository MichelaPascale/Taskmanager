package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.validation.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.repository.CredentialsRepository;
import org.springframework.validation.Validator;


@Component
public class CredentialsValidator implements Validator{

	@Autowired
	CredentialsRepository credentialsRepository;


	final Integer MAX_USERNAME_LENGTH = 20;
	final Integer MIN_USERNAME_LENGTH = 4;
	final Integer MAX_PASSWORD_LENGTH = 20;
	final Integer MIN_PASSWORD_LENGTH = 6;

	@Override
	public void validate(Object o, Errors errors) {

		Credentials credentials = (Credentials) o;
		String userName = credentials.getUserName().trim();
		String password = credentials.getPassword().trim();

		// per i campi vuoti e lunghezze minori o superiori
		if(userName.isEmpty())  //.isBlank() ??
			errors.rejectValue("userName", "required");  // con messaggio di errrore "required"
		else if(userName.length() < MIN_USERNAME_LENGTH || userName.length() > MAX_USERNAME_LENGTH)
			errors.rejectValue("userName", "size");	
		else if(this.credentialsRepository.findByUserName(userName).isPresent())
			errors.rejectValue("userName", "duplicate");

		if(password.isEmpty())  //.isBlank() ??
			errors.rejectValue("password", "required");  // con messaggio di errrore "required"
		else if(password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH)
			errors.rejectValue("password", "size");

	}
	
	/*public void validateUsername(Object o, Errors errors) {

		Credentials credentials = (Credentials) o;
		String userName = credentials.getUserName().trim();

		// per i campi vuoti e lunghezze minori o superiori
		if(userName.isEmpty())  //.isBlank() ??
			errors.rejectValue("userName", "required");  // con messaggio di errrore "required"
		else if(userName.length() < MIN_USERNAME_LENGTH || userName.length() > MAX_USERNAME_LENGTH)
			errors.rejectValue("userName", "size");
		else if(!this.credentialsRepository.findByUserName(userName).isPresent())
			errors.rejectValue("userName", "notFind");
	}*/

	@Override
	// Specifica che la classe che andiamo ad utilizzare è la classe User
	public boolean supports(Class<?> aClass) {
		return Credentials.class.equals(aClass);
	}
}
