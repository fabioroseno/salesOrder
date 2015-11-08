package domain;

import entities.annotations.EntityDescriptor;
import entities.annotations.View;
import entities.annotations.Views;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Entity
@Views(
  @View(name = "Products",
       title = "Products",
     filters = "description,unitPrice",
     members = "description,unitPrice",
    template = "@CRUD+@PAGER+@FILTER"
        ,roles = "Admin"))
@EntityDescriptor(template="@TABLE+@CRUD+@PAGER")
public class Product implements Serializable {

    @Id @GeneratedValue
    private Integer id;

    @Column(length = 40, nullable = false, unique = true)
    @NotEmpty(message = "Enter the description of the Product")
    private String description;

    @Column(precision = 6, scale = 2)
    @Min(value = 0, message = "Enter a unit price greater than zero")    
    private double unitPrice;
    
    @Override
    public String toString() {
        return description;
    }
}
