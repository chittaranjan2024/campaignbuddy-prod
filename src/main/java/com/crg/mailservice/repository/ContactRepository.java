package com.crg.mailservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crg.mailservice.model.Contact;
import com.crg.mailservice.model.User;

public interface ContactRepository extends JpaRepository<Contact, Long> { 
	
	 List<Contact> findByOwner(User owner);
	 
	 List<Contact> findByOwnerAndSubscribedTrue(User user);
	 List<Contact> findByOwnerAndSubscribedFalse(User user);
	 
	 List<Contact> findBySubscribedTrue();
	 List<Contact> findBySubscribedFalse();

	    
	 

}
