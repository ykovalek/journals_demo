package demo.journals.mail;

import static demo.journals.util.JournalsTestUtil.getMimeMessage;
import static demo.journals.util.JournalsTestUtil.getUpdate;
import static demo.journals.util.JournalsTestUtil.getUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import demo.journals.repository.UpdateRepository;
import demo.journals.repository.UserRepository;
import com.google.common.collect.ImmutableList;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class JournalsMailSenderTest {

    @MockBean
    private UpdateRepository updateRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender mailSender;

    private JournalsMailSender journalsMailSender;

    @Before
    public void setUp() {
        given(mailSender.createMimeMessage()).willReturn(getMimeMessage());

        journalsMailSender = new JournalsMailSender(updateRepository, userRepository, mailSender);
    }

    @Test
    public void shouldSendDailyDigestWhenUpdatesAvailable() throws MessagingException {
        given(updateRepository.findAll()).willReturn(ImmutableList.of(getUpdate()));
        given(userRepository.findAll()).willReturn(ImmutableList.of(getUser()));

        journalsMailSender.sendDailyDigest();

        verify(updateRepository, times(1)).findAll();
        verify(userRepository, times(1)).findAll();
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void shouldNotSendDailyDigestWhenUpdatesNotAvailable() throws MessagingException {
        given(updateRepository.findAll()).willReturn(ImmutableList.of());
        given(userRepository.findAll()).willReturn(ImmutableList.of(getUser()));

        journalsMailSender.sendDailyDigest();

        verify(updateRepository, times(1)).findAll();
        verify(userRepository, times(1)).findAll();
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

}
