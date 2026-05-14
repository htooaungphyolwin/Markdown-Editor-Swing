package com.mdeditor.ui.reading;

import com.mdeditor.preview.ThemeManager;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

public class ReadingToolBar extends JToolBar {

    private final ReadingFrame readingFrame;
    private final ThemeManager themeManager;
    private java.util.function.Consumer<Integer> fontSizeChangeListener;

    public ReadingToolBar(ReadingFrame readingFrame, ThemeManager themeManager) {
        super("Reader Toolbar");
        this.readingFrame = readingFrame;
        this.themeManager = themeManager;

        setFloatable(false);

        JButton fontPlus = new JButton("A+");
        fontPlus.setToolTipText("Increase font size");
        fontPlus.setFocusable(false);
        fontPlus.addActionListener(e -> {
            if (fontSizeChangeListener != null) fontSizeChangeListener.accept(2);
        });
        add(fontPlus);

        JButton fontMinus = new JButton("A-");
        fontMinus.setToolTipText("Decrease font size");
        fontMinus.setFocusable(false);
        fontMinus.addActionListener(e -> {
            if (fontSizeChangeListener != null) fontSizeChangeListener.accept(-2);
        });
        add(fontMinus);

        addSeparator();

        JComboBox<String> themeCombo = new JComboBox<>(new String[]{
                ThemeManager.THEME_LIGHT, ThemeManager.THEME_DARK, ThemeManager.THEME_SEPIA
        });
        themeCombo.setSelectedItem(themeManager.getCurrentTheme());
        themeCombo.addActionListener(e -> {
            String selected = (String) themeCombo.getSelectedItem();
            themeManager.setTheme(selected);
            readingFrame.applyThemeStyle();
        });
        add(themeCombo);

        addSeparator();

        JButton closeBtn = new JButton("Close Reader");
        closeBtn.setFocusable(false);
        closeBtn.addActionListener(e -> readingFrame.setVisible(false));
        add(closeBtn);

        readingFrame.getRootPane().registerKeyboardAction(
                e -> readingFrame.setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        readingFrame.getRootPane().registerKeyboardAction(
                e -> readingFrame.setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public void setFontSizeChangeListener(java.util.function.Consumer<Integer> listener) {
        this.fontSizeChangeListener = listener;
    }
}
