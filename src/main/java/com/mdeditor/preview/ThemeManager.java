package com.mdeditor.preview;

import java.awt.Color;

public class ThemeManager {

    public static final String THEME_LIGHT = "Light";
    public static final String THEME_DARK = "Dark";
    public static final String THEME_SEPIA = "Sepia";
    public static final String THEME_ATOM_ONE_DARK = "Atom One Dark";
    public static final String THEME_DRACULA = "Dracula";
    public static final String THEME_MONOKAI = "Monokai";
    public static final String THEME_SOLARIZED_LIGHT = "Solarized Light";
    public static final String THEME_SOLARIZED_DARK = "Solarized Dark";
    public static final String THEME_GITHUB_DARK = "GitHub Dark";
    public static final String THEME_NORD = "Nord";

    private String currentTheme = THEME_LIGHT;

    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(String theme) {
        this.currentTheme = theme;
    }

    public boolean isValidTheme(String theme) {
        return theme != null && (
            theme.equals(THEME_LIGHT) || theme.equals(THEME_DARK) || theme.equals(THEME_SEPIA) ||
            theme.equals(THEME_ATOM_ONE_DARK) || theme.equals(THEME_DRACULA) || theme.equals(THEME_MONOKAI) ||
            theme.equals(THEME_SOLARIZED_LIGHT) || theme.equals(THEME_SOLARIZED_DARK) ||
            theme.equals(THEME_GITHUB_DARK) || theme.equals(THEME_NORD)
        );
    }

    public String[] getAllThemes() {
        return new String[]{
            THEME_LIGHT, THEME_DARK, THEME_SEPIA, THEME_ATOM_ONE_DARK,
            THEME_DRACULA, THEME_MONOKAI, THEME_SOLARIZED_LIGHT, THEME_SOLARIZED_DARK,
            THEME_GITHUB_DARK, THEME_NORD
        };
    }

    public String wrapWithTheme(String bodyHtml) {
        return wrapWithTheme(bodyHtml, null, null);
    }

    public String wrapWithTheme(String bodyHtml, String documentBase) {
        return wrapWithTheme(bodyHtml, documentBase, null);
    }

