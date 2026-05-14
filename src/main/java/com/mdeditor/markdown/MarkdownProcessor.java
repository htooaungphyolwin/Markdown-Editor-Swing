package com.mdeditor.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkdownProcessor {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownProcessor() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, java.util.List.of(
                com.vladsch.flexmark.ext.tables.TablesExtension.create(),
                com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension.create(),
                com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension.create(),
                com.vladsch.flexmark.ext.autolink.AutolinkExtension.create()
        ));
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    public String toHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";
        var document = parser.parse(markdown);
        return renderer.render(document);
    }
}
