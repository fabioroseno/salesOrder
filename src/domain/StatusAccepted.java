package domain;

import entities.Repository;

/**
 *
 * @author marcius.brandao
 */
public class StatusAccepted implements IStatus {

    @Override
    public void accept(Order order) {
        throw new IllegalStateException("call Accept() only for new Orders");
    }

    @Override
    public void pay(Order order) {
        order.setStatus(Status.Paid);
        Repository.save(order);
    }

    @Override
    public void reject(Order order) {
        order.setStatus(Status.Canceled);
        Repository.save(order);
    }
}
