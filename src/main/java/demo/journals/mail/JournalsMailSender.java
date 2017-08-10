package demo.journals.mail;

import demo.journals.model.Journal;
import demo.journals.model.Update;
import demo.journals.model.User;
import demo.journals.repository.UpdateRepository;
import demo.journals.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JournalsMailSender {

    private final UpdateRepository updateRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    JournalsMailSender(UpdateRepository updateRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.updateRepository = updateRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "${cron.daily-digest}")
    @Transactional
    @Retryable(value = {Throwable.class}, maxAttempts = 23, backoff = @Backoff(delay = 60 * 60 * 1000))
    void sendDailyDigest() throws MessagingException {
        log.info("Attempting to send daily digest by email to all users");
        Collection<Update> updates = updateRepository.findAll();
        Collection<Journal> journals = updates.stream().map(Update::getJournal).collect(Collectors.toList());
        List<String> emails = userRepository.findAll().stream().filter(User::getEnabled).map(User::getEmail)
                .collect(Collectors.toList());
        if (!updates.isEmpty() && !emails.isEmpty()) {
            sendMail(journals, emails);
            clearUpdates(updates);
        }
    }

    public void sendMail(Collection<Journal> updates, List<String> emails) throws MessagingException {
        log.info(String.format("Sending updates about %s to %s", updates, emails));
        MimeMessage mail = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(emails.toArray(new String[]{}));
            helper.setReplyTo("someone@journals.local");
            helper.setFrom("someone@journals.local");
            helper.setSubject("New journals have been added to our library");
            helper.setText(createMessageBody(updates));
            log.info("Sending mail to recipients");
            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Unable to send email message", e);
            throw e;
        }
    }

    private void clearUpdates(Collection<Update> updates) {
        log.info("Clearing the updates table");
        updates.forEach(updateRepository::delete);
    }

    private String createMessageBody(Collection<Journal> updates) {
        StringBuffer message = new StringBuffer();
        message.append("New additions to our library: \n\n");
        updates.forEach(update -> message.append(
                String.format("Title: %s, Category: %s\n", update.getName(), update.getCategory())));
        message.append("\nCheck them out on our website!\n\n");
        message.append("Best Regards,\n");
        message.append("Journals Team");
        return message.toString();
    }

}
