import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

/*
 *
 */
public class TicketManagerGUI extends JFrame{
    private JPanel rootPanel;
    private JTextField tbxDescription;
    private JSpinner spinnerPriority;
    private JTextField tbxReporter;
    private JButton btnAddTicket;
    private JList lstTicketList;
    private JButton btnDeleteTicket;
    private JTextField tbxResolution;
    private JButton btnDelete;
    private DefaultListModel listModel = new DefaultListModel();

    TicketManagerGUI(final LinkedList<Ticket> ticketQueue, final LinkedList<Ticket> resolvedTickets){
        setContentPane(rootPanel);
        pack();
        setSize(1280, 720); // Sets window size
        setTitle("Ticket Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateListElement(ticketQueue);


        btnAddTicket.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTickets(ticketQueue, resolvedTickets);
                updateListElement(ticketQueue);
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int deleteTicketID;
                deleteTicket(ticketQueue, resolvedTickets, lstTicketList.getSelectedIndex(), tbxResolution.getText());
                writeFile(resolvedTickets, ticketQueue);
                updateListElement(ticketQueue);
            }
        });
    }

    private void updateListElement(LinkedList<Ticket> ticketQueue) {
        listModel.clear();
        LinkedList<String> listOfTickets = new LinkedList<>();
        for (Ticket t:ticketQueue) {
            listModel.add(listModel.size(), t.toString());
        }
        lstTicketList.setModel(listModel);
    }
    // Method to add tickets
    private void addTickets(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> resolvedtickets) {
        Ticket t = new Ticket(tbxDescription.getText(), (Integer) spinnerPriority.getValue(), tbxReporter.getText(), new Date());
        addTicketInPriorityOrder(ticketQueue, t);
        writeFile(resolvedtickets, ticketQueue);
    }

    // Method to add tickets in priority order
    private static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket){
        //Logic: assume the list is either empty or sorted
        if (tickets.size() == 0 ) {//Special case - if list is empty, add ticket and return
            tickets.add(newTicket);
            return;
        }

        //Tickets with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire)
        //Tickets with the LOWEST value of their priority number (so the lowest priority) go at the end

        int newTicketPriority = newTicket.getPriority();

        for (int x = 0; x < tickets.size() ; x++) {    //use a regular for loop so we know which element we are looking at

            //if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= tickets.get(x).getPriority()) {
                tickets.add(x, newTicket);
                return;
            }
        }

        //Will only get here if the ticket is not added in the loop
        //If that happens, it must be lower priority than all other tickets. So, add to the end.
        tickets.addLast(newTicket);
    }

    // Method to write out the programs data to files
    private static void writeFile(LinkedList<Ticket> resolvedTickets, LinkedList<Ticket> ticketQueue) {
        try {
            // http://docs.oracle.com/javase/7/docs/api/java/util/Date.html
            File writeFileName = new File("Resolved_tickets_as_of_"
                    + Calendar.MONTH + "_" + Calendar.DAY_OF_MONTH + "_" + Calendar.YEAR + ".txt");
            BufferedWriter writeResolvedTickets = new BufferedWriter((new FileWriter(writeFileName)));
            for (Ticket t : resolvedTickets) {
                writeResolvedTickets.write(t.toString() + "\n"); // Write data line by line
            }
            writeResolvedTickets.close(); // Close writer
            // ---------
            File writeFileName2 = new File("src/main/queueFile.txt"); // File name and location
            BufferedWriter writeQueueFile = new BufferedWriter(new FileWriter(writeFileName2));
            writeQueueFile.write(ticketQueue.get(ticketQueue.size() - 1).getStaticTicketIDCounter() + "\n"); // Writes last staticTicketIDCounter
            for (Ticket t2 : ticketQueue) {
                writeQueueFile.write(t2.getTicketID() + ";" + t2.getDescription() + ";" + t2.getPriority()
                        + ";" + t2.getReporter() + ";" + t2.getDateReported() + ";" + t2.getResolutionDate() + ";"
                        + t2.getResolutionDescription() + "\n"); // Writes each ticket to a new line in the file
            }
            writeQueueFile.close();
        } catch (IOException e) {
            System.out.println("There was a problem writing data to the file.");
        }
    }

    // Method to take two lists of tickets and delete the queried ID from the ticket query
    private static void deleteTicket(LinkedList<Ticket> ticketQueue, LinkedList<Ticket> resolvedTickets, int deleteID, String resolutionString) {
        //Loop over all tickets. Delete the one with the ticket ID
        boolean found = false;
        for (Ticket ticket : ticketQueue) {
            if (ticket.getTicketID() == deleteID) {
                ticket.setResolutionDescription(resolutionString); // Set resolution of removed ticket
                ticket.setResolutionDate(new Date()); // Set resolution to today's date
                resolvedTickets.add(ticket); // Add resolved ticket to list
                ticketQueue.remove(ticket);
                System.out.println(String.format("Ticket %d deleted", deleteID));
                break; // No need to loop any more.
            }
        }
    }


}
