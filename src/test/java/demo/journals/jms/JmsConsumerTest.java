package demo.journals.jms;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import demo.journals.mail.JournalsMailSender;
import demo.journals.model.Update;
import demo.journals.repository.JournalRepository;
import demo.journals.repository.UpdateRepository;
import demo.journals.repository.UserRepository;
import com.google.common.collect.ImmutableList;
import demo.journals.util.JournalsTestUtil;
import javax.mail.MessagingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class JmsConsumerTest {

    @MockBean
    private JournalRepository journalRepository;

    @MockBean
    private UpdateRepository updateRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JournalsMailSender mailSender;

    private JmsConsumer jmsConsumer;

    @Before
    public void setUp() {
        given(journalRepository.findOne(0L)).willReturn(JournalsTestUtil.getJournal());
        jmsConsumer = new JmsConsumer(journalRepository, updateRepository, userRepository, mailSender);
    }

    @Test
    public void shouldSaveDigestUpdateToDb() {
        JournalUpdate update = new JournalUpdate(0L);
        jmsConsumer.prepareForDailyDigest(update);

        verify(updateRepository, times(1)).save(eq(new Update(JournalsTestUtil.getJournal())));
    }

    @Test
    public void shouldSendUpdateToSubscribers() throws MessagingException {
        given(userRepository.findAll()).willReturn(ImmutableList.of(JournalsTestUtil.getUser()));
        JournalUpdate update = new JournalUpdate(0L);

        jmsConsumer.notifySubscribers(update);

        verify(mailSender, times(1))
                .sendMail(Matchers.eq(ImmutableList.of(JournalsTestUtil.getJournal())), Matchers
                        .eq(ImmutableList.of(JournalsTestUtil.getUser().getEmail())));
    }

}
