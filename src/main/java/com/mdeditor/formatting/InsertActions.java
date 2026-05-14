package com.mdeditor.formatting;

import com.mdeditor.ui.EditorPanel;

public class InsertActions {

    private final EditorPanel editorPanel;

    public InsertActions(EditorPanel editorPanel) {
        this.editorPanel = editorPanel;
    }

    public void insertBold() {
        wrapSelection("**", "**");
    }

    public void insertItalic() {
        wrapSelection("*", "*");
    }

    public void insertCode() {
        wrapSelection("`", "`");
    }

    public void insertStrikethrough() {
        wrapSelection("~~", "~~");
    }

    public void insertLink() {
        String selection = getSelectedText();
        if (selection != null && !selection.isEmpty()) {
            editorPanel.insertAtCaret("](" + selection + ")");
        } else {
            editorPanel.insertAtCaret("[link text](url)");
        }
    }

    public void insertImage() {
        editorPanel.insertAtCaret("![alt text](image-url)");
    }

    public void insertHeading(int level) {
        String prefix = "#".repeat(level) + " ";
        int pos = editorPanel.getCaretPosition();
        String text = editorPanel.getText();
        int lineStart = pos;
        while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
            lineStart--;
        }
        editorPanel.getEditor().setCaretPosition(lineStart);
        editorPanel.insertAtCaret(prefix);
    }

    public void insertBulletList() {
        insertAtLineStart("- ");
    }

    public void insertOrderedList() {
        insertAtLineStart("1. ");
    }

    public void insertTaskList() {
        insertAtLineStart("- [ ] ");
    }

    public void insertBlockquote() {
        insertAtLineStart("> ");
    }

    public void insertHorizontalRule() {
        editorPanel.insertAtCaret("\n---\n");
    }

    public void insertTable() {
        editorPanel.insertAtCaret(
            "\n| Header 1 | Header 2 | Header 3 |\n" +
            "|----------|----------|----------|\n" +
            "| Cell 1   | Cell 2   | Cell 3   |\n"
        );
    }

    public void indent() {
        insertAtLineStart("    ");
    }

    private void wrapSelection(String prefix, String suffix) {
        String selected = getSelectedText();
        if (selected != null && !selected.isEmpty()) {
            String text = editorPanel.getText();
            int start = editorPanel.getCaretPosition() - selected.length();
            int end = editorPanel.getCaretPosition();
            String newText = text.substring(0, start) + prefix + selected + suffix + text.substring(end);
            editorPanel.setText(newText);
            editorPanel.getEditor().setCaretPosition(start + prefix.length() + selected.length() + suffix.length());
        } else {
            editorPanel.insertAtCaret(prefix + "text" + suffix);
        }
    }

    private String getSelectedText() {
        return editorPanel.getEditor().getSelectedText();
    }

    private void insertAtLineStart(String prefix) {
        int pos = editorPanel.getCaretPosition();
        String text = editorPanel.getText();
        int lineStart = pos;
        while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
            lineStart--;
        }
        editorPanel.getEditor().setCaretPosition(lineStart);
        editorPanel.insertAtCaret(prefix);
    }
}
