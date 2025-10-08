import javax.swing.*;

public class MtBullerAdmin {
    public static void main(String[] args) {
        MtBullerResort mt = new MtBullerResort();
        mt.populateLists();
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
//        mt.run();
    }
}