package it.uniroma3.siw.taskmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.repository.CredentialsRepository;

@Service
public class CredentialsService {

	@Autowired
	private CredentialsRepository credentialsRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// SAVE DELLE CREDENZIALI CON ENCODE DELLA PASSWORD

	@Transactional
	public Credentials saveCredentials(Credentials c) {
		c.setRole(Credentials.DEFAULT_ROLE);  // setto le credenziali al ruolo di default -> se unno volesse promuovere un utente lo andrà a modificare direttamente col DB
		// password encode
		c.setPassword(this.passwordEncoder.encode(c.getPassword()));
		return this.credentialsRepository.save(c);
	}

	// RICERCA DELLE CREDENZIALI

	@Transactional
	public Credentials getCredentials(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credentials getCredentialsByUserName(String userName) {
		Optional<Credentials> result = this.credentialsRepository.findByUserName(userName);
		return result.orElse(null);
	}

	// LISTA DI TUTTE LE CREDENZIALI
	@Transactional
	public List<Credentials> getAllCredentials() {
		List<Credentials> all = new ArrayList<>();
		Iterable<Credentials> i = this.credentialsRepository.findAll();	 
		for(Credentials credential : i) {
			all.add(credential);
		}
		return all;
	}

	// RIMUOVI CREDENZIALI DATO LO USERNAME

	@Transactional
	public void deleteCredentials(String userName) {
		this.credentialsRepository.delete(this.getCredentialsByUserName(userName));
	}


}
