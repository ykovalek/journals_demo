package demo.journals.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import demo.journals.Application;
import demo.journals.model.Journal;
import demo.journals.model.Publisher;
import demo.journals.model.User;
import demo.journals.repository.PublisherRepository;
import java.util.List;
import java.util.Optional;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JournalServiceTest {

    private final static String NEW_JOURNAL_NAME = "New Journal";

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    public void browseSubscribedUser() {
        List<Journal> journals = journalService.listAll(getUser("user1"));
        assertNotNull(journals);
        assertEquals(1, journals.size());

        assertEquals(new Long(1), journals.get(0).getId());
        assertEquals("Medicine", journals.get(0).getName());
        assertEquals(new Long(1), journals.get(0).getPublisher().getId());
        assertNotNull(journals.get(0).getPublishDate());
    }

    @Test
    public void browseUnSubscribedUser() {
        List<Journal> journals = journalService.listAll(getUser("user2"));
        assertEquals(0, journals.size());
    }

    @Test
    public void listPublisher() {
        User user = getUser("publisher1");
        Optional<Publisher> p = publisherRepository.findByUser(user);
        List<Journal> journals = journalService.publisherList(p.get());
        assertEquals(2, journals.size());

        assertEquals(new Long(1), journals.get(0).getId());
        assertEquals(new Long(2), journals.get(1).getId());

        assertEquals("Medicine", journals.get(0).getName());
        assertEquals("Test Journal", journals.get(1).getName());
        journals.forEach(j -> assertNotNull(j.getPublishDate()));
        journals.forEach(j -> assertEquals(new Long(1), j.getPublisher().getId()));

    }

    @Test(expected = ServiceException.class)
    public void publishFail() throws ServiceException {
        User user = getUser("publisher2");
        Optional<Publisher> p = publisherRepository.findByUser(user);

        Journal journal = new Journal();
        journal.setName("New Journal");

        journalService.publish(p.get(), journal, 1L);
    }

    @Test(expected = ServiceException.class)
    public void publishFail2() throws ServiceException {
        User user = getUser("publisher2");
        Optional<Publisher> p = publisherRepository.findByUser(user);

        Journal journal = new Journal();
        journal.setName("New Journal");

        journalService.publish(p.get(), journal, 150L);
    }

    @Test()
    public void publishSuccess() {
        User user = getUser("publisher2");
        Optional<Publisher> p = publisherRepository.findByUser(user);

        Journal journal = new Journal();
        journal.setName(NEW_JOURNAL_NAME);
        journal.setUuid("SOME_EXTERNAL_ID");
        try {
            journalService.publish(p.get(), journal, 3L);
        } catch (ServiceException e) {
            fail(e.getMessage());
        }

        List<Journal> journals = journalService.listAll(getUser("user1"));
        assertEquals(2, journals.size());

        journals = journalService.publisherList(p.get());
        assertEquals(2, journals.size());
        assertEquals(new Long(3), journals.get(0).getId());
        assertEquals(new Long(4), journals.get(1).getId());
        assertEquals("Health", journals.get(0).getName());
        assertEquals(NEW_JOURNAL_NAME, journals.get(1).getName());
        journals.forEach(j -> assertNotNull(j.getPublishDate()));
        journals.forEach(j -> assertEquals(new Long(2), j.getPublisher().getId()));
    }

    @Test(expected = ServiceException.class)
    public void unPublishFail() {
        User user = getUser("publisher1");
        Optional<Publisher> p = publisherRepository.findByUser(user);
        journalService.unPublish(p.get(), 4L);
    }

    @Test(expected = ServiceException.class)
    public void unPublishFail2() {
        User user = getUser("publisher1");
        Optional<Publisher> p = publisherRepository.findByUser(user);
        journalService.unPublish(p.get(), 100L);
    }

    @Test
    public void unPublishSuccess() {
        User user = getUser("publisher2");
        Optional<Publisher> p = publisherRepository.findByUser(user);
        journalService.unPublish(p.get(), 4L);

        List<Journal> journals = journalService.publisherList(p.get());
        assertEquals(1, journals.size());
        journals = journalService.listAll(getUser("user1"));
        assertEquals(1, journals.size());
    }

    private User getUser(String name) {
        Optional<User> user = userService.getUserByLoginName(name);
        if (!user.isPresent()) {
            fail("user1 doesn't exist");
        }
        return user.get();
    }

}
