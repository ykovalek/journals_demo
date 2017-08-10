package demo.journals.dto;

import demo.journals.model.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDTO {

    private long id;

    private String name;

    private boolean active;

    public SubscriptionDTO(Category c) {
        name = c.getName();
        id = c.getId();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SubscriptionDTO that = (SubscriptionDTO) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
