import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    public MenuPanel(Main main) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Hotel Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // Updated: Grid now has 6 rows instead of 5
        JPanel buttonsPanel = new JPanel(new GridLayout(6, 1, 12, 12));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(30, 120, 30, 120));

        JButton viewRoomBtn = new JButton("View Room");
        JButton bookingBtn = new JButton("Booking");
        JButton servicesBtn = new JButton("Add Services");
        JButton reportBtn = new JButton("View Report");
        JButton billingBtn = new JButton("Billing");
        JButton historyBtn = new JButton("Guest History");

        // Keep original order + add new button at the end
        buttonsPanel.add(viewRoomBtn);
        buttonsPanel.add(bookingBtn);
        buttonsPanel.add(servicesBtn);
        buttonsPanel.add(reportBtn);
        buttonsPanel.add(billingBtn);
        buttonsPanel.add(historyBtn);

        add(buttonsPanel, BorderLayout.CENTER);

        viewRoomBtn.addActionListener(e -> {
            HotelData.loadRoomsFromDB();
            HotelData.updateRoomButtons();
            main.showPanel("ViewRoom");
        });
        bookingBtn.addActionListener(e -> main.showPanel("Booking"));
        servicesBtn.addActionListener(e -> main.showPanel("Services"));
        reportBtn.addActionListener(e -> {
            HotelData.updateReport();
            main.showPanel("Report");
        });
        billingBtn.addActionListener(e -> main.showPanel("Billing"));
        historyBtn.addActionListener(e -> {
            HotelData.updateHistory();
            main.showPanel("History");
        });
    }
}
