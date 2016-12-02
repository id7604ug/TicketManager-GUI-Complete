import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/*
 *
 */
public class Main {
    public static void main(String[] args){
        LinkedList<Ticket> ticketQueue = new LinkedList<>(); // List for open tickets
        LinkedList<Ticket> resolvedTickets = new LinkedList<>(); // List for resolved tickets
        try(BufferedReader readQueue = new BufferedReader(new FileReader("src/main/queueFile.txt"))) {
            readFile(ticketQueue, readQueue);
        } catch (IOException e){
            System.out.println("There was a problem reading the queueFile.txt file");
        }
            TicketManagerGUI gui = new TicketManagerGUI(ticketQueue, resolvedTickets);

    }

    // Method to read data from a file
    private static void readFile(LinkedList<Ticket> ticketQueue, BufferedReader readQueue) throws IOException {
        Ticket newTic = new Ticket("", 0, "", new Date()); // Create temp ticket to set the start ticket ID counter
        newTic.setStaticTicketIDCounter(Integer.parseInt(readQueue.readLine()));
        String readLine = readQueue.readLine();
        while(true){
            String description;
            int priority;
            String rep;
            Date date;
            if (readLine == null){
                break;
            } else {
                String[] readLineList = readLine.split(";");
                Date importDate = new Date();
                Ticket t = new Ticket(readLineList[1], Integer.parseInt(readLineList[2]), readLineList[3], new Date(), Integer.parseInt(readLineList[0]));
                ticketQueue.add(t); // Add ticket to ticket queue
                readLine = readQueue.readLine();
            }

        }
    }


}
