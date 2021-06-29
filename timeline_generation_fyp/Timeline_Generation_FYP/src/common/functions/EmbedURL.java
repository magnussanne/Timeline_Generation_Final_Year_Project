package common.functions;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JComponent;

public class EmbedURL {
    public static void embed(String url, JComponent object) {
        object.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        object.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                try {

                    Desktop.getDesktop().browse(new URI(url));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

        });
    }
}
