package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.*;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, FlightBookingSystemException {

        FlightBookingSystem fbs = new FlightBookingSystem();

        // ✅ Initialize DataManagers
        FlightDataManager flightDM = new FlightDataManager();
        CustomerDataManager customerDM = new CustomerDataManager();
        BookingDataManager bookingDM = new BookingDataManager();

        // ✅ Load existing data (if any)
        try {
            flightDM.loadData(fbs);
            customerDM.loadData(fbs);
            bookingDM.loadData(fbs);
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }

        // ✅ Set DataManagers in CommandParser
        CommandParser.setDataManagers(flightDM, customerDM, bookingDM);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Flight Booking System");
        System.out.println("Enter 'help' to see a list of available commands.");

        while (true) {
            System.out.print("> ");
            String line = br.readLine();
            if (line.equals("exit")) {
                break;
            }

            try {
                Command command = CommandParser.parse(line);
                command.execute(fbs);
            } catch (FlightBookingSystemException ex) {
                System.out.println(ex.getMessage());
            }
        }

        // ✅ Save all data on exit
        try {
            flightDM.storeData(fbs);
            customerDM.storeData(fbs);
            bookingDM.storeData(fbs);
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }

        System.exit(0);
    }
}
