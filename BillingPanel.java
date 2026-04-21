import javax.swing.*;
import java.awt.*;

public class BillingPanel extends JPanel {
    public BillingPanel(Main main, JFrame frame) {
        setLayout(new BorderLayout(10, 10));
        JLabel label = new JLabel("Billing & Checkout", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        add(label, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(30, 80, 20, 80));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JLabel roomLabel = new JLabel("Enter Room Number:");
        roomLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JTextField roomField = new JTextField(15);
        roomField.setFont(new Font("Arial", Font.PLAIN, 16));
        roomField.setPreferredSize(new Dimension(200, 35));
        inputPanel.add(roomLabel);
        inputPanel.add(roomField);

        JButton calcBtn = new JButton("Calculate Bill");
        calcBtn.setFont(new Font("Arial", Font.BOLD, 15));
        calcBtn.setPreferredSize(new Dimension(190, 45));
        JPanel calcPanel = new JPanel();
        calcPanel.add(calcBtn);

        JTextArea resultArea = new JTextArea(12, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bill Summary"));
        JPanel resultPanel = new JPanel();
        resultPanel.add(scrollPane);

        center.add(inputPanel);
        center.add(Box.createVerticalStrut(10));
        center.add(calcPanel);
        center.add(Box.createVerticalStrut(10));
        center.add(resultPanel);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.setPreferredSize(new Dimension(120, 40));
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        bottom.add(checkoutBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // Calculate Bill (reads from DB)
        calcBtn.addActionListener(e -> {
            try {
                int roomNo = Integer.parseInt(roomField.getText().trim());
                if (roomNo < 101 || roomNo > 110) {
                    JOptionPane.showMessageDialog(frame, "Invalid Room Number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                HotelData.RoomInfo info = HotelData.getRoomInfo(roomNo);
                if (info.available) {
                    JOptionPane.showMessageDialog(frame, "Room " + roomNo + " is not booked!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int pricePerNight = (roomNo <= 104) ? 1800 : (roomNo <= 107 ? 2500 : 3500);
                int baseCost = pricePerNight * info.nights;

                int serviceCost = 0;
                StringBuilder serviceDetails = new StringBuilder();
                if (info.services != null) {
                    if (info.services.contains("Laundry")) {
                        serviceDetails.append("  • Laundry ........ ₹500\n");
                        serviceCost += 500;
                    }
                    if (info.services.contains("Food")) {
                        serviceDetails.append("  • Food Service ... ₹800\n");
                        serviceCost += 800;
                    }
                    if (info.services.contains("Cleaning")) {
                        serviceDetails.append("  • Room Cleaning .. ₹300\n");
                        serviceCost += 300;
                    }
                    if (info.services.contains("Wi-Fi")) {
                        serviceDetails.append("  • Wi-Fi .......... ₹200\n");
                        serviceCost += 200;
                    }
                }

                if (serviceDetails.length() == 0) serviceDetails.append("  (No additional services)\n");
                int total = baseCost + serviceCost;

                StringBuilder bill = new StringBuilder();
                bill.append("========================================\n");
                bill.append("            HOTEL BILLING SUMMARY\n");
                bill.append("========================================\n");
                bill.append("Guest Name  : " + info.guestName + "\n");
                bill.append("Contact     : " + info.contact + "\n");
                bill.append("Room Number : " + roomNo + "\n");
                bill.append("Nights Stay : " + info.nights + "\n");
                bill.append("Room Rate   : ₹" + pricePerNight + " per night\n");
                bill.append("----------------------------------------\n");
                bill.append("Room Cost   : ₹" + baseCost + "\n");
                bill.append("Services:\n" + serviceDetails);
                bill.append("----------------------------------------\n");
                bill.append("Service Cost: ₹" + serviceCost + "\n");
                bill.append("TOTAL AMOUNT: ₹" + total + "\n");
                bill.append("========================================\n");

                resultArea.setText(bill.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid Room Number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Checkout (inserts into guest_history and clears room)
        checkoutBtn.addActionListener(e -> {
            try {
                int roomNo = Integer.parseInt(roomField.getText().trim());
                HotelData.RoomInfo info = HotelData.getRoomInfo(roomNo);

                if (info.available) {
                    JOptionPane.showMessageDialog(frame, "This room is not occupied.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int pricePerNight = (roomNo <= 104) ? 1800 : (roomNo <= 107 ? 2500 : 3500);
                int baseCost = pricePerNight * info.nights;

                int serviceCost = 0;
                if (info.services != null) {
                    if (info.services.contains("Laundry")) serviceCost += 500;
                    if (info.services.contains("Food")) serviceCost += 800;
                    if (info.services.contains("Cleaning")) serviceCost += 300;
                    if (info.services.contains("Wi-Fi")) serviceCost += 200;
                }

                int total = baseCost + serviceCost;

                // Save history and clear room in DB
                HotelData.checkoutRoomInDB(roomNo, total);

                // Update caches
                int idx = roomNo - 101;
                HotelData.roomAvailable[idx] = true;
                HotelData.guestNames[idx] = "";
                HotelData.guestContacts[idx] = "";
                HotelData.guestNights[idx] = 0;
                HotelData.guestServices[idx] = null;

                HotelData.updateRoomButtons();
                HotelData.updateReport();
                HotelData.updateHistory();

                JOptionPane.showMessageDialog(frame,
                        "Checkout Successful! Total bill: ₹" + total,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                resultArea.setText("");
                roomField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid Room Number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> main.showPanel("Menu"));
    }
}
