package com.crg.mailservice.controller;

import com.crg.mailservice.model.MailingList;
import com.crg.mailservice.service.MailingListService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mailing-lists")
public class MailingListController {

    @Autowired
    private MailingListService mailingListService;

    @PostMapping("/create")
    public ResponseEntity<MailingList> createMailingList(@RequestBody MailingListRequest request) {
        MailingList mailingList = mailingListService.createMailingList(
                request.getName(), request.getContactIds(), request.getUserId());
        return ResponseEntity.ok(mailingList);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MailingList>> getMailingListsByUser(@PathVariable Long userId) {
        List<MailingList> lists = mailingListService.getMailingListsByUser(userId);
        return ResponseEntity.ok(lists);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMailingList(@PathVariable Long id) {
        mailingListService.deleteMailingList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MailingList> getMailingList(@PathVariable Long id) {
        return mailingListService.getMailingListById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{mailingListId}/contacts/{contactId}")
    public ResponseEntity<MailingList> addContactToMailingList(
            @PathVariable Long mailingListId,
            @PathVariable Long contactId) {
        MailingList updated = mailingListService.addContactToMailingList(mailingListId, contactId);
        return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<MailingList> updateMailingList(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description) {
        MailingList updatedList = mailingListService.updateMailingList(id, name, description);
        return ResponseEntity.ok(updatedList);
    }


}

// DTO for request body
@Data
class MailingListRequest {
    private String name;
    private List<Long> contactIds;
    private Long userId;
}
