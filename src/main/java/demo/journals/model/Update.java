package demo.journals.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Update {

    @Id
    private long id;

    @OneToOne
    private Journal journal;

    public Update(Journal journal) {
        this.journal = journal;
    }

}
