package domain;

import entities.annotations.ActionDescriptor;
import entities.annotations.EntityDescriptor;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@EntityDescriptor(hidden = true)
public class OrderLine implements Serializable {

    @Id @GeneratedValue
    private Integer id;

    @ManyToOne(optional = false)
    @NotNull(message = "Enter the Order of the Line")
    private Order order;

    @ManyToOne(optional = false)    
    @NotNull(message = "Enter the Product of the Line")
    private Product product;

    @Column(precision = 6, scale = 2)
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private double price;

    @Min(value = 1, message = "Number Of Units must be at least one")
    @Column(precision = 4)
    private int numberOfUnits;

    @Transient
    @Column(precision = 8, scale = 2)
    private double totalAmount;
    
    @ActionDescriptor(displayName="Remove", image="image:trash")
    public void remove() {
        order.getLines().remove(this);
        order.setNumberOfItems(order.getNumberOfItems() - 1);
    }

    public double getTotalAmount() {
        return price * numberOfUnits;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
        
}