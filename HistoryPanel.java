import javax.swing.*;
import java.awt.*;

public class HistoryPanel extends JPanel {
    public HistoryPanel(Main main) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Guest History", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.NORTH);

        HotelData.historyArea = new JTextArea();
        HotelData.historyArea.setEditable(false);
        HotelData.historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(HotelData.historyArea);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton refresh = new JButton("Refresh");
        JButton back = new JButton("Back to Menu");
        bottom.add(refresh);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        refresh.addActionListener(e -> HotelData.updateHistory());
        back.addActionListener(e -> main.showPanel("Menu"));
    }
}
