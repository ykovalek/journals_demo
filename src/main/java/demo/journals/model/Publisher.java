package demo.journals.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Publisher {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(optional = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name;
}
