package demo.journals.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "subscription")
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private Date date;

    @ManyToOne(optional = false)
    private Category category;

    @PrePersist
    private void onPersist() {
        date = new Date();
    }
}
