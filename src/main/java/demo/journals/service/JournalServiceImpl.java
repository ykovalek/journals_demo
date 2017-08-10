package demo.journals.service;

import demo.journals.controller.PublisherController;
import demo.journals.jms.JmsPublisher;
import demo.journals.model.Category;
import demo.journals.model.Journal;
import demo.journals.model.Publisher;
import demo.journals.model.Subscription;
import demo.journals.model.User;
import demo.journals.repository.CategoryRepository;
import demo.journals.repository.JournalRepository;
import demo.journals.repository.UserRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class JournalServiceImpl implements JournalService {

    private final static Logger log = Logger.getLogger(JournalServiceImpl.class);

    private final JournalRepository journalRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final JmsPublisher jmsPublisher;

    public JournalServiceImpl(JournalRepository journalRepository, UserRepository userRepository,
            CategoryRepository categoryRepository, JmsPublisher jmsPublisher) {
        this.journalRepository = journalRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.jmsPublisher = jmsPublisher;
    }

    @Override
    public List<Journal> listAll(User user) {
        User persistentUser = userRepository.findOne(user.getId());
        List<Subscription> subscriptions = persistentUser.getSubscriptions();
        if (subscriptions != null) {
            List<Long> ids = new ArrayList<>(subscriptions.size());
            subscriptions.forEach(s -> ids.add(s.getCategory().getId()));
            return journalRepository.findByCategoryIdIn(ids);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Journal> publisherList(Publisher publisher) {
        Iterable<Journal> journals = journalRepository.findByPublisher(publisher);
        return StreamSupport.stream(journals.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Journal publish(Publisher publisher, Journal journal, Long categoryId) throws ServiceException {
        Category category = categoryRepository.findOne(categoryId);
        if (category == null) {
            throw new ServiceException("Category not found");
        }
        journal.setPublisher(publisher);
        journal.setCategory(category);
        try {
            journal = journalRepository.save(journal);
            jmsPublisher.notifyNewJournalAdded(journal);
            return journal;
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void unPublish(Publisher publisher, Long id) throws ServiceException {
        Journal journal = journalRepository.findOne(id);
        if (journal == null) {
            throw new ServiceException("Journal doesn't exist");
        }
        String filePath = PublisherController.getFileName(publisher.getId(), journal.getUuid());
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.error("File " + filePath + " cannot be deleted");
            }
        }
        if (!journal.getPublisher().getId().equals(publisher.getId())) {
            throw new ServiceException("Journal cannot be removed");
        }
        journalRepository.delete(journal);
    }
}
