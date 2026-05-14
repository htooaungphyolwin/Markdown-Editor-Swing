package com.mdeditor.ui.reading;

import com.mdeditor.markdown.MarkdownProcessor;
import com.mdeditor.preview.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

public class ReadingFrame extends JFrame {

    private final JEditorPane readerPane;
    private final ReadingToolBar toolBar;
    private final MarkdownProcessor markdownProcessor;
    private final ThemeManager themeManager;

    public ReadingFrame(MarkdownProcessor markdownProcessor, ThemeManager themeManager) {
        super("Reader - Markdown Editor");
        this.markdownProcessor = markdownProcessor;
        this.themeManager = themeManager;

        readerPane = new JEditorPane();
        readerPane.setEditable(false);
        readerPane.setContentType("text/html");

        HTMLEditorKit kit = new HTMLEditorKit();
        readerPane.setEditorKitForContentType("text/html", kit);
        readerPane.setEditorKit(kit);

        JScrollPane scrollPane = new JScrollPane(readerPane);
        scrollPane.setBorder(null);

        toolBar = new ReadingToolBar(this, themeManager);
        toolBar.setFontSizeChangeListener(this::changeFontSize);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setSize(800, 600);
        setMinimumSize(new Dimension(400, 300));

        applyThemeStyle();
    }

    public void setHtmlContent(String styledHtml) {
        readerPane.setText(styledHtml);
        readerPane.setCaretPosition(0);
    }

    public void setDocumentBase(String filePath) {
        if (filePath != null) {
            readerPane.putClientProperty(javax.swing.JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        }
    }

    public void applyThemeStyle() {
        readerPane.setBackground(themeManager.getBackgroundColor());
        readerPane.setForeground(themeManager.getTextColor());
    }

    private void changeFontSize(int delta) {
        Font current = readerPane.getFont();
        int newSize = Math.max(10, Math.min(48, current.getSize() + delta));
        readerPane.setFont(current.deriveFont((float) newSize));
    }
}
