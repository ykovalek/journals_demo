package demo.journals.controller;

import demo.journals.model.Category;
import demo.journals.model.Journal;
import demo.journals.model.Subscription;
import demo.journals.model.User;
import demo.journals.repository.JournalRepository;
import demo.journals.repository.UserRepository;
import demo.journals.service.CurrentUser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JournalController {

    private final JournalRepository journalRepository;
    private final UserRepository userRepository;

    public JournalController(JournalRepository journalRepository, UserRepository userRepository) {
        this.journalRepository = journalRepository;
        this.userRepository = userRepository;
    }

    @ResponseBody
    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET, produces = "application/pdf")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity renderDocument(@AuthenticationPrincipal Principal principal, @PathVariable("id") Long id)
            throws IOException {
        Journal journal = journalRepository.findOne(id);
        Category category = journal.getCategory();
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        //At this point, @PreAuthorize gurantees that user exists
        User user = userRepository.findOne(activeUser.getUser().getId());
        List<Subscription> subscriptions = user.getSubscriptions();
        Optional<Subscription> subscription = subscriptions.stream()
                .filter(s -> s.getCategory().getId().equals(category.getId())).findFirst();
        if (subscription.isPresent() || journal.getPublisher().getId().equals(user.getId())) {
            File file = new File(PublisherController.getFileName(journal.getPublisher().getId(), journal.getUuid()));
            InputStream in = new FileInputStream(file);
            return ResponseEntity.ok(IOUtils.toByteArray(in));
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
