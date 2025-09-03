package com.crg.mailservice.exception;

//Example: ResourceNotFoundException.java
public class ResourceNotFoundException extends RuntimeException {
 public ResourceNotFoundException(String message) {
     super(message);
 }
}
