	package com.crg.mailservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crg.mailservice.model.Template;
import com.crg.mailservice.model.User;

public interface TemplateRepository extends JpaRepository<Template, Long> { 
	List<Template> findByCreatedBy(User user);

}

