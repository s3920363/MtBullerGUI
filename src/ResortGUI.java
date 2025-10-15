import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ResortGUI extends JFrame {
    private final JTextArea output = new JTextArea(18, 80);
    private final JComboBox<String> typeFilterCombo = new JComboBox<>(new String[]{"All", "Hotel", "Apartment", "Lodge", "Cabin"});
    private final JTextField maxPriceField = new JTextField(7);

    //customers tab
    private final JTextField nameField = new JTextField(12);
    private final JTextField emailField = new JTextField(14);
    private final JComboBox<String> skillCombo = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Expert"});

    //packages tab
    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JComboBox<Accommodation> accommodationCombo = new JComboBox<>();
    private final JTextField dateField = new JTextField(10); // "YYYY-MM-DD" or "now"
    private final JTextField daysField = new JTextField(5);


    MtBullerResort resort = new MtBullerResort();

    public ResortGUI() {
        super("Mount Buller Resort GUI");

        resort.populateLists();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //tabs

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Accommodations", buildAccommodationsTab());
        tabs.addTab("Customers", buildCustomersTab());
        tabs.addTab("Packages", buildPackagesTab());


        //add components
        JScrollPane scroll = buildOutput();
        add(tabs, BorderLayout.NORTH);

        add(scroll, BorderLayout.CENTER);

        refreshCombos();
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

        JButton listAvaiBtn = new JButton("List Available");
        listAvaiBtn.addActionListener(e -> {
            output.setText("----- Available Accommodations -----\n");
            for (Accommodation a : resort.accommodations) {
                if (a.isAvailable()) {
                    output.append(a + "\n");
                }
            }
        });

        JButton filterTypeBtn = new JButton("Filter by Type");
        filterTypeBtn.addActionListener(e -> filterByType());

        JButton filterPriceBtn = new JButton("Filter by Price");
        filterPriceBtn.addActionListener(e -> filterByPrice());

        p.add(listAllBtn);
        p.add(listAvaiBtn);
        p.add(typeFilterCombo);
        p.add(filterTypeBtn);
        p.add(maxPriceField);
        p.add(filterPriceBtn);
        return p;
    }

    private Component buildCustomersTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton listBtn = new JButton("List Customers");
        listBtn.addActionListener(e -> {
            output.setText("----- Customers -----\n");
            for (Customer c : resort.customers) output.append(c + "\n");
        });

        JButton addBtn = new JButton("Add Customer");
        addBtn.addActionListener(this::handleAddCustomer);

        p.add(listBtn);
        p.add(new JLabel("Name:"));
        p.add(nameField);
        p.add(new JLabel("Email:"));
        p.add(emailField);
        p.add(new JLabel("Skill:"));
        p.add(skillCombo);
        p.add(addBtn);

        return p;
    }

    private Component buildPackagesTab() {
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton listBtn = new JButton("List Packages");
        listBtn.addActionListener(e -> listPackages());

        JButton createBtn = new JButton("Create Package");
        createBtn.addActionListener(e -> createPackage());

        p1.add(listBtn);
        p1.add(new JLabel("Customer:"));
        p1.add(customerCombo);
        p1.add(new JLabel("Accommodation:"));
        p1.add(accommodationCombo);

        p2.add(new JLabel("Date (YYYY-MM-DD or now):"));
        p2.add(dateField);
        p2.add(new JLabel("Days:"));
        p2.add(daysField);
        p2.add(createBtn);


        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // vertical stacking
        container.add(p1);
        container.add(p2);

        return container;


    }

    private void createPackage() {
    }

    private void listPackages() {
    }

    private void filterByPrice() {
        String txt = maxPriceField.getText().trim();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a max price.");
            return;
        }
        try {
            double max = Double.parseDouble(txt);
            if (max <= 0) throw new NumberFormatException();
            output.setText("----- Available Accommodations (Max $" + max + ") -----\n");
            int count = 0;
            for (Accommodation a : resort.accommodations) {
                if (a.isAvailable() && a.getPrice() <= max) {
                    output.append(a + "\n");
                    count++;
                }
            }
            if (count == 0) output.append("No accommodations found under $" + max + "\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid number greater than 0.");
        }
    }

    private void filterByType() {
        String sel = Objects.toString(typeFilterCombo.getSelectedItem(), "All");
        output.setText("----- Available Accommodations (" + sel + ") -----\n");
        int count = 0;
        for (Accommodation a : resort.accommodations) {
            boolean choice = sel.equalsIgnoreCase("All") || a.getType().equalsIgnoreCase(sel);
            if (choice && a.isAvailable()) {
                output.append(a + "\n");
                count++;
            }
        }
        if (count == 0) output.append("No accommodations found for type: " + sel + "\n");
    }

    private JScrollPane buildOutput() {
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        return new JScrollPane(output);
    }

    private void handleAddCustomer(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String skill = Objects.toString(skillCombo.getSelectedItem(), "").toLowerCase();

        if (name.isEmpty() || email.isEmpty() || skill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        if (!(skill.equals("beginner") || skill.equals("intermediate") || skill.equals("expert"))) {
            JOptionPane.showMessageDialog(this, "Skill level must be Beginner, Intermediate, or Expert.");
            return;
        }
        Customer c = new Customer(name, email, skill);
        resort.customers.add(c);
        output.setText("Customer added successfully:\n" + c + "\n");
        refreshCombos();
        nameField.setText("");
        emailField.setText("");
    }


    private void refreshCombos() {
        // Customers without a package
        DefaultComboBoxModel<Customer> custModel = new DefaultComboBoxModel<>();
        for (Customer c : resort.customers) if (!c.inPackage()) custModel.addElement(c);
        customerCombo.setModel(custModel);

        // Available accommodations
        DefaultComboBoxModel<Accommodation> accModel = new DefaultComboBoxModel<>();
        for (Accommodation a : resort.accommodations) if (a.isAvailable()) accModel.addElement(a);
        accommodationCombo.setModel(accModel);

    }

}