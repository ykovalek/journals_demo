package demo.journals.rest;

import demo.journals.dto.SubscriptionDTO;
import demo.journals.model.Category;
import demo.journals.model.Journal;
import demo.journals.model.Publisher;
import demo.journals.model.Subscription;
import demo.journals.model.User;
import demo.journals.repository.CategoryRepository;
import demo.journals.repository.PublisherRepository;
import demo.journals.service.CurrentUser;
import demo.journals.service.JournalService;
import demo.journals.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/journals")
public class JournalRestController {

    private final PublisherRepository publisherRepository;
    private final JournalService journalService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    public JournalRestController(PublisherRepository publisherRepository, JournalService journalService,
            UserService userService, CategoryRepository categoryRepository) {
        this.publisherRepository = publisherRepository;
        this.journalService = journalService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> browse(@AuthenticationPrincipal Principal principal) {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        return ResponseEntity.ok(journalService.listAll(activeUser.getUser()));
    }

    @RequestMapping(value = "/published", method = RequestMethod.GET)
    public List<Journal> publishedList(@AuthenticationPrincipal Principal principal) {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        Optional<Publisher> publisher = publisherRepository.findByUser(activeUser.getUser());
        return journalService.publisherList(publisher.get());
    }

    @RequestMapping(value = "/unPublish/{id}", method = RequestMethod.DELETE)
    public void unPublish(@PathVariable("id") Long id, @AuthenticationPrincipal Principal principal) {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        Optional<Publisher> publisher = publisherRepository.findByUser(activeUser.getUser());
        journalService.unPublish(publisher.get(), id);
    }

    @RequestMapping(value = "/subscriptions")
    public List<SubscriptionDTO> getUserSubscriptions(@AuthenticationPrincipal Principal principal) {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        User persistedUser = userService.findById(activeUser.getId());
        List<Subscription> subscriptions = persistedUser.getSubscriptions();
        List<Category> categories = categoryRepository.findAll();
        List<SubscriptionDTO> subscriptionDTOs = new ArrayList<>(categories.size());
        categories.forEach(c -> {
            SubscriptionDTO subscr = new SubscriptionDTO(c);
            Optional<Subscription> subscription = subscriptions.stream()
                    .filter(s -> s.getCategory().getId().equals(c.getId())).findFirst();
            subscr.setActive(subscription.isPresent());
            subscriptionDTOs.add(subscr);
        });
        return subscriptionDTOs;
    }

    @RequestMapping(value = "/subscribe/{categoryId}", method = RequestMethod.POST)
    public void subscribe(@PathVariable("categoryId") Long categoryId, @AuthenticationPrincipal Principal principal) {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        User user = userService.findById(activeUser.getUser().getId());
        userService.subscribe(user, categoryId);
    }
}
