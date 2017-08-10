package demo.journals.jms;

import demo.journals.model.Journal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JmsPublisher {

    @Value("${jms.general-notification}")
    private String GENERAL_QUEUE;

    @Value("${jms.subscriber-notification}")
    private String SUBSCRIBER_QUEUE;

    private final JmsTemplate jmsTemplate;

    JmsPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void notifyNewJournalAdded(Journal journal) {
        log.info(String
                .format("Sending journal %s (id = %s) to notification queues", journal.getName(), journal.getId()));
        jmsTemplate.convertAndSend(GENERAL_QUEUE, new JournalUpdate(journal.getId()));
        jmsTemplate.convertAndSend(SUBSCRIBER_QUEUE, new JournalUpdate(journal.getId()));
    }
}
