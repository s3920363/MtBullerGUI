import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResortGUI extends JFrame implements ActionListener {
    private final JTextArea output = new JTextArea(18, 80);
    MtBullerResort resort = new MtBullerResort();

    public ResortGUI() {
        super("Mount Buller Resort GUI");

        resort.populateLists();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //tabs

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Accommodations", buildAccommodationsTab());

        //add components
        JScrollPane scroll = buildOutput();
        add(tabs, BorderLayout.NORTH);

        add(scroll, BorderLayout.CENTER);


        setSize(1300, 700);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public static void main(String[] args) {
        new ResortGUI();
    }

    private Component buildAccommodationsTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton listAllBtn = new JButton("List All");
        listAllBtn.addActionListener(e -> {
            output.setText("----- All Accommodations -----\n");
            for (Accommodation a : resort.accommodations) {
                output.append(a + "\n");
            }
        });

        p.add(listAllBtn);
        return p;
    }

    private JScrollPane buildOutput() {
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        return new JScrollPane(output);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}