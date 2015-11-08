package domain;

import entities.Repository;

public class TotalCreditService {

    static public double getCurrentCredit(Customer customer) {
        Double credit;
        credit = (Double) Repository.query("CurrentDebtForCustomer", customer).get(0);
        if (credit == null) {
            credit = 0d;
        }
        return credit;
    }

    static public double getCurrentCredit(Order orderToExclude) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
