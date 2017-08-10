package demo.journals.jms;

import static demo.journals.util.JournalsTestUtil.getJournal;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class JmsPublsisherTest {

    @MockBean
    private JmsTemplate jmsTemplate;

    private JmsPublisher jmsPublisher;

    @Before
    public void setUp() {
        jmsPublisher = new JmsPublisher(jmsTemplate);
    }

    @Test
    public void shouldSendToMessageQueues() {
        jmsPublisher.notifyNewJournalAdded(getJournal());

        verify(jmsTemplate, times(2)).convertAndSend(any(String.class), any(JournalUpdate.class));
    }

}
