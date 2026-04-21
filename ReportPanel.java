import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {
    public ReportPanel(Main main) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Hotel Report - Current Rooms", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.NORTH);

        HotelData.reportArea = new JTextArea();
        HotelData.reportArea.setEditable(false);
        HotelData.reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(HotelData.reportArea);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refresh = new JButton("Refresh");
        JButton back = new JButton("Back to Menu");
        bottom.add(refresh);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        refresh.addActionListener(e -> HotelData.updateReport());
        back.addActionListener(e -> main.showPanel("Menu"));
    }
}
