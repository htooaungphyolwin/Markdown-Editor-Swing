package com.mdeditor.document;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

public class DocumentState {

    private final UndoManager undoManager;
    private boolean dirty;
    private String filePath;
    private int wordCount;

    public DocumentState() {
        this.undoManager = new UndoManager();
        this.dirty = false;
        this.filePath = null;
        this.wordCount = 0;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        if (filePath == null) return "Untitled";
        String name = filePath.substring(filePath.lastIndexOf(java.io.File.separator) + 1);
        return name;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void updateWordCount(String text) {
        if (text == null || text.isBlank()) {
            wordCount = 0;
        } else {
            wordCount = text.trim().split("\\s+").length;
        }
    }

    public void bind(Document doc) {
        doc.addUndoableEditListener((UndoableEditListener) undoManager);
    }

    public void reset() {
        undoManager.discardAllEdits();
        dirty = false;
        filePath = null;
        wordCount = 0;
    }
}
