package demo.journals.repository;

import demo.journals.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLoginName(String loginName);

}
