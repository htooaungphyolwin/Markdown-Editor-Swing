package com.mdeditor.ui;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {

    private final JLabel cursorLabel;
    private final JLabel wordCountLabel;
    private final JLabel modifiedLabel;

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 2));
        setBorder(BorderFactory.createEtchedBorder());

        cursorLabel = new JLabel("Ln: 1  Col: 1");
        wordCountLabel = new JLabel("Words: 0");
        modifiedLabel = new JLabel(" ");

        add(cursorLabel);
        add(wordCountLabel);
        add(modifiedLabel);
    }

    public void updateCursor(int line, int col) {
        cursorLabel.setText("Ln: " + line + "  Col: " + col);
    }

    public void updateWordCount(int count) {
        wordCountLabel.setText("Words: " + count);
    }

    public void setModified(boolean modified) {
        modifiedLabel.setText(modified ? "* Modified" : "");
    }
}
