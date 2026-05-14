package com.mdeditor.util;

import com.mdeditor.formatting.InsertActions;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;

public class KeyboardShortcuts {

    public static void install(JTextArea editor, UndoManager undoManager, InsertActions insertActions,
                               Runnable onSave, Runnable onOpen, Runnable onNew,
                               Runnable onFind, Runnable onFindReplace, Runnable onExportHtml,
                               Runnable onPrint, Runnable onTogglePreview,
                               Runnable onToggleReading) {

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        editor.getActionMap().put("save", createAction(onSave));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "open");
        editor.getActionMap().put("open", createAction(onOpen));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        editor.getActionMap().put("new", createAction(onNew));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        editor.getActionMap().put("undo", createAction(() -> {
            if (undoManager.canUndo()) undoManager.undo();
        }));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        editor.getActionMap().put("redo", createAction(() -> {
            if (undoManager.canRedo()) undoManager.redo();
        }));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "bold");
        editor.getActionMap().put("bold", createAction(insertActions::insertBold));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), "italic");
        editor.getActionMap().put("italic", createAction(insertActions::insertItalic));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK), "code");
        editor.getActionMap().put("code", createAction(insertActions::insertCode));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "find");
        editor.getActionMap().put("find", createAction(onFind));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "replace");
        editor.getActionMap().put("replace", createAction(onFindReplace));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "exportHtml");
        editor.getActionMap().put("exportHtml", createAction(onExportHtml));

        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "print");
        editor.getActionMap().put("print", createAction(onPrint));

        KeyStroke previewKey = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        editor.getInputMap().put(previewKey, "togglePreview");
        editor.getActionMap().put("togglePreview", createAction(onTogglePreview));

        KeyStroke readingKey = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        editor.getInputMap().put(readingKey, "toggleReading");
        editor.getActionMap().put("toggleReading", createAction(onToggleReading));
    }

    private static Action createAction(Runnable runnable) {
        return new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                runnable.run();
            }
        };
    }
}
