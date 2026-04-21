import javax.swing.*;
import java.awt.*;

public class ViewRoomPanel extends JPanel {
    public ViewRoomPanel(Main main) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Room Availability & Prices", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.NORTH);

        JPanel roomsPanel = new JPanel(new GridLayout(2, 5, 12, 12));
        roomsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create 10 room cards (101..110)
        for (int i = 0; i < 10; i++) {
            int roomNo = 101 + i;
            int price = (roomNo <= 104) ? 1800 : (roomNo <= 107 ? 2500 : 3500);

            JPanel single = new JPanel(new BorderLayout());
            JButton btn = new JButton("Room " + roomNo);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            HotelData.roomButtons[i] = btn;

            final int idx = i;
            btn.addActionListener(e -> {
                // Use cached values for fast UI; for absolutely latest, call HotelData.getRoomInfo(roomNo)
                boolean available = HotelData.roomAvailable[idx];
                String guest = HotelData.guestNames[idx] == null ? "" : HotelData.guestNames[idx];
                String contact = HotelData.guestContacts[idx] == null ? "" : HotelData.guestContacts[idx];
                int nights = HotelData.guestNights[idx];
                String services = HotelData.guestServices[idx] == null ? "None" : HotelData.guestServices[idx];

                StringBuilder msg = new StringBuilder();
                msg.append("Room ").append(roomNo).append("\n");
                msg.append("Price: ₹").append(price).append(" per night\n\n");

                if (available) {
                    msg.append("Status: AVAILABLE\n");
                    msg.append("You can book this room from the Booking panel.");
                } else {
                    msg.append("Status: BOOKED\n");
                    msg.append("Guest : ").append(guest).append("\n");
                    if (!contact.isEmpty()) msg.append("Contact: ").append(contact).append("\n");
                    msg.append("Nights: ").append(nights).append("\n");
                    msg.append("Services: ").append(services).append("\n");
                }

                JOptionPane.showMessageDialog(this,
                        msg.toString(),
                        "Room " + roomNo + " Details",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            JLabel priceLabel = new JLabel("₹" + price + " / night", SwingConstants.CENTER);
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            single.add(btn, BorderLayout.CENTER);
            single.add(priceLabel, BorderLayout.SOUTH);
            roomsPanel.add(single);
        }

        add(roomsPanel, BorderLayout.CENTER);

        // Available Rooms button (shows room number + price)
        JButton availableBtn = new JButton("Show Available Rooms");
        availableBtn.setFont(new Font("Arial", Font.BOLD, 15));
        availableBtn.addActionListener(e -> {
            StringBuilder availableList = new StringBuilder();
            availableList.append("Available Rooms:\n\n");

            for (int i = 0; i < 10; i++) {
                if (HotelData.roomAvailable[i]) {
                    int roomNo = 101 + i;
                    int price;
                    if (roomNo <= 104) price = 1800;
                    else if (roomNo <= 107) price = 2500;
                    else price = 3500;
                    availableList.append("Room ").append(roomNo).append(" – ₹").append(price).append("\n");
                }
            }

            if (availableList.toString().trim().equals("Available Rooms:")) {
                availableList.append("No rooms are available.");
            }

            JOptionPane.showMessageDialog(this,
                    availableList.toString(),
                    "Available Rooms",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Bottom panel with available button and Back button
        JPanel bottom = new JPanel();
        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> main.showPanel("Menu"));
        bottom.add(availableBtn);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }
}
