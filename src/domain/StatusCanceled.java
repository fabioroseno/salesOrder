package domain;

/**
 *
 * @author marcius.brandao
 */
public class StatusCanceled implements IStatus {

    @Override
    public void accept(Order order) {
        throw new IllegalStateException("This Order is canceled");
    }

    @Override
    public void pay(Order order) {
        throw new IllegalStateException("This Order is canceled");
    }

    @Override
    public void reject(Order order) {
        throw new IllegalStateException("This Order is canceled");
    }
}
