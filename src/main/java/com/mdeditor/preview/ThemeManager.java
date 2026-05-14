package com.mdeditor.preview;

import java.awt.Color;

public class ThemeManager {

    public static final String THEME_LIGHT = "Light";
    public static final String THEME_DARK = "Dark";
    public static final String THEME_SEPIA = "Sepia";

    private String currentTheme = THEME_LIGHT;

    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(String theme) {
        if (theme.equals(THEME_LIGHT) || theme.equals(THEME_DARK) || theme.equals(THEME_SEPIA)) {
            this.currentTheme = theme;
        }
    }

    public String wrapWithTheme(String bodyHtml) {
        return wrapWithTheme(bodyHtml, null);
    }

    public String wrapWithTheme(String bodyHtml, String documentBase) {
        String css = getCss();
        String baseTag = "";
        if (documentBase != null && !documentBase.isEmpty()) {
            String encoded = documentBase.endsWith("/") ? documentBase : documentBase + "/";
            baseTag = "<base href=\"" + encoded + "\">\n";
        }
        return """
            <html>
            <head>
            %s
            <style>
            %s
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif; padding: 16px; }
            code { background-color: %s; padding: 2px 4px; border-radius: 3px; font-size: 0.9em; }
            pre { background-color: %s; padding: 12px; border-radius: 4px; overflow-x: auto; }
            blockquote { border-left: 4px solid #ccc; margin: 0; padding: 0 16px; color: #666; }
            table { border-collapse: collapse; width: 100%%; }
            th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
            th { background-color: %s; }
            img { max-width: 100%%; }
            </style>
            </head>
            <body>%s</body>
            </html>
            """.formatted(baseTag, css, getCodeBg(), getCodeBg(), getTableHeaderBg(), bodyHtml);
    }

    private String getCss() {
        return switch (currentTheme) {
            case THEME_DARK -> """
                body { background-color: #1e1e1e; color: #d4d4d4; }
                a { color: #569cd6; }
                code { background-color: #2d2d2d; color: #ce9178; }
                pre { background-color: #2d2d2d; }
                blockquote { border-left-color: #555; color: #999; }
                th, td { border-color: #444; }
                th { background-color: #333; }
                """;
            case THEME_SEPIA -> """
                body { background-color: #fbf1c7; color: #3c3836; }
                a { color: #d79921; }
                code { background-color: #ebdbb2; color: #b16286; }
                pre { background-color: #ebdbb2; }
                blockquote { border-left-color: #d5c4a1; color: #7c6f64; }
                th, td { border-color: #d5c4a1; }
                th { background-color: #ebdbb2; }
                """;
            default -> """
                body { background-color: #ffffff; color: #24292e; }
                a { color: #0366d6; }
                code { background-color: #f6f8fa; color: #d73a49; }
                pre { background-color: #f6f8fa; }
                th, td { border-color: #dfe2e5; }
                th { background-color: #f6f8fa; }
                """;
        };
    }

    public Color getBackgroundColor() {
        return switch (currentTheme) {
            case THEME_DARK -> new Color(0x1e, 0x1e, 0x1e);
            case THEME_SEPIA -> new Color(0xfb, 0xf1, 0xc7);
            default -> Color.WHITE;
        };
    }

    public Color getTextColor() {
        return switch (currentTheme) {
            case THEME_DARK -> new Color(0xd4, 0xd4, 0xd4);
            case THEME_SEPIA -> new Color(0x3c, 0x38, 0x36);
            default -> new Color(0x24, 0x29, 0x2e);
        };
    }

    private String getCodeBg() {
        return switch (currentTheme) {
            case THEME_DARK -> "#2d2d2d";
            case THEME_SEPIA -> "#ebdbb2";
            default -> "#f6f8fa";
        };
    }

    private String getTableHeaderBg() {
        return switch (currentTheme) {
            case THEME_DARK -> "#333";
            case THEME_SEPIA -> "#ebdbb2";
            default -> "#f6f8fa";
        };
    }
}
