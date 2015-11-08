package domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import entities.annotations.ActionDescriptor;
import entities.annotations.EntityDescriptor;
import entities.annotations.View;
import entities.annotations.Views;

@Data
@Entity
@Table(name = "ORDERS")
@EntityDescriptor(template="@FORM_CRUD")
@EqualsAndHashCode(of = {"id", "number"})
@Views({
    /**
     * Order soberano estilo
     */
    @View(title = "Orders",
         name = "Orders",
         filters="number;customer",
      members = "["
              + "number;customer;orderDate;*numberOfItems;"
              + "lines<product,numberOfUnits,price,remove()>;"
              + "*totalAmount;status;*version;"
              + "[addLine(),accept(),pay(),reject()]]",
      template="@CRUD_PAGE+@FILTER"
        ,roles = "Admin"),
    /*
     *
     */
  @View(title = "Add Order",
         name = "AddOrder",
      members = "["
              + "Header[#number,#orderDate;#customer:2];"
              + " Lines[addLine();"
              + "       lines<[#product:3;"
              + "             #numberOfUnits,#price,#remove()]>;"
              + "       *totalAmount];"
              + "accept()]",
   namedQuery = "Select new domain.Order()"
        ,roles = "Admin,Salesman"),
  /**
   *
   */
  @View(title = "List of Orders",
         name = "ListOfOrders",
      filters = "customer;orderDate;totalAmount",
      members = "number,customer,orderDate,numberOfItems,totalAmount,status",       
     template = "@FILTER+@PAGER",
   namedQuery = "from Order order by number",         
        roles = "LOGGED")
})
public class Order implements Serializable {

    private static final int MAX_TOTAL_AMOUNT_ORDER = 1000;

    @Id @GeneratedValue
    private Integer id;

    @Length(max = 8)
    @Column(length = 8, unique = true, nullable = false)
    @NotEmpty(message = "Enter the number of the Order")
    private String number;

    @ManyToOne(optional = false)
    @NotNull(message = "Enter the customer of the Order")
    private Customer customer;

    @Past
    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @Column(precision = 4)
    @Min(value = 1, message = "Enter at least one line")
    private Integer numberOfItems = 0;

    @Valid
    @OneToMany(mappedBy = "order",
                cascade = CascadeType.ALL,
          orphanRemoval = true)
    private List<OrderLine> lines;

    @Formula("(select sum(ol.numberofunits*ol.price)"
           + "  from orderline ol where ol.order_id = id)")
    @Column(precision = 8, scale = 2)
    private Double totalAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Version
    private Timestamp version;

    public Order() {
        status = Status.NewOrder;
        orderDate = new Date();
        lines = new ArrayList<OrderLine>();
    }

    public void addLine() {
        OrderLine orderLine = new OrderLine();
        orderLine.setOrder(this);
        lines.add(orderLine);
        numberOfItems++;
    }

    @ActionDescriptor(refreshView = true)
    public String accept() {
        status.accept(this);
        return "Accepted order";
    }

    public String pay() {
        status.pay(this);
        return "Paid order";
    }

    public String reject() {        
        status.reject(this);
        return "Canceled order";
    }

    protected boolean isOkAccordingToSize() {
        totalAmount = 0d;
        for (OrderLine orderLine : lines) {
            totalAmount += orderLine.getTotalAmount();
        }
        return totalAmount <= MAX_TOTAL_AMOUNT_ORDER;
    }

    protected boolean isOkAccordingToCreditLimit() {
        if (this.customer == null) {
            throw new IllegalArgumentException("Enter the customer of the Order");
        }
        double currentCredit = TotalCreditService.getCurrentCredit(this.customer);
        return currentCredit + totalAmount <= this.customer.getCreditLimit();
    }
    
    @Override
    public String toString() {
        return "Order " + number;
    }
}
