import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelData {
    // GUI caches (helpful for view)
    public static boolean[] roomAvailable = new boolean[10];
    public static JButton[] roomButtons = new JButton[10];
    public static String[] guestNames = new String[10];
    public static int[] guestNights = new int[10];
    public static String[] guestServices = new String[10];
    public static String[] guestContacts = new String[10];

    public static JTextArea reportArea;
    public static JTextArea historyArea;

    // Load rooms from DB into caches
    public static void loadRoomsFromDB() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms ORDER BY room_no")) {

            int i = 0;
            while (rs.next() && i < 10) {
                roomAvailable[i] = rs.getObject("available") == null ? true : rs.getBoolean("available");
                guestNames[i] = rs.getString("guest_name") == null ? "" : rs.getString("guest_name");
                guestContacts[i] = rs.getString("contact") == null ? "" : rs.getString("contact");
                guestNights[i] = rs.getObject("nights") == null ? 0 : rs.getInt("nights");
                guestServices[i] = rs.getString("services");
                i++;
            }
            updateRoomButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save booking (with contact + checkin_date = NOW())
    public static void saveBookingToDB(int roomNo, String guest, String contact, int nights) {
        String sql = "UPDATE rooms SET guest_name=?, contact=?, nights=?, services=?, available=?, checkin_date=NOW() WHERE room_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, guest);
            ps.setString(2, contact);
            ps.setInt(3, nights);
            ps.setString(4, "None");
            ps.setBoolean(5, false);
            ps.setInt(6, roomNo);
            ps.executeUpdate();
            System.out.println("Booking saved to DB for room " + roomNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save services for a room
    public static void saveServicesToDB(int roomNo, String services) {
        String sql = "UPDATE rooms SET services=? WHERE room_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, services);
            ps.setInt(2, roomNo);
            ps.executeUpdate();
            System.out.println("Services saved to DB for room " + roomNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get live room info
    public static RoomInfo getRoomInfo(int roomNo) {
        RoomInfo info = new RoomInfo();
        String sql = "SELECT * FROM rooms WHERE room_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info.guestName = rs.getString("guest_name");
                    info.contact = rs.getString("contact");
                    info.nights = rs.getObject("nights") == null ? 0 : rs.getInt("nights");
                    info.services = rs.getString("services");
                    info.available = rs.getObject("available") == null ? true : rs.getBoolean("available");
                    info.checkin = rs.getTimestamp("checkin_date");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    // Checkout: insert history, then clear rooms row
    public static void checkoutRoomInDB(int roomNo, int totalBill) {
        String selectSql = "SELECT guest_name, contact, nights, services, checkin_date FROM rooms WHERE room_no=?";
        String insertHistory = "INSERT INTO guest_history(guest_name, contact, room_no, nights, services, total_bill, checkin_date, checkout_date) VALUES(?,?,?,?,?,?,?,NOW())";
        String clearRoom = "UPDATE rooms SET guest_name=NULL, contact=NULL, nights=NULL, services=NULL, available=TRUE, checkin_date=NULL WHERE room_no=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(selectSql)) {

            psSelect.setInt(1, roomNo);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    String guest = rs.getString("guest_name");
                    String contact = rs.getString("contact");
                    int nights = rs.getObject("nights") == null ? 0 : rs.getInt("nights");
                    String services = rs.getString("services");
                    Timestamp checkin = rs.getTimestamp("checkin_date");

                    try (PreparedStatement psInsert = conn.prepareStatement(insertHistory)) {
                        psInsert.setString(1, guest);
                        psInsert.setString(2, contact);
                        psInsert.setInt(3, roomNo);
                        psInsert.setInt(4, nights);
                        psInsert.setString(5, services);
                        psInsert.setInt(6, totalBill);
                        psInsert.setTimestamp(7, checkin);
                        psInsert.executeUpdate();
                    }
                }
            }

            // Clear the room
            try (PreparedStatement psClear = conn.prepareStatement(clearRoom)) {
                psClear.setInt(1, roomNo);
                psClear.executeUpdate();
            }
            System.out.println("Checkout done and history recorded for room " + roomNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update room buttons based on current cached state
    public static void updateRoomButtons() {
        for (int i = 0; i < 10; i++) {
            if (roomButtons[i] == null) continue;
            if (roomAvailable[i]) {
                roomButtons[i].setEnabled(true);
                roomButtons[i].setBackground(Color.GREEN);
            } else {
                roomButtons[i].setEnabled(false);
                roomButtons[i].setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    // Update the current rooms report (reads directly from DB)
    public static void updateReport() {
        if (reportArea == null) return;
        StringBuilder report = new StringBuilder();
        report.append("📋 HOTEL REPORT\n\n");
        report.append(String.format("%-10s %-15s %-10s %-25s\n", "Room", "Guest", "Nights", "Services"));
        report.append("---------------------------------------------------------------\n");

        String sql = "SELECT * FROM rooms ORDER BY room_no";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int roomNo = rs.getInt("room_no");
                boolean available = rs.getObject("available") == null ? true : rs.getBoolean("available");
                if (available) {
                    report.append(String.format("%-10s %-15s %-10s %-25s\n", roomNo, "Available", "-", "-"));
                } else {
                    report.append(String.format("%-10s %-15s %-10d %-25s\n",
                            roomNo,
                            rs.getString("guest_name"),
                            rs.getObject("nights") == null ? 0 : rs.getInt("nights"),
                            rs.getString("services")));
                }
            }
            reportArea.setText(report.toString());
        } catch (Exception e) {
            e.printStackTrace();
            reportArea.setText("Error generating report.");
        }
    }

    // Update guest history area
    public static void updateHistory() {
        if (historyArea == null) return;
        StringBuilder rep = new StringBuilder();
        rep.append("📜 GUEST HISTORY\n\n");
        rep.append(String.format("%-5s %-15s %-12s %-6s %-20s %-10s %-20s\n",
                "ID", "Guest", "Contact", "Room", "Services", "Bill", "Checkout"));
        rep.append("------------------------------------------------------------------------------------\n");

        String sql = "SELECT history_id, guest_name, contact, room_no, nights, services, total_bill, checkout_date FROM guest_history ORDER BY history_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rep.append(String.format("%-5d %-15s %-12s %-6d %-20s %-10d %-20s\n",
                        rs.getInt("history_id"),
                        rs.getString("guest_name"),
                        rs.getString("contact"),
                        rs.getInt("room_no"),
                        rs.getString("services"),
                        rs.getInt("total_bill"),
                        rs.getTimestamp("checkout_date").toString()));
            }
            historyArea.setText(rep.toString());
        } catch (Exception e) {
            e.printStackTrace();
            historyArea.setText("Error reading history.");
        }
    }

    // Helper class to return live info
    public static class RoomInfo {
        public String guestName;
        public String contact;
        public int nights;
        public String services;
        public boolean available = true;
        public Timestamp checkin;
    }
}
