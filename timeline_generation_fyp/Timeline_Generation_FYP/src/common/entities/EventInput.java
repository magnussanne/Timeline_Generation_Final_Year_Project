package common.entities;

import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class EventInput {
    private JTextField eventTF;
    private JCheckBox periodCheck;

    public EventInput(JTextField eventTF, JCheckBox periodCheck) {
        this.eventTF = eventTF;
        this.periodCheck = periodCheck;
    }

    public JTextField getEventTF() {
        return eventTF;
    }

    public JCheckBox getPeriodCheck() {
        return periodCheck;
    }
}
