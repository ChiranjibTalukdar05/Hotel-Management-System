import javax.swing.*;
import java.awt.*;

public class ServicesPanel extends JPanel {
    public ServicesPanel(Main main, JFrame frame) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Add Services for Guest", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 1, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JLabel roomLabel = new JLabel("Enter Room Number:");
        JTextField roomField = new JTextField();

        JCheckBox laundry = new JCheckBox("Laundry Service (₹500)");
        JCheckBox food = new JCheckBox("Food Service (₹800)");
        JCheckBox cleaning = new JCheckBox("Room Cleaning (₹300)");
        JCheckBox wifi = new JCheckBox("Wi-Fi (₹200)");

        JButton addBtn = new JButton("Add Services");

        form.add(roomLabel);
        form.add(roomField);
        form.add(laundry);
        form.add(food);
        form.add(cleaning);
        form.add(wifi);
        form.add(addBtn);
        add(form, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            try {
                int roomNo = Integer.parseInt(roomField.getText().trim());
                if (roomNo < 101 || roomNo > 110) {
                    JOptionPane.showMessageDialog(frame, "Invalid Room Number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int index = roomNo - 101;
                if (HotelData.roomAvailable[index]) {
                    JOptionPane.showMessageDialog(frame, "Room " + roomNo + " not booked!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder services = new StringBuilder();
                int totalServiceCost = 0;

                if (laundry.isSelected()) {
                    services.append("Laundry (₹500), ");
                    totalServiceCost += 500;
                }
                if (food.isSelected()) {
                    services.append("Food (₹800), ");
                    totalServiceCost += 800;
                }
                if (cleaning.isSelected()) {
                    services.append("Cleaning (₹300), ");
                    totalServiceCost += 300;
                }
                if (wifi.isSelected()) {
                    services.append("Wi-Fi (₹200), ");
                    totalServiceCost += 200;
                }

                if (services.length() == 0) {
                    JOptionPane.showMessageDialog(frame, "Select at least one service.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                services.setLength(services.length() - 2);
                HotelData.guestServices[index] = services.toString();

                // Save to DB
                HotelData.saveServicesToDB(roomNo, HotelData.guestServices[index]);

                // Refresh report & GUI
                HotelData.updateReport();

                JOptionPane.showMessageDialog(frame,
                        "Services added for Room " + roomNo + ":\n" + HotelData.guestServices[index]
                                + "\n\nTotal Service Cost: ₹" + totalServiceCost,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                roomField.setText("");
                laundry.setSelected(false);
                food.setSelected(false);
                cleaning.setSelected(false);
                wifi.setSelected(false);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid Room Number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> main.showPanel("Menu"));
        JPanel bottom = new JPanel();
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }
}
