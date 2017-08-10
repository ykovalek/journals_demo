package demo.journals.util;

import demo.journals.model.Category;
import demo.journals.model.Journal;
import demo.journals.model.Publisher;
import demo.journals.model.Role;
import demo.journals.model.Subscription;
import demo.journals.model.Update;
import demo.journals.model.User;
import com.google.common.collect.ImmutableList;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class JournalsTestUtil {

    public static MimeMessage getMimeMessage() {
        return new MimeMessage((Session)null);
    }

    public static Update getUpdate() {
        return new Update(getJournal());
    }

    public static Journal getJournal() {
        Journal journal = new Journal();
        journal.setId(0L);
        journal.setName("Journal");
        journal.setCategory(getCategory());
        journal.setPublisher(getPublisher());
        return journal;
    }

    private static Publisher getPublisher() {
        Publisher publisher = new Publisher();
        publisher.setId(0L);
        publisher.setName("Publisher");
        publisher.setUser(getUser());
        return publisher;
    }

    public static User getUser() {
        User user = new User();
        user.setId(0L);
        user.setEmail("email");
        user.setEnabled(true);
        user.setLoginName("login");
        user.setRole(Role.USER);
        Subscription subscription = getSubscription();
        subscription.setUser(user);
        user.setSubscriptions(ImmutableList.of(subscription));
        return user;
    }

    private static Subscription getSubscription() {
        Subscription subscription = new Subscription();
        subscription.setId(0L);
        subscription.setCategory(getCategory());
        return subscription;
    }

    private static Category getCategory() {
        Category category = new Category();
        category.setId(0L);
        category.setName("Category");
        return category;
    }

}
