package domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import lombok.Data;

import org.hibernate.validator.constraints.NotEmpty;

import entities.annotations.View;
import entities.annotations.Views;

@Data
@Entity
@NamedQueries({
    @NamedQuery(name = "CurrentDebtForCustomer",
               query = "Select sum(totalAmount)"
                     + "  From Order "
                     + " Where customer = :customer"
                     + "   and status = 'Accepted'")})
@Views(
  @View(name = "Customers",
       title = "Customers",
     filters = "name;                           "
             + "address.street;                 "
             + "address.town;                   "
             + "address.country;                "
             + "address.postalCode              ",
     members = "[number;name;                   "
             + " organizationNumber;creditLimit;"
             + " Address[address.street;        "
             + "         address.postalCode;    "
             + "         address.town;          "
             + "         address.country]       "
             + "]",
    template = "@CRUD_PAGE+@FILTER"
      ,roles = "Admin"))
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"number"})})
public class Customer implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 8)
    @NotEmpty(message = "Enter the number of the Customer")
    private String number;

    @NotEmpty(message = "Enter the name of the Customer")
    @Column(length = 40, nullable = false)
    private String name;

    @Column(length = 10)
    @NotEmpty(message = "Enter the Organization Number of the Customer")
    private String organizationNumber;

    @Min(0)
    @Column(precision = 8, scale = 2)
    private double creditLimit;

    @Valid
    @Embedded
    private Address address = new Address();

    @Version
    private Timestamp version;

    public boolean hasOKCreditLimit() {
        return CreditService.getMaxCredit(this) > creditLimit;
    }

    @Override
    public String toString() {
        return name + " [" + number + "]";
    }
}
