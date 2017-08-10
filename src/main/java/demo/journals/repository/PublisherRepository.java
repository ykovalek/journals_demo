package demo.journals.repository;

import demo.journals.model.Publisher;
import demo.journals.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    Optional<Publisher> findByUser(User user);

}
