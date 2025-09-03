package com.crg.mailservice.service;

import com.crg.mailservice.exception.ResourceNotFoundException;
import com.crg.mailservice.model.Contact;
import com.crg.mailservice.model.MailingList;
import com.crg.mailservice.model.User;
import com.crg.mailservice.repository.ContactRepository;
import com.crg.mailservice.repository.MailingListRepository;
import com.crg.mailservice.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MailingListService {

    @Autowired
    private MailingListRepository mailingListRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

	/*
	 * public MailingList createMailingList(String name, List<Long> contactIds, Long
	 * userId) { List<Contact> contacts = contactRepository.findAllById(contactIds);
	 * User user = userRepository.findById(userId) .orElseThrow(() -> new
	 * RuntimeException("User not found"));
	 * 
	 * MailingList mailingList = MailingList.builder() .name(name)
	 * .contacts(contacts) .createdBy(user) .build();
	 * 
	 * return mailingListRepository.save(mailingList); }
	 */
    public MailingList createMailingList(String name, List<Long> contactIds, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contact> contacts = new ArrayList<>();
        if (contactIds != null && !contactIds.isEmpty()) {
            contacts = contactRepository.findAllById(contactIds);
        }

        MailingList mailingList = MailingList.builder()
                .name(name)
                .contacts(contacts)
                .createdBy(user)
                .createdAt(LocalDateTime.now()) // optional: if using timestamps
                .build();

        return mailingListRepository.save(mailingList);
    }

    public List<MailingList> getMailingListsByUser(Long userId) {
        return mailingListRepository.findByCreatedById(userId);
    }

    public Optional<MailingList> getMailingListById(Long id) {
        return mailingListRepository.findById(id);
    }

    public void deleteMailingList(Long id) {
        mailingListRepository.deleteById(id);
    }

    public Optional<MailingList> getByNameAndUserId(String name, Long userId) {
        return Optional.ofNullable(mailingListRepository.findByNameAndCreatedById(name, userId));
    }
    
    
    @Transactional
    public MailingList addContactToMailingList(Long mailingListId, Long contactId) {
    	MailingList mailingList = mailingListRepository.findById(mailingListId)
    		    .orElseThrow(() -> new RuntimeException("Mailing list not found with ID: " + mailingListId));


        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        mailingList.getContacts().add(contact);
        return mailingListRepository.save(mailingList);
    }
	/*
	 * @Transactional public MailingList updateMailingList(Long mailingListId,
	 * String newName, List<Long> newContactIds) { MailingList mailingList =
	 * mailingListRepository.findById(mailingListId) .orElseThrow(() -> new
	 * ResourceNotFoundException("Mailing list not found with ID: " +
	 * mailingListId));
	 * 
	 * if (newName != null && !newName.trim().isEmpty()) {
	 * mailingList.setName(newName); }
	 * 
	 * if (newContactIds != null) { List<Contact> updatedContacts =
	 * contactRepository.findAllById(newContactIds);
	 * mailingList.setContacts(updatedContacts); }
	 * 
	 * return mailingListRepository.save(mailingList); }
	 */
    public MailingList updateMailingList(Long mailingListId, String newName, String newDescription) {
        MailingList mailingList = mailingListRepository.findById(mailingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found with ID: " + mailingListId));

        mailingList.setName(newName);
        mailingList.setDescription(newDescription);
        return mailingListRepository.save(mailingList);
    }

    public List<Contact> getAvailableContacts(Long userId, Long mailingListId) {
        // Get all contacts of the user
    	
    	Optional<User> user=userRepository.findById(userId);
        List<Contact> userContacts = contactRepository.findByOwner(user.get());

        // Get contacts already in the mailing list
        MailingList mailingList = mailingListRepository.findById(mailingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));
        List<Contact> mailingListContacts = mailingList.getContacts();

        // Filter contacts not in the mailing list
        Set<Long> existingIds = mailingListContacts.stream()
                .map(Contact::getId)
                .collect(Collectors.toSet());

        return userContacts.stream()
                .filter(contact -> !existingIds.contains(contact.getId()))
                .collect(Collectors.toList());
    }


}
