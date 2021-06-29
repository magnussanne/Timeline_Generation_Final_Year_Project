package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import common.entities.EventInput;
import common.functions.UploadCSV;
import timeline_template.TimeLinePlot;

public class Gui {
    private static JTextArea textArea;
    private JTextField titleTF = new JTextField(20);
    private String title;

    public void createGUI() {
        JFrame frame = new JFrame("Timeline Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        menu.add(file);
        JMenuItem save = new JMenuItem("Save");
        file.add(save);
        JMenuItem upload = new JMenuItem("Upload");
        file.add(upload);
        JMenuItem help = new JMenuItem("Help");
        file.add(help);
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                JOptionPane.showMessageDialog(null, "Hover over something you do not understand.", "Help",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel panelLeft = new JPanel(new GridBagLayout());
        ArrayList<EventInput> eventInputs = new ArrayList<EventInput>();
        GridBagConstraints c = new GridBagConstraints();
        JScrollPane scrollPaneLeft = new JScrollPane(panelLeft, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneLeft.setPreferredSize(new Dimension(400, 800));
        c.gridx = 1;
        c.gridy = 0;
        c.ipadx = 10;
        c.weightx = 10;
        JLabel titleLabel = new JLabel("Enter Title:");
        panelLeft.add(titleLabel, c);
        c.gridx = 2;
        panelLeft.add(titleTF, c);
        JLabel eventLabel = new JLabel("Enter Event:");
        eventLabel.setToolTipText("Enter the name of the event, be specific for more obscure events.");
        JLabel checkBoxLabel = new JLabel("Single Date?");
        checkBoxLabel.setToolTipText("Tick box if the event occured on a single date. i.e. is not a period of time.");
        JButton addEvent = new JButton("+");
        addEvent.setToolTipText("Press to add an event slot.");
        c.gridy++;
        c.gridx = 1;
        panelLeft.add(addEvent, c);
        c.gridx = 2;
        panelLeft.add(eventLabel, c);
        c.gridx = 4;
        panelLeft.add(checkBoxLabel, c);
        eventInputSlot(panelLeft, c, eventInputs);
        addEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventInputSlot(panelLeft, c, eventInputs);
            }
        });

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                saveToCSV(titleTF.getText(), eventInputs, false);
            }
        });

        loadTimeline(upload, panelLeft, c, eventInputs);

        JPanel panelRight = new JPanel();
        panelRight.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPaneRight = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JButton start = new JButton("Generate Timeline");
        start.setFont(new Font("Menlo", Font.BOLD, 40));

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToCSV("timeline", eventInputs, true);
            }
        });

        panelRight.add(BorderLayout.CENTER, scrollPaneRight);
        panelRight.add(BorderLayout.SOUTH, start);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneLeft, panelRight);
        splitPane.setDividerSize(0);

        frame.add(splitPane);
        frame.getContentPane().add(BorderLayout.NORTH, menu);
        frame.setVisible(true);
    }

    public void printText(String text) {
        textArea.setText(text);
        textArea.update(textArea.getGraphics());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private void loadTimeline(JMenuItem upload, JPanel panel, GridBagConstraints c, ArrayList<EventInput> eventInputs) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        UploadCSV uploadCSV = new UploadCSV();
        ArrayList<String> scrapeTopics = new ArrayList<String>();

        upload.addActionListener(e -> {
            fc.setDialogTitle("Select Timeline");
            int r = fc.showOpenDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {
                resetTextFields(eventInputs, scrapeTopics);
                File file = fc.getSelectedFile();
                if (fc.getName(file).toLowerCase().endsWith(".csv")) {
                    uploadCSV.extractingData(scrapeTopics, file.getName());
                    titleTF.setText(scrapeTopics.get(0));
                    scrapeTopics.remove(0);
                    if (scrapeTopics.size() > eventInputs.size()) {
                        int difference = scrapeTopics.size() - eventInputs.size();
                        for (int i = 0; i < difference; i++) {
                            eventInputSlot(panel, c, eventInputs);
                        }
                    }
                    int i = 0;
                    for (String scrapeTopic : scrapeTopics) {
                        if (!scrapeTopic.contains("-P-")) {
                            eventInputs.get(i).getPeriodCheck().setSelected(true);
                        }
                        scrapeTopic = scrapeTopic.replaceAll("-P-", "");
                        eventInputs.get(i).getEventTF().setText(scrapeTopic);
                        i++;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error: File format\nFile must be of CSV format.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    r = JFileChooser.CANCEL_OPTION;
                }
            }
        });
    }

    private void resetTextFields(ArrayList<EventInput> eventInputs, ArrayList<String> scrapeTopics) {
        titleTF.setText(null);
        for (EventInput input : eventInputs) {
            input.getPeriodCheck().setSelected(false);
            input.getEventTF().setText(null);
        }
        scrapeTopics.clear();
    }

    private void eventInputSlot(JPanel panel, GridBagConstraints c, ArrayList<EventInput> eventInputs) {
        JTextField eventTF = new JTextField(20);
        JCheckBox periodCheck = new JCheckBox();
        eventTF.setToolTipText("Enter the name of the event, be specific for more obscure events.");
        periodCheck.setToolTipText("Tick box if the event occured on a single date. i.e. is not a period of time.");
        c.gridy++;
        c.gridx = 2;
        panel.add(eventTF, c);
        c.gridx = 4;
        panel.add(periodCheck, c);
        eventInputs.add(new EventInput(eventTF, periodCheck));
        panel.revalidate();
        panel.repaint();
    }

    private void saveToCSV(String fileName, ArrayList<EventInput> eventInputs, Boolean genTimeline) {
        if (!titleTF.getText().equals("")) {
            setTitle(titleTF.getText());
            String filePath = "./" + fileName + ".csv";
            try {
                FileWriter fileWriter = new FileWriter(filePath, false);

                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(titleTF.getText() + ",");
                for (EventInput temp : eventInputs) {
                    if (!temp.getEventTF().getText().equals("")) {
                        bufferedWriter.write(temp.getEventTF().getText());
                        if (!temp.getPeriodCheck().isSelected()) {
                            bufferedWriter.write("-P-");
                        }
                        bufferedWriter.write(",");
                    }
                }
                bufferedWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (genTimeline == true) {
                SwingUtilities.invokeLater(new TimeLinePlot());
            }
        } else {
            JOptionPane.showMessageDialog(null, "You must enter a title.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
