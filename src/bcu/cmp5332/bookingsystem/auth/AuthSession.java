package bcu.cmp5332.bookingsystem.auth;

import bcu.cmp5332.bookingsystem.model.Customer;

public class AuthSession {

    private final Role role;
    private final Customer customer;

    public AuthSession(Role role, Customer customer) {
        this.role = role;
        this.customer = customer;
    }

    public Role getRole() {
        return role;
    }

    public Customer getCustomer() {
        return customer;
    }
}
