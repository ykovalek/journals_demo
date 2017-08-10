package demo.journals.service;

import demo.journals.model.Category;
import demo.journals.model.Subscription;
import demo.journals.model.User;
import demo.journals.repository.CategoryRepository;
import demo.journals.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public UserServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<User> getUserByLoginName(String loginName) {
        return Optional.ofNullable(userRepository.findByLoginName(loginName));
    }

    @Override
    public void subscribe(User user, Long categoryId) {
        List<Subscription> subscriptions;
        subscriptions = user.getSubscriptions();
        if (subscriptions == null) {
            subscriptions = new ArrayList<>();
        }
        Optional<Subscription> subscr = subscriptions.stream()
                .filter(s -> s.getCategory().getId().equals(categoryId)).findFirst();
        if (!subscr.isPresent()) {
            Subscription s = new Subscription();
            s.setUser(user);
            Category category = categoryRepository.findOne(categoryId);
            if (category == null) {
                throw new ServiceException("Category not found");
            }
            s.setCategory(category);
            subscriptions.add(s);
            userRepository.save(user);
        }
    }

    @Override
    public User findById(Long id) {
        return userRepository.findOne(id);
    }

}
