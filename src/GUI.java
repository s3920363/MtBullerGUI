import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;

public class GUI extends JFrame {
    private final MtBullerResort resort = new MtBullerResort();

    private final JTextArea output = new JTextArea(18, 80);

    // Customers tab
    private final JTextField nameField = new JTextField(12);
    private final JTextField emailField = new JTextField(14);
    private final JComboBox<String> skillCombo = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Expert"});

    // Packages tab (create)
    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JComboBox<Accommodation> accommodationCombo = new JComboBox<>();
    private final JTextField dateField = new JTextField(10); // "YYYY-MM-DD" or "now"
    private final JTextField daysField = new JTextField(5);

    // Extras tab (lift pass / lessons)
    private final JComboBox<TravelPackage> packageForPassCombo = new JComboBox<>();
    private final JRadioButton dailyPassBtn = new JRadioButton("Daily", true);
    private final JRadioButton seasonPassBtn = new JRadioButton("Season");
    private final JTextField passDaysField = new JTextField(5);

    private final JComboBox<TravelPackage> packageForLessonsCombo = new JComboBox<>();
    private final JTextField lessonsCountField = new JTextField(5);

    // Accommodations tab (filters)
    private final JComboBox<String> typeFilterCombo = new JComboBox<>(new String[]{"All", "Hotel", "Apartment", "Lodge", "Cabin"});
    private final JTextField maxPriceField = new JTextField(7);

    public GUI() {
        super("Mount Buller Resort – Simple Swing GUI");
        resort.populateLists();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Output area
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(output);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Accommodations", buildAccommodationsTab());
        tabs.addTab("Customers", buildCustomersTab());
        tabs.addTab("Packages", buildPackagesTab());
        tabs.addTab("Extras", buildExtrasTab());
        tabs.addTab("File", buildFileTab());

        add(tabs, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refreshAllCombos();
        setSize(1300, 600);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }

    private JPanel buildAccommodationsTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton listAllBtn = new JButton("List All");
        listAllBtn.addActionListener(e -> {
            output.setText("----- All Accommodations -----\n");
            for (Accommodation a : resort.accommodations) {
                output.append(a + "\n");
            }
        });

        JButton listAvailBtn = new JButton("List Available");
        listAvailBtn.addActionListener(e -> {
            output.setText("----- Available Accommodations -----\n");
            boolean any = false;
            for (Accommodation a : resort.accommodations) {
                if (a.isAvailable()) {
                    output.append(a + "\n");
                    any = true;
                }
            }
            if (!any) output.append("There is no available accommodation!\n");
        });

        JButton filterTypeBtn = new JButton("Filter by Type");
        filterTypeBtn.addActionListener(e -> doFilterByType());

        JButton filterPriceBtn = new JButton("Filter by Price");
        filterPriceBtn.addActionListener(e -> doFilterByPrice());

        p.add(listAllBtn);
        p.add(listAvailBtn);
        p.add(new JLabel("Type:"));
        p.add(typeFilterCombo);
        p.add(filterTypeBtn);
        p.add(new JLabel("Max $:"));
        p.add(maxPriceField);
        p.add(filterPriceBtn);
        return p;
    }

    private void doFilterByType() {
        String sel = Objects.toString(typeFilterCombo.getSelectedItem(), "All");
        output.setText("----- Available Accommodations (" + sel + ") -----\n");
        int count = 0;
        for (Accommodation a : resort.accommodations) {
            boolean okType = sel.equalsIgnoreCase("All") || a.getType().equalsIgnoreCase(sel);
            if (okType && a.isAvailable()) {
                output.append(a + "\n");
                count++;
            }
        }
        if (count == 0) output.append("No accommodations found for type: " + sel + "\n");
    }

