package com.mdeditor.ui;

import com.mdeditor.preview.ThemeManager;
import java.awt.Color;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

public class PreviewPanel extends JScrollPane {

    private final JEditorPane preview;
    private final ThemeManager themeManager;

    public PreviewPanel() {
        preview = new JEditorPane();
        preview.setEditable(false);
        preview.setContentType("text/html");

        HTMLEditorKit kit = new HTMLEditorKit();
        preview.setEditorKitForContentType("text/html", kit);
        preview.setEditorKit(kit);

        themeManager = new ThemeManager();
        setHtmlContent("<html><body></body></html>");

        setViewportView(preview);
    }

    public void setHtmlContent(String html) {
        setHtmlContent(html, null);
    }

    public void setHtmlContent(String html, String documentBase) {
        String styled = themeManager.wrapWithTheme(html, documentBase);
        preview.setText(styled);
        preview.setCaretPosition(0);
        if (documentBase != null) {
            preview.putClientProperty(javax.swing.JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        }
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public void applyTheme(String themeName) {
        themeManager.setTheme(themeName);
        preview.setBackground(themeManager.getBackgroundColor());
        preview.setForeground(themeManager.getTextColor());
    }
}
