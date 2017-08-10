package demo.journals.jms;

import com.google.common.collect.ImmutableList;
import demo.journals.mail.JournalsMailSender;
import demo.journals.model.Journal;
import demo.journals.model.Subscription;
import demo.journals.model.Update;
import demo.journals.model.User;
import demo.journals.repository.JournalRepository;
import demo.journals.repository.UpdateRepository;
import demo.journals.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class JmsConsumer {

    private final JournalRepository journalRepository;
    private final UpdateRepository updateRepository;
    private final UserRepository userRepository;
    private final JournalsMailSender mailSender;

    JmsConsumer(JournalRepository journalRepository, UpdateRepository updateRepository,
            UserRepository userRepository, JournalsMailSender mailSender) {
        this.journalRepository = journalRepository;
        this.updateRepository = updateRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @JmsListener(destination = "${jms.general-notification}")
    @Transactional
    void prepareForDailyDigest(JournalUpdate update) {
        log.info(String.format("Received update re: journal id = %s", update.getJournalId()));
        Optional<Journal> journal = Optional.ofNullable(journalRepository.findOne(update.getJournalId()));
        journal.ifPresent(j -> updateRepository.save(new Update(j)));
    }

    @JmsListener(destination = "${jms.subscriber-notification}")
    @Transactional
    void notifySubscribers(JournalUpdate update) throws MessagingException {
        log.info(String.format("Received update re: journal id = %s", update.getJournalId()));
        Journal journal = journalRepository.findOne(update.getJournalId());
        if (journal != null) {
            sendToSubscribers(journal);
        } else {
            log.info(String.format("Journal with id=%s not found", update.getJournalId()));
        }
    }

    private void sendToSubscribers(Journal journal) throws MessagingException {
        log.info(String.format("Fetching the list of subscribers to category: %s", journal.getCategory().getName()));
        List<String> subscribers = userRepository.findAll().stream().
                filter(u -> u.getSubscriptions().stream().map(Subscription::getCategory)
                        .anyMatch(s -> s.equals(journal.getCategory()))).map(User::getEmail)
                .collect(Collectors.toList());
        mailSender.sendMail(ImmutableList.of(journal), subscribers);
    }

}
