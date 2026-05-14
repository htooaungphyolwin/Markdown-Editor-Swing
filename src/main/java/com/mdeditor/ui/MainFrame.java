package com.mdeditor.ui;

import com.mdeditor.document.DocumentState;
import com.mdeditor.document.FileManager;
import com.mdeditor.formatting.InsertActions;
import com.mdeditor.markdown.MarkdownProcessor;
import com.mdeditor.preview.ThemeManager;
import com.mdeditor.ui.reading.ReadingManager;
import com.mdeditor.util.KeyboardShortcuts;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame {

    private final EditorPanel editorPanel;
    private final PreviewPanel previewPanel;
    private final StatusBar statusBar;
    private final DocumentState docState;
    private final InsertActions insertActions;
    private final MarkdownProcessor markdownProcessor;
    private final ReadingManager readingManager;

    private final Timer debounceTimer;
    private boolean previewVisible = true;
    private JSplitPane splitPane;
    private JToggleButton previewToggle;
    private JToggleButton readToggle;

    private static final int DEBOUNCE_DELAY = 300;

    public MainFrame() {
        super("Markdown Editor");

        editorPanel = new EditorPanel();
        previewPanel = new PreviewPanel();
        statusBar = new StatusBar();
        docState = new DocumentState();
        insertActions = new InsertActions(editorPanel);
        markdownProcessor = new MarkdownProcessor();

        buildUI();

        readingManager = new ReadingManager(editorPanel, previewPanel, markdownProcessor,
                splitPane, (javax.swing.JPanel) getContentPane(), this::onReadingModeChanged);

        debounceTimer = new Timer(DEBOUNCE_DELAY, e -> renderPreview());
        debounceTimer.setRepeats(false);

        installListeners();
        installShortcuts();
        updateTitle();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        setJMenuBar(createMenuBar());

        JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel, previewPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        add(statusBar, BorderLayout.SOUTH);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        docState.bind(editorPanel.getEditor().getDocument());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("New", "N", e -> handleNew()));
        fileMenu.add(createMenuItem("Open\u2026", "O", e -> handleOpen()));
        fileMenu.add(createMenuItem("Save", "S", e -> handleSave()));
        fileMenu.add(createMenuItem("Save As\u2026", null, e -> handleSaveAs()));
        fileMenu.add(new JSeparator());
        fileMenu.add(createMenuItem("Export HTML\u2026", "E", e -> handleExportHtml()));
        fileMenu.add(new JSeparator());
        fileMenu.add(createMenuItem("Print\u2026", "P", e -> handlePrint()));
        fileMenu.add(new JSeparator());
        fileMenu.add(createMenuItem("Exit", null, e -> handleExit()));
        menuBar.add(fileMenu);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem("Undo", "Z", e -> {
            if (docState.getUndoManager().canUndo()) docState.getUndoManager().undo();
        }));
        editMenu.add(createMenuItem("Redo", "Y", e -> {
            if (docState.getUndoManager().canRedo()) docState.getUndoManager().redo();
        }));
        editMenu.add(new JSeparator());
        editMenu.add(createMenuItem("Cut", null, e -> editorPanel.getEditor().cut()));
        editMenu.add(createMenuItem("Copy", null, e -> editorPanel.getEditor().copy()));
        editMenu.add(createMenuItem("Paste", null, e -> editorPanel.getEditor().paste()));
        editMenu.add(new JSeparator());
        editMenu.add(createMenuItem("Find\u2026", "F", e -> showFindReplace()));
        editMenu.add(createMenuItem("Find & Replace\u2026", "H", e -> showFindReplace()));
        editMenu.add(new JSeparator());
        editMenu.add(createMenuItem("Select All", "A", e -> editorPanel.getEditor().selectAll()));
        menuBar.add(editMenu);

        // Format menu
        JMenu formatMenu = new JMenu("Format");
        formatMenu.add(createMenuItem("Bold", "B", e -> insertActions.insertBold()));
        formatMenu.add(createMenuItem("Italic", "I", e -> insertActions.insertItalic()));
        formatMenu.add(createMenuItem("Code", "K", e -> insertActions.insertCode()));
        formatMenu.add(createMenuItem("Strikethrough", null, e -> insertActions.insertStrikethrough()));
        formatMenu.add(new JSeparator());
        JMenu headingMenu = new JMenu("Heading");
        for (int i = 1; i <= 6; i++) {
            final int level = i;
            headingMenu.add(createMenuItem("H" + i, null, e -> insertActions.insertHeading(level)));
        }
        formatMenu.add(headingMenu);
        formatMenu.add(new JSeparator());
        formatMenu.add(createMenuItem("Link", null, e -> insertActions.insertLink()));
        formatMenu.add(createMenuItem("Image", null, e -> insertActions.insertImage()));
        formatMenu.add(createMenuItem("Table", null, e -> insertActions.insertTable()));
        formatMenu.add(new JSeparator());
        formatMenu.add(createMenuItem("Bullet List", null, e -> insertActions.insertBulletList()));
        formatMenu.add(createMenuItem("Ordered List", null, e -> insertActions.insertOrderedList()));
        formatMenu.add(createMenuItem("Task List", null, e -> insertActions.insertTaskList()));
        formatMenu.add(createMenuItem("Blockquote", null, e -> insertActions.insertBlockquote()));
        formatMenu.add(createMenuItem("Horizontal Rule", null, e -> insertActions.insertHorizontalRule()));
        formatMenu.add(new JSeparator());
        formatMenu.add(createMenuItem("Indent", null, e -> insertActions.indent()));
        menuBar.add(formatMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem togglePreviewItem = new JCheckBoxMenuItem("Show Preview", previewVisible);
        togglePreviewItem.addActionListener(e -> togglePreview());
        viewMenu.add(togglePreviewItem);

        viewMenu.add(new JSeparator());

        JMenu readingMenu = new JMenu("Reading Mode");
        JMenuItem popoutItem = new JMenuItem("Pop-out Reader");
        popoutItem.addActionListener(e -> {
            readingManager.setDefaultMode(ReadingManager.MODE_POPOUT);
            readingManager.enterReadingMode();
            onReadingModeChanged();
        });
        readingMenu.add(popoutItem);

        JMenuItem focusItem = new JMenuItem("Focus View");
        focusItem.addActionListener(e -> {
            readingManager.setDefaultMode(ReadingManager.MODE_FOCUS);
            readingManager.enterReadingMode();
            onReadingModeChanged();
        });
        readingMenu.add(focusItem);

        readingMenu.add(new JSeparator());
        JMenuItem exitReadingItem = new JMenuItem("Exit Reading Mode");
        exitReadingItem.addActionListener(e -> {
            readingManager.exitReadingMode();
            onReadingModeChanged();
        });
        readingMenu.add(exitReadingItem);
        viewMenu.add(readingMenu);

        viewMenu.add(new JSeparator());
        JMenu themeMenu = new JMenu("Theme");
        for (String theme : new String[]{ThemeManager.THEME_LIGHT, ThemeManager.THEME_DARK, ThemeManager.THEME_SEPIA}) {
            JMenuItem item = new JMenuItem(theme);
            item.addActionListener(e -> {
                previewPanel.applyTheme(theme);
                renderPreview();
            });
            themeMenu.add(item);
        }
        viewMenu.add(themeMenu);
        menuBar.add(viewMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("Markdown Cheatsheet", null, e -> showCheatsheet()));
        helpMenu.add(new JSeparator());
        helpMenu.add(createMenuItem("About", null, e -> showAbout()));
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(true);

        toolbar.add(createToolButton("New", "N", e -> handleNew()));
        toolbar.add(createToolButton("Open", "O", e -> handleOpen()));
        toolbar.add(createToolButton("Save", "S", e -> handleSave()));
        toolbar.addSeparator();

        toolbar.add(createToolButton("Bold", "B", e -> insertActions.insertBold()));
        toolbar.add(createToolButton("Italic", "I", e -> insertActions.insertItalic()));
        toolbar.add(createToolButton("Code", "K", e -> insertActions.insertCode()));
        toolbar.add(createToolButton("Strike", null, e -> insertActions.insertStrikethrough()));
        toolbar.addSeparator();

        JButton headingBtn = new JButton("H\u2193");
        headingBtn.setToolTipText("Insert Heading");
        headingBtn.addActionListener(e -> {
            String[] options = {"H1", "H2", "H3", "H4", "H5", "H6"};
            String choice = (String) JOptionPane.showInputDialog(this, "Select heading level:",
                    "Heading", JOptionPane.PLAIN_MESSAGE, null, options, "H1");
            if (choice != null) {
                int level = Integer.parseInt(choice.substring(1));
                insertActions.insertHeading(level);
            }
        });
        toolbar.add(headingBtn);
        toolbar.addSeparator();

        toolbar.add(createToolButton("Link", null, e -> insertActions.insertLink()));
        toolbar.add(createToolButton("Image", null, e -> insertActions.insertImage()));
        toolbar.add(createToolButton("Table", null, e -> insertActions.insertTable()));
        toolbar.addSeparator();

        toolbar.add(createToolButton("UL", null, e -> insertActions.insertBulletList()));
        toolbar.add(createToolButton("OL", null, e -> insertActions.insertOrderedList()));
        toolbar.add(createToolButton("Task", null, e -> insertActions.insertTaskList()));
        toolbar.add(createToolButton("Quote", null, e -> insertActions.insertBlockquote()));
        toolbar.addSeparator();

        toolbar.add(createToolButton("Undo", "Z", e -> {
            if (docState.getUndoManager().canUndo()) docState.getUndoManager().undo();
        }));
        toolbar.add(createToolButton("Redo", "Y", e -> {
            if (docState.getUndoManager().canRedo()) docState.getUndoManager().redo();
        }));
        toolbar.addSeparator();

        // Preview toggle dropdown
        previewToggle = new JToggleButton("Preview");
        previewToggle.setSelected(previewVisible);
        previewToggle.setToolTipText("Toggle Preview (Ctrl+Shift+P)");
        previewToggle.addActionListener(e -> {
            togglePreview();
            previewToggle.setSelected(previewVisible);
        });
        toolbar.add(previewToggle);

        // Reading Mode toggle dropdown
        readToggle = new JToggleButton("Read\u25BC");
        readToggle.setSelected(false);
        readToggle.setToolTipText("Toggle Reading Mode (Ctrl+Shift+R)");
        readToggle.addActionListener(e -> {
            readingManager.toggleReading();
            readToggle.setSelected(readingManager.isReadingActive());
            readToggle.setText(readingManager.getReadingModeLabel());
        });
        toolbar.add(readToggle);

        return toolbar;
    }

    private JMenuItem createMenuItem(String text, String shortcut, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        if (shortcut != null) {
            int modifiers = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            item.setAccelerator(KeyStroke.getKeyStroke(shortcut.charAt(0), modifiers));
        }
        return item;
    }

    private JButton createToolButton(String text, String shortcut, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setToolTipText(text + (shortcut != null ? " (Ctrl+" + shortcut + ")" : ""));
        btn.addActionListener(listener);
        btn.setFocusable(false);
        return btn;
    }

    private void installListeners() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { onDocumentChange(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { onDocumentChange(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { onDocumentChange(); }
        };
        editorPanel.addDocumentListener(docListener);

        CaretListener caretListener = e -> updateStatusBarCursor();
        editorPanel.getEditor().addCaretListener(caretListener);
    }

    private void onDocumentChange() {
        docState.setDirty(true);
        updateTitle();
        updateStatusBarWordCount();
        debounceTimer.restart();
    }

    private String getDocumentBase() {
        String path = docState.getFilePath();
        if (path == null) return null;
        File f = new File(path);
        if (f.getParent() != null) {
            return f.getParent();
        }
        return null;
    }

    private void renderPreview() {
        String markdown = editorPanel.getText();
        String html = markdownProcessor.toHtml(markdown);
        previewPanel.setHtmlContent(html, getDocumentBase());
    }

    private void togglePreview() {
        previewVisible = !previewVisible;
        if (previewVisible) {
            splitPane.setRightComponent(previewPanel);
            splitPane.setDividerLocation(0.5);
            renderPreview();
        } else {
            splitPane.setRightComponent(null);
        }
    }

    private void onReadingModeChanged() {
        if (readToggle != null) {
            readToggle.setSelected(readingManager.isReadingActive());
            readToggle.setText(readingManager.getReadingModeLabel());
        }
    }

    // File actions
    private void handleNew() {
        if (checkSaveBeforeClosing()) {
            editorPanel.setText("");
            docState.reset();
            docState.bind(editorPanel.getEditor().getDocument());
            readingManager.setDocumentBase(null);
            updateTitle();
            statusBar.setModified(false);
        }
    }

    private void handleOpen() {
        if (!checkSaveBeforeClosing()) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Markdown files (*.md, *.markdown, *.txt)", "md", "markdown", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = FileManager.read(chooser.getSelectedFile());
                editorPanel.setText(content);
                docState.reset();
                docState.bind(editorPanel.getEditor().getDocument());
                docState.setFilePath(chooser.getSelectedFile().getAbsolutePath());
                docState.setDirty(false);
                readingManager.setDocumentBase(getDocumentBase());
                updateTitle();
                statusBar.setModified(false);
                renderPreview();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSave() {
        if (docState.getFilePath() == null) {
            handleSaveAs();
        } else {
            saveToFile(new File(docState.getFilePath()));
        }
    }

    private void handleSaveAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Markdown files (*.md)", "md"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".md");
            }
            docState.setFilePath(file.getAbsolutePath());
            saveToFile(file);
        }
    }

    private void saveToFile(File file) {
        try {
            FileManager.write(file, editorPanel.getText());
            docState.setDirty(false);
            docState.setFilePath(file.getAbsolutePath());
            readingManager.setDocumentBase(getDocumentBase());
            updateTitle();
            statusBar.setModified(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExportHtml() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("HTML files (*.html)", "html"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".html");
            }
            try {
                String html = markdownProcessor.toHtml(editorPanel.getText());
                FileManager.write(file, html);
                JOptionPane.showMessageDialog(this, "HTML exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting HTML: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handlePrint() {
        try {
            java.awt.print.PrinterJob pj = java.awt.print.PrinterJob.getPrinterJob();
            pj.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;
                String text = editorPanel.getText();
                graphics.drawString(text, 100, 100);
                return java.awt.print.Printable.PAGE_EXISTS;
            });
            if (pj.printDialog()) pj.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error printing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExit() {
        if (checkSaveBeforeClosing()) {
            System.exit(0);
        }
    }

    private boolean checkSaveBeforeClosing() {
        if (!docState.isDirty()) return true;
        int result = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes. Do you want to save before closing?",
                "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            handleSave();
            return !docState.isDirty(); // false if save was cancelled
        }
        return result == JOptionPane.NO_OPTION;
    }

    private void showFindReplace() {
        String text = editorPanel.getEditor().getSelectedText();
        String search = JOptionPane.showInputDialog(this, "Find:", text != null ? text : "");
        if (search != null && !search.isEmpty()) {
            String content = editorPanel.getText();
            int idx = content.indexOf(search);
            if (idx >= 0) {
                editorPanel.getEditor().setCaretPosition(idx);
                editorPanel.getEditor().select(idx, idx + search.length());
                editorPanel.requestEditorFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Text not found: " + search);
            }
        }
    }

    private void showCheatsheet() {
        String cheatsheet = """
            # Markdown Cheatsheet

            **Bold**      `**text**`
            *Italic*      `*text*`
            `Code`        `` `code` ``
            ~~Strike~~    `~~text~~`

            # Heading 1   `# text`
            ## Heading 2  `## text`

            [Link](url)   `[text](url)`
            ![Image](src) `![alt](src)`

            - Bullet list `- item`
            1. Ordered    `1. item`
            - [ ] Task    `- [ ] task`
            > Blockquote  `> text`

            ````code block`  ``` `` `code `````
            --- Horizontal  `---`
            | Table |       `| col | col |`
            """;

        JOptionPane.showMessageDialog(this, cheatsheet, "Markdown Cheatsheet", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Markdown Editor v1.0\n\nA desktop markdown editor with live preview.\nBuilt with Java Swing and flexmark.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTitle() {
        String name = docState.getFileName();
        String modified = docState.isDirty() ? " *" : "";
        setTitle(name + modified + " - Markdown Editor");
    }

    private void updateStatusBarCursor() {
        try {
            int pos = editorPanel.getCaretPosition();
            String text = editorPanel.getText();
            int line = 1, col = 1;
            for (int i = 0; i < pos && i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    line++;
                    col = 1;
                } else {
                    col++;
                }
            }
            statusBar.updateCursor(line, col);
        } catch (Exception ignored) {}
    }

    private void updateStatusBarWordCount() {
        docState.updateWordCount(editorPanel.getText());
        statusBar.updateWordCount(docState.getWordCount());
    }

    private void installShortcuts() {
        KeyboardShortcuts.install(
                editorPanel.getEditor(),
                docState.getUndoManager(),
                insertActions,
                this::handleSave,
                this::handleOpen,
                this::handleNew,
                this::showFindReplace,
                this::showFindReplace,
                this::handleExportHtml,
                this::handlePrint,
                this::togglePreview,
                () -> {
                    readingManager.toggleReading();
                    onReadingModeChanged();
                }
        );
    }
}
