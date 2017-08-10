package demo.journals.service;

import demo.journals.model.Journal;
import demo.journals.model.Publisher;
import demo.journals.model.User;
import java.util.List;

public interface JournalService {

    List<Journal> listAll(User user);

    List<Journal> publisherList(Publisher publisher);

    Journal publish(Publisher publisher, Journal journal, Long categoryId);

    void unPublish(Publisher publisher, Long journalId);
}
