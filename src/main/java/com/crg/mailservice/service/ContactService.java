package com.crg.mailservice.service;

import org.springframework.stereotype.Service;

import com.crg.mailservice.model.Contact;
import com.crg.mailservice.repository.ContactRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact toggleSubscription(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // Flip the subscription status
        contact.setSubscribed(!contact.isSubscribed());

        return contactRepository.save(contact);
    }

}
