import javax.swing.*;
import java.awt.*;

public class Main {
    JFrame frame;
    JPanel mainPanel;
    CardLayout cardLayout;

    public Main() {
        DatabaseConnection.getConnection(); // establish single connection

        HotelData.loadRoomsFromDB();        // load initial cache

        frame = new JFrame("Hotel Management System - hoteldb");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add panels
        mainPanel.add(new MenuPanel(this), "Menu");
        mainPanel.add(new ViewRoomPanel(this), "ViewRoom");
        mainPanel.add(new BookingPanel(this, frame), "Booking");
        mainPanel.add(new ServicesPanel(this, frame), "Services");
        mainPanel.add(new ReportPanel(this), "Report");
        mainPanel.add(new BillingPanel(this, frame), "Billing");
        mainPanel.add(new HistoryPanel(this), "History");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public void showPanel(String name) {
        if ("ViewRoom".equals(name)) {
            HotelData.loadRoomsFromDB();
            HotelData.updateRoomButtons();
        }
        if ("Report".equals(name)) {
            HotelData.updateReport();
        }
        if ("History".equals(name)) {
            HotelData.updateHistory();
        }
        cardLayout.show(mainPanel, name);
    }
}
