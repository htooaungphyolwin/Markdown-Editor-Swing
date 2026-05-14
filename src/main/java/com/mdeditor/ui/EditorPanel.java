package com.mdeditor.ui;

import com.mdeditor.markdown.MarkdownProcessor;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;

public class EditorPanel extends JScrollPane {

    private final JTextArea editor;

    public EditorPanel() {
        editor = new JTextArea();
        editor.setFont(new Font("Menlo", Font.PLAIN, 14));
        editor.setTabSize(4);
        editor.setWrapStyleWord(true);
        editor.setLineWrap(true);
        setViewportView(editor);
    }

    public JTextArea getEditor() {
        return editor;
    }

    public String getText() {
        return editor.getText();
    }

    public void setText(String text) {
        editor.setText(text);
    }

    public void addDocumentListener(DocumentListener listener) {
        editor.getDocument().addDocumentListener(listener);
    }

    public void requestEditorFocus() {
        editor.requestFocusInWindow();
    }

    public int getCaretPosition() {
        return editor.getCaretPosition();
    }

    public void insertAtCaret(String text) {
        editor.insert(text, editor.getCaretPosition());
        editor.requestFocusInWindow();
    }
}
