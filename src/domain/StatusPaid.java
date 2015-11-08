package domain;

/**
 *
 * @author marcius.brandao
 */
public class StatusPaid implements IStatus {

    @Override
    public void accept(Order order) {
        throw new IllegalStateException("This Order is paid");
    }

    @Override
    public void pay(Order order) {
        throw new IllegalStateException("This Order is paid");
    }

    @Override
    public void reject(Order order) {
        throw new IllegalStateException("This Order is paid");
    }
}
