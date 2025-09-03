package com.crg.mailservice.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crg.mailservice.model.Contact;
import com.crg.mailservice.model.MailingList;
import com.crg.mailservice.model.User;
import com.crg.mailservice.repository.ContactRepository;
import com.crg.mailservice.repository.MailingListRepository;
import com.crg.mailservice.repository.UserRepository;
import com.crg.mailservice.service.ContactService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final MailingListRepository mailingListRepository;
    
    private final ContactService contactService;

   
    // Create contact
    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return ResponseEntity.ok(contactRepository.save(contact));
    }

    // Get all contacts
    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactRepository.findAll());
    }

    // Get contact by ID
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        return contactRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update contact
    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact updatedContact) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(updatedContact.getName());
            contact.setEmail(updatedContact.getEmail());
            contact.setOwner(updatedContact.getOwner());
            return ResponseEntity.ok(contactRepository.save(contact));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        if (!contactRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        contactRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Get contacts by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Contact>> getContactsByUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<Contact> contacts = contactRepository.findByOwner(user);
                    return ResponseEntity.ok(contacts);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    @GetMapping("/not-in-mailinglist/{userId}/{mailingListId}")
    public ResponseEntity<List<Contact>> getContactsNotInMailingList(
            @PathVariable Long userId,
            @PathVariable Long mailingListId) {

        // Step 1: Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Fetch mailing list
        MailingList mailingList = mailingListRepository.findById(mailingListId)
                .orElseThrow(() -> new RuntimeException("Mailing list not found"));

        // Step 3: Fetch all contacts for the user
        List<Contact> userContacts = contactRepository.findByOwnerAndSubscribedTrue(user);

        // Step 4: Extract IDs of contacts already in the mailing list
        Set<Long> mailingListContactIds = mailingList.getContacts()
                .stream()
                .map(Contact::getId)
                .collect(Collectors.toSet());

        // Step 5: Filter out contacts already in the mailing list
        List<Contact> filteredContacts = userContacts.stream()
                .filter(contact -> !mailingListContactIds.contains(contact.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredContacts);
    }
    
    
    @PutMapping("/{id}/subscribe")
    public ResponseEntity<Contact> subscribe(@PathVariable Long id) {
        Contact contact = contactRepository.findById(id).orElseThrow();
        contact.setSubscribed(true);
        return ResponseEntity.ok(contactRepository.save(contact));
    }

    @PutMapping("/{id}/unsubscribe")
    public ResponseEntity<Contact> unsubscribe(@PathVariable Long id) {
        Contact contact = contactRepository.findById(id).orElseThrow();
        contact.setSubscribed(false);
        return ResponseEntity.ok(contactRepository.save(contact));
    }
    
    // ✅ Get all subscribed contacts
    @GetMapping("/subscribers/{id}")
    public ResponseEntity<List<Contact>> getSubscribers(@PathVariable Long id) {
    	
    	   // Step 1: Fetch user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(contactRepository.findByOwnerAndSubscribedTrue(user));
    }

    // ✅ Get all unsubscribed contacts
    @GetMapping("/unsubscribers/{id}")
    public ResponseEntity<List<Contact>> getUnsubscribers(@PathVariable("id") Long id) {

 	   // Step 1: Fetch user
     User user = userRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(contactRepository.findByOwnerAndSubscribedFalse(user));
    }
    
 // Toggle subscription status
    @PutMapping("/{id}/toggle-subscription")
    public ResponseEntity<Contact> toggleSubscription(@PathVariable Long id) {
        Contact updatedContact = contactService.toggleSubscription(id);
        return ResponseEntity.ok(updatedContact);
    }

}

