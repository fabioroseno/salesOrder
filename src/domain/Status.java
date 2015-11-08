package domain;

public enum Status implements IStatus {

    NewOrder(new StatusNewOrder()),
    Accepted(new StatusAccepted()),
    Paid(new StatusPaid()),
    Canceled(new StatusCanceled());
    private IStatus status;

    Status(IStatus status) {
        this.status = status;
    }

    @Override
    public void accept(Order order) {
        status.accept(order);
    }

    @Override
    public void pay(Order order) {
        status.pay(order);
    }

    @Override
    public void reject(Order order) {
        status.reject(order);
    }
}
