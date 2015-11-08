package domain;

/**
 *
 * @author marcius.brandao
 */
public interface IStatus {

    void accept(Order order);

    void pay(Order order);

    void reject(Order order);
}
