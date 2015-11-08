package domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Address implements Serializable {

    @Column(length = 40, nullable = false)
    private String street;

    @Column(length = 8, nullable = false)
    private String postalCode;

    @Column(length = 20, nullable = false)
    private String town;

    @Column(length = 20, nullable = false)
    private String country;

    @Override
    public String toString() {
        return street + "," + town + " (" + postalCode + ")";
    }
}
