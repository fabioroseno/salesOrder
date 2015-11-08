package domain;

import entities.Repository;

/**
 *
 * @author marcius.brandao
 */
public class StatusNewOrder implements IStatus {

    @Override
    public void accept(Order order) {
        if (!order.isOkAccordingToSize()) {
            throw new IllegalStateException("Total amount greater than limit");
        }

        if (!order.isOkAccordingToCreditLimit()) {
            throw new IllegalStateException("Credit limit exceeded");
        }

        order.setStatus(Status.Accepted);
        Repository.save(order);
    }

    @Override
    public void pay(Order order) {
        throw new IllegalStateException("call pay() only for accepted Orders");
    }

    @Override
    public void reject(Order order) {
        throw new IllegalStateException("This is a new Order");
    }
}