    private void doFilterByPrice() {
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

    private JPanel buildCustomersTab() {
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

    private void handleAddCustomer(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String skill = Objects.toString(skillCombo.getSelectedItem(), "").toLowerCase();

        if (name.isEmpty() || email.isEmpty() || skill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        if (!email.contains("@")) {
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
        refreshAllCombos();
        nameField.setText("");
        emailField.setText("");
        skillCombo.setSelectedIndex(0);
    }

    private JPanel buildPackagesTab() {
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton listBtn = new JButton("List Packages");
        listBtn.addActionListener(e -> listPackagesToOutput());

        JButton createBtn = new JButton("Create Package");
        createBtn.addActionListener(e -> handleCreatePackage());

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

        // Combine both panels
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // vertical stacking
        container.add(p1);
        container.add(p2);

        return container;


    }

    private void handleCreatePackage() {
        Customer c = (Customer) customerCombo.getSelectedItem();
        Accommodation a = (Accommodation) accommodationCombo.getSelectedItem();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a customer.");
            return;
        }
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Select an accommodation.");
            return;
        }
        if (!a.isAvailable()) {
            JOptionPane.showMessageDialog(this, "Selected accommodation is not available.");
            return;
        }
        if (c.inPackage()) {
            JOptionPane.showMessageDialog(this, "Selected customer already has a package.");
            return;
        }

        LocalDate date;
        String dateTxt = dateField.getText().trim();
        if (dateTxt.equalsIgnoreCase("now") || dateTxt.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateTxt);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format.");
                return;
            }
        }
        int days;
        try {
            days = Integer.parseInt(daysField.getText().trim());
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a positive number of days.");
            return;
        }

        TravelPackage pkg = new TravelPackage(c, date, days);
        pkg.attachAccommodation(a);
        // Make effects visible immediately in UI
        a.setAvailable(false);
        c.setHasPackage();
        resort.packages.add(pkg);

        output.setText("Package created successfully!\n" + pkg + "\n");
        refreshAllCombos();
        dateField.setText("");
        daysField.setText("");
    }

    private JPanel buildExtrasTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Group pass radio buttons
        ButtonGroup passGroup = new ButtonGroup();
        passGroup.add(dailyPassBtn);
        passGroup.add(seasonPassBtn);

        JButton addPassBtn = new JButton("Add Lift Pass");
        addPassBtn.addActionListener(e -> handleAddPass());

        JButton addLessonsBtn = new JButton("Add Lessons");
        addLessonsBtn.addActionListener(e -> handleAddLessons());

        p.add(new JLabel("Lift Pass → Package:"));
        p.add(packageForPassCombo);
        p.add(dailyPassBtn);
        p.add(new JLabel("Days:"));
        p.add(passDaysField);
        p.add(seasonPassBtn);
        p.add(addPassBtn);

        p.add(new JSeparator(SwingConstants.VERTICAL));

