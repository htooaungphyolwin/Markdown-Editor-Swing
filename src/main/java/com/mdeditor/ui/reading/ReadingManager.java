package com.mdeditor.ui.reading;

import com.mdeditor.markdown.MarkdownProcessor;
import com.mdeditor.ui.EditorPanel;
import com.mdeditor.ui.PreviewPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.DocumentListener;

public class ReadingManager {

    public static final String MODE_POPOUT = "popout";
    public static final String MODE_FOCUS = "focus";

    private final EditorPanel editorPanel;
    private final PreviewPanel previewPanel;
    private final MarkdownProcessor markdownProcessor;
    private final JSplitPane splitPane;
    private final JPanel contentPane;
    private final Runnable onReadingModeChanged;

    private ReadingFrame readingFrame;
    private String defaultMode = MODE_POPOUT;
    private boolean readingActive = false;

    private Component focusPreviewComponent;
    private Component focusEditorComponent;

    private final Timer debounceTimer;

    private final DocumentListener docListener;

    private String documentBase;

    private static final int DEBOUNCE_DELAY = 300;

    public ReadingManager(EditorPanel editorPanel, PreviewPanel previewPanel,
                          MarkdownProcessor markdownProcessor, JSplitPane splitPane,
                          JPanel contentPane, Runnable onReadingModeChanged) {
        this.editorPanel = editorPanel;
        this.previewPanel = previewPanel;
        this.markdownProcessor = markdownProcessor;
        this.splitPane = splitPane;
        this.contentPane = contentPane;
        this.onReadingModeChanged = onReadingModeChanged;

        this.debounceTimer = new Timer(DEBOUNCE_DELAY, e -> syncReaderContent());
        debounceTimer.setRepeats(false);

        this.docListener = new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { onEditorChange(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { onEditorChange(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { onEditorChange(); }
        };

        editorPanel.addDocumentListener(docListener);
    }

    public void setDefaultMode(String mode) {
        if (MODE_POPOUT.equals(mode) || MODE_FOCUS.equals(mode)) {
            this.defaultMode = mode;
        }
    }

    public String getDefaultMode() {
        return defaultMode;
    }

    public boolean isReadingActive() {
        return readingActive;
    }

    public void toggleReading() {
        if (readingActive) {
            exitReadingMode();
        } else {
            enterReadingMode();
        }
    }

    public void enterReadingMode() {
        if (readingActive) return;
        readingActive = true;

        if (MODE_POPOUT.equals(defaultMode)) {
            openReader();
        } else {
            enterFocusView();
        }

        onReadingModeChanged.run();
    }

    public void exitReadingMode() {
        if (!readingActive) return;
        readingActive = false;

        if (readingFrame != null && readingFrame.isVisible()) {
            closeReader();
        }
        exitFocusView();

        onReadingModeChanged.run();
    }

    public void openReader() {
        if (readingFrame == null) {
            readingFrame = new ReadingFrame(markdownProcessor, previewPanel.getThemeManager());
            readingFrame.setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
            readingFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    readingActive = false;
                    onReadingModeChanged.run();
                }
            });
        }

        syncReaderContent();
        readingFrame.setVisible(true);
        readingFrame.toFront();
    }

    public void closeReader() {
        if (readingFrame != null) {
            readingFrame.setVisible(false);
        }
    }

    public void enterFocusView() {
        focusEditorComponent = splitPane.getLeftComponent();
        focusPreviewComponent = splitPane.getRightComponent();

        if (focusPreviewComponent == null) {
            focusPreviewComponent = previewPanel;
        }

        splitPane.setLeftComponent(null);
        splitPane.setRightComponent(focusPreviewComponent);

        splitPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "exitFocus");
        splitPane.getActionMap().put("exitFocus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitReadingMode();
            }
        });
    }

    public void exitFocusView() {
        splitPane.getActionMap().remove("exitFocus");
        splitPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));

        if (focusEditorComponent != null && focusPreviewComponent != null) {
            splitPane.setLeftComponent(focusEditorComponent);
            splitPane.setRightComponent(focusPreviewComponent);
            splitPane.setDividerLocation(0.5);
        }

        focusEditorComponent = null;
        focusPreviewComponent = null;
    }

    private void onEditorChange() {
        debounceTimer.restart();
    }

    private void syncReaderContent() {
        String markdown = editorPanel.getText();
        String html = markdownProcessor.toHtml(markdown);

        if (readingFrame != null) {
            readingFrame.setContent(html, documentBase);
            if (documentBase != null) {
                readingFrame.setDocumentBase(documentBase);
            }
        }
    }

    public void setDocumentBase(String documentBase) {
        this.documentBase = documentBase;
    }

    public String getReadingModeLabel() {
        if (!readingActive) return "Read\u25BC";
        if (readingFrame != null && readingFrame.isVisible()) return "Reader ON";
        return "Focus ON";
    }
}
