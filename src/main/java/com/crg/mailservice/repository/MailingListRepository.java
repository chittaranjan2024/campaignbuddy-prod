package com.crg.mailservice.repository;

import com.crg.mailservice.model.MailingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailingListRepository extends JpaRepository<MailingList, Long> {

    List<MailingList> findByCreatedById(Long userId);

    MailingList findByNameAndCreatedById(String name, Long userId);
    
    @Query("SELECT m FROM MailingList m JOIN FETCH m.contacts WHERE m.id = :id")
    Optional<MailingList> findByIdWithContacts(@Param("id") Long id);

}