        p.add(new JLabel("Lessons → Package:"));
        p.add(packageForLessonsCombo);
        p.add(new JLabel("Count:"));
        p.add(lessonsCountField);
        p.add(addLessonsBtn);
        return p;
    }

    private void handleAddPass() {
        TravelPackage pkg = (TravelPackage) packageForPassCombo.getSelectedItem();
        if (pkg == null) {
            JOptionPane.showMessageDialog(this, "Select a package.");
            return;
        }
        if (pkg.getHasLiftPass()) {
            JOptionPane.showMessageDialog(this, "This package already has a Lift Pass.");
            return;
        }

        LiftPass pass;
        if (seasonPassBtn.isSelected()) {
            pass = new LiftPass("Season", 0);
        } else {
            int days;
            try {
                days = Integer.parseInt(passDaysField.getText().trim());
                if (days <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number of days.");
                return;
            }
            pass = new LiftPass("Daily", days);
        }
        pkg.setLiftPass(pass);
        output.setText("Lift pass added successfully!\n" + pkg + "\n");
        refreshAllCombos();
        passDaysField.setText("");
    }

    private void handleAddLessons() {
        TravelPackage pkg = (TravelPackage) packageForLessonsCombo.getSelectedItem();
        if (pkg == null) {
            JOptionPane.showMessageDialog(this, "Select a package.");
            return;
        }
        if (pkg.getHasLessons()) {
            JOptionPane.showMessageDialog(this, "This package already has Lessons.");
            return;
        }

        int count;
        try {
            count = Integer.parseInt(lessonsCountField.getText().trim());
            if (count <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a positive lessons count.");
            return;
        }
        String level = pkg.getCustomer().getSkillLevel();
        Lessons lessons = new Lessons(level, count);
        pkg.setLessons(lessons);
        output.setText("Lessons added successfully!\n" + pkg + "\n");
        refreshAllCombos();
        lessonsCountField.setText("");
    }

    private JPanel buildFileTab() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton saveBtn = new JButton("Save Packages...");
        saveBtn.addActionListener(e -> doSave());

        JButton loadBtn = new JButton("Load Packages...");
        loadBtn.addActionListener(e -> doLoad());

        JButton listBtn = new JButton("List Packages");
        listBtn.addActionListener(e -> listPackagesToOutput());

        p.add(saveBtn);
        p.add(loadBtn);
        p.add(listBtn);
        return p;
    }

    private void doSave() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save packages to .dat");
        fc.setFileFilter(new FileNameExtensionFilter("Data Files (*.dat)", "dat"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".dat")) {
                f = new File(f.getParentFile(), f.getName() + ".dat");
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(new ArrayList<>(resort.packages));
                output.setText("Packages saved to " + f.getName() + "\n");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doLoad() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load packages from .dat");
        fc.setFileFilter(new FileNameExtensionFilter("Data Files (*.dat)", "dat"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "No such file.");
                return;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                resort.packages = (ArrayList<TravelPackage>) ois.readObject();
                // Reconcile customers/accommodations
                for (TravelPackage pkg : resort.packages) {
                    Customer c = resort.searchCustomerByID(pkg.getCustomer().getID());
                    if (c != null) {
                        c.setHasPackage();
                    } else {
                        Customer newC = pkg.getCustomer();
                        newC.setHasPackage();
                        resort.customers.add(newC);
                    }
                    Accommodation a = resort.searchAccommodationByID(pkg.getAccommodation().getID());
                    if (a != null) a.setAvailable(false);
                }
                output.setText("Packages loaded from " + f.getName() + "\n");
                listPackagesToOutput();
                refreshAllCombos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
            }
        }
    }

    private void listPackagesToOutput() {
        if (resort.packages.isEmpty()) {
            output.setText("There are no packages.\n");
        } else {
            output.setText("----- Packages -----\n");
            for (TravelPackage p : resort.packages) output.append(p + "\n");
        }
    }

    private void refreshAllCombos() {
        // Customers without a package
        DefaultComboBoxModel<Customer> custModel = new DefaultComboBoxModel<>();
        for (Customer c : resort.customers) if (!c.inPackage()) custModel.addElement(c);
        customerCombo.setModel(custModel);

        // Available accommodations
        DefaultComboBoxModel<Accommodation> accModel = new DefaultComboBoxModel<>();
        for (Accommodation a : resort.accommodations) if (a.isAvailable()) accModel.addElement(a);
        accommodationCombo.setModel(accModel);

        // Packages needing pass
        DefaultComboBoxModel<TravelPackage> passModel = new DefaultComboBoxModel<>();
        for (TravelPackage p : resort.packages) if (!p.getHasLiftPass()) passModel.addElement(p);
        packageForPassCombo.setModel(passModel);

        // Packages needing lessons
        DefaultComboBoxModel<TravelPackage> lessonsModel = new DefaultComboBoxModel<>();
        for (TravelPackage p : resort.packages) if (!p.getHasLessons()) lessonsModel.addElement(p);
        packageForLessonsCombo.setModel(lessonsModel);
    }
}