import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicLookAndFeel;

import com.bulenkov.darcula.DarculaLaf;

import gui.Gui;

public class Application {
    public static void main(String[] args) {
        try {
            BasicLookAndFeel darcula = new DarculaLaf();
            UIManager.setLookAndFeel(darcula);
        } catch (Exception e) {
        }
        Gui gui = new Gui();
        gui.createGUI();
    }
}
