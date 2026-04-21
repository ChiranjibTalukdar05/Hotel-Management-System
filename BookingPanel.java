import javax.swing.*;
import java.awt.*;

public class BookingPanel extends JPanel {
    public BookingPanel(Main main, JFrame frame) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Room Booking", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel nameLabel = new JLabel("Guest Name:");
        JTextField nameField = new JTextField();
        JLabel phoneLabel = new JLabel("Contact Number:");
        JTextField phoneField = new JTextField();
        JLabel roomLabel = new JLabel("Room Number:");
        JTextField roomField = new JTextField();
        JLabel nightsLabel = new JLabel("No. of Nights:");
        JTextField nightsField = new JTextField();
        JButton submitBtn = new JButton("Submit Booking");

        form.add(nameLabel); form.add(nameField);
        form.add(phoneLabel); form.add(phoneField);
        form.add(roomLabel); form.add(roomField);
        form.add(nightsLabel); form.add(nightsField);
        form.add(new JLabel()); form.add(submitBtn);
        add(form, BorderLayout.CENTER);

        submitBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String contact = phoneField.getText().trim();
                int roomNo = Integer.parseInt(roomField.getText().trim());
                int nights = Integer.parseInt(nightsField.getText().trim());

                if (name.isEmpty() || contact.isEmpty() || roomNo < 101 || roomNo > 110 || nights <= 0) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid booking details.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int index = roomNo - 101;
                if (!HotelData.roomAvailable[index]) {
                    JOptionPane.showMessageDialog(frame, "Room " + roomNo + " is already booked!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Save booking to DB (includes contact and checkin timestamp)
                HotelData.saveBookingToDB(roomNo, name, contact, nights);

                // Update caches
                HotelData.roomAvailable[index] = false;
                HotelData.guestNames[index] = name;
                HotelData.guestContacts[index] = contact;
                HotelData.guestNights[index] = nights;
                HotelData.guestServices[index] = "None";

                HotelData.updateRoomButtons();
                HotelData.updateReport();

                JOptionPane.showMessageDialog(frame,
                        "Booking Successful!\nGuest: " + name + "\nRoom: " + roomNo + "\nNights: " + nights,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                nameField.setText("");
                phoneField.setText("");
                roomField.setText("");
                nightsField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> main.showPanel("Menu"));
        JPanel bottom = new JPanel();
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }
}
