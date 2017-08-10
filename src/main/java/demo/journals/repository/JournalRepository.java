package demo.journals.repository;

import demo.journals.model.Journal;
import demo.journals.model.Publisher;
import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface JournalRepository extends CrudRepository<Journal, Long> {

    Collection<Journal> findByPublisher(Publisher publisher);

    List<Journal> findByCategoryIdIn(List<Long> ids);

}
