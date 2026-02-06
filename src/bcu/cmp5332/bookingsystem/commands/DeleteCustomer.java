package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.CustomerDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

/**
 * Command to soft-delete a customer from the system.
 * Requirement: 70-79% - Remove (hide) customers using soft delete.
 */
public class DeleteCustomer implements Command {

    private final int customerId;
    private final CustomerDataManager customerDataManager;

    /**
     * Creates a new DeleteCustomer command.
     * 
     * @param customerId the ID of the customer to delete
     * @param customerDataManager the data manager for persistence
     */
    public DeleteCustomer(int customerId, CustomerDataManager customerDataManager) {
        this.customerId = customerId;
        this.customerDataManager = customerDataManager;
    }

    /**
     * Executes the soft delete by marking the customer as deleted.
     * 
     * @param fbs the flight booking system
     * @throws FlightBookingSystemException if customer not found or other error
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Customer customer = fbs.getCustomerByID(customerId);
        
        if (customer.isDeleted()) {
            throw new FlightBookingSystemException("Customer is already deleted.");
        }
        
        // Soft delete - just mark as deleted
        customer.setDeleted(true);
        
        System.out.println("Customer #" + customerId + " (" + customer.getName() + ") has been deleted (hidden from system).");
        
        // Save changes immediately
        try {
            customerDataManager.storeData(fbs);
        } catch (IOException e) {
            // Rollback the delete
            customer.setDeleted(false);
            throw new FlightBookingSystemException("Error saving customer deletion: " + e.getMessage());
        }
    }
}