    public String wrapWithTheme(String bodyHtml, String documentBase, String extraFontSize) {
        String css = getCss(extraFontSize);
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

    private String getCss(String extraFontSize) {
        String base = "";
        if (extraFontSize != null) {
            base = "body { font-size: " + extraFontSize + "px; } "
                + "h1 { font-size: 2em; } h2 { font-size: 1.5em; } "
                + "h3 { font-size: 1.17em; } h4 { font-size: 1em; } "
                + "h5 { font-size: 0.83em; } h6 { font-size: 0.75em; } ";
        }

        return switch (currentTheme) {
            case THEME_ATOM_ONE_DARK -> base + """
                body { background-color: #282c34; color: #abb2bf; }
                a { color: #61afef; }
                code { background-color: #2c313a; color: #e06c75; }
                pre { background-color: #2c313a; }
                blockquote { border-left-color: #528bff; color: #5c6370; }
                th, td { border-color: #3e4451; }
                th { background-color: #2c313a; }
                h1, h2, h3, h4, h5, h6 { color: #e5c07b; }
                """;
            case THEME_DRACULA -> base + """
                body { background-color: #282a36; color: #f8f8f2; }
                a { color: #8be9fd; }
                code { background-color: #44475a; color: #ff79c6; }
                pre { background-color: #44475a; }
                blockquote { border-left-color: #6272a4; color: #6272a4; }
                th, td { border-color: #44475a; }
                th { background-color: #44475a; }
                """;
            case THEME_MONOKAI -> base + """
                body { background-color: #272822; color: #f8f8f2; }
                a { color: #66d9ef; }
                code { background-color: #3e3d32; color: #f92672; }
                pre { background-color: #3e3d32; }
                blockquote { border-left-color: #75715e; color: #75715e; }
                th, td { border-color: #3e3d32; }
                th { background-color: #3e3d32; }
                """;
            case THEME_SOLARIZED_LIGHT -> base + """
                body { background-color: #fdf6e3; color: #657b83; }
                a { color: #268bd2; }
                code { background-color: #eee8d5; color: #cb4b16; }
                pre { background-color: #eee8d5; }
                blockquote { border-left-color: #93a1a1; color: #93a1a1; }
                th, td { border-color: #93a1a1; }
                th { background-color: #eee8d5; }
                """;
            case THEME_SOLARIZED_DARK -> base + """
                body { background-color: #002b36; color: #839496; }
                a { color: #268bd2; }
                code { background-color: #073642; color: #cb4b16; }
                pre { background-color: #073642; }
                blockquote { border-left-color: #586e75; color: #586e75; }
                th, td { border-color: #073642; }
                th { background-color: #073642; }
                """;
            case THEME_GITHUB_DARK -> base + """
                body { background-color: #0d1117; color: #c9d1d9; }
                a { color: #58a6ff; }
                code { background-color: #161b22; color: #79c0ff; }
                pre { background-color: #161b22; }
                blockquote { border-left-color: #30363d; color: #8b949e; }
                th, td { border-color: #30363d; }
                th { background-color: #161b22; }
                """;
            case THEME_NORD -> base + """
                body { background-color: #2e3440; color: #d8dee9; }
                a { color: #88c0d0; }
                code { background-color: #3b4252; color: #ebcb8b; }
                pre { background-color: #3b4252; }
                blockquote { border-left-color: #4c566a; color: #4c566a; }
                th, td { border-color: #434c5e; }
                th { background-color: #3b4252; }
                """;
            case THEME_DARK -> base + """
                body { background-color: #1e1e1e; color: #d4d4d4; }
                a { color: #569cd6; }
                code { background-color: #2d2d2d; color: #ce9178; }
                pre { background-color: #2d2d2d; }
                blockquote { border-left-color: #555; color: #999; }
                th, td { border-color: #444; }
                th { background-color: #333; }
                """;
            case THEME_SEPIA -> base + """
                body { background-color: #fbf1c7; color: #3c3836; }
                a { color: #d79921; }
                code { background-color: #ebdbb2; color: #b16286; }
                pre { background-color: #ebdbb2; }
                blockquote { border-left-color: #d5c4a1; color: #7c6f64; }
                th, td { border-color: #d5c4a1; }
                th { background-color: #ebdbb2; }
                """;
            default -> base + """
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
            case THEME_ATOM_ONE_DARK -> new Color(0x28, 0x2c, 0x34);
            case THEME_DRACULA -> new Color(0x28, 0x2a, 0x36);
            case THEME_MONOKAI -> new Color(0x27, 0x28, 0x22);
            case THEME_SOLARIZED_LIGHT -> new Color(0xfd, 0xf6, 0xe3);
            case THEME_SOLARIZED_DARK -> new Color(0x00, 0x2b, 0x36);
            case THEME_GITHUB_DARK -> new Color(0x0d, 0x11, 0x17);
            case THEME_NORD -> new Color(0x2e, 0x34, 0x40);
            case THEME_DARK -> new Color(0x1e, 0x1e, 0x1e);
            case THEME_SEPIA -> new Color(0xfb, 0xf1, 0xc7);
            default -> Color.WHITE;
        };
    }

    public Color getTextColor() {
        return switch (currentTheme) {
            case THEME_ATOM_ONE_DARK -> new Color(0xab, 0xb2, 0xbf);
            case THEME_DRACULA -> new Color(0xf8, 0xf8, 0xf2);
            case THEME_MONOKAI -> new Color(0xf8, 0xf8, 0xf2);
            case THEME_SOLARIZED_LIGHT -> new Color(0x65, 0x7b, 0x83);
            case THEME_SOLARIZED_DARK -> new Color(0x83, 0x94, 0x96);
            case THEME_GITHUB_DARK -> new Color(0xc9, 0xd1, 0xd9);
            case THEME_NORD -> new Color(0xd8, 0xde, 0xe9);
            case THEME_DARK -> new Color(0xd4, 0xd4, 0xd4);
            case THEME_SEPIA -> new Color(0x3c, 0x38, 0x36);
            default -> new Color(0x24, 0x29, 0x2e);
        };
    }

    private String getCodeBg() {
        return switch (currentTheme) {
            case THEME_ATOM_ONE_DARK -> "#2c313a";
            case THEME_DRACULA -> "#44475a";
            case THEME_MONOKAI -> "#3e3d32";
            case THEME_SOLARIZED_LIGHT -> "#eee8d5";
            case THEME_SOLARIZED_DARK -> "#073642";
            case THEME_GITHUB_DARK -> "#161b22";
            case THEME_NORD -> "#3b4252";
            case THEME_DARK -> "#2d2d2d";
            case THEME_SEPIA -> "#ebdbb2";
            default -> "#f6f8fa";
        };
    }

    private String getTableHeaderBg() {
        return switch (currentTheme) {
            case THEME_ATOM_ONE_DARK -> "#2c313a";
            case THEME_DRACULA -> "#44475a";
            case THEME_MONOKAI -> "#3e3d32";
            case THEME_SOLARIZED_LIGHT -> "#eee8d5";
            case THEME_SOLARIZED_DARK -> "#073642";
            case THEME_GITHUB_DARK -> "#161b22";
            case THEME_NORD -> "#3b4252";
            case THEME_DARK -> "#333";
            case THEME_SEPIA -> "#ebdbb2";
            default -> "#f6f8fa";
        };
    }
}
