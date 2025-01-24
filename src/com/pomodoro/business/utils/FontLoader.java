package com.pomodoro.business.utils;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.text.Font;

public class FontLoader {
  private static final String FONT_PATH = "/com/pomodoro/assets/fonts/";
  private static final Map<String, Font> loadedFonts = new HashMap<>();

  public static void loadFonts() {
    loadFont("Geist-Regular.otf");
    loadFont("Geist-Light.otf");
    loadFont("Geist-Medium.otf");
    loadFont("Geist-SemiBold.otf");
    loadFont("Geist-Bold.otf");
    loadFont("Geist-Black.otf");
  }

  public static Font getFont(String fontName, double size) {
    String key = fontName + size;
    if (!loadedFonts.containsKey(key)) {
      Font font = Font.loadFont(FontLoader.class.getResourceAsStream(FONT_PATH + fontName), size);
      loadedFonts.put(key, font);
    }
    return loadedFonts.get(key);
  }

  private static void loadFont(String fontName) {
    Font.loadFont(FontLoader.class.getResourceAsStream(FONT_PATH + fontName), 12);
  }

  // Convenience methods
  public static Font regular(double size) {
    return getFont("Geist-Regular.otf", size);
  }

  public static Font light(double size) {
    return getFont("Geist-Light.otf", size);
  }

  public static Font medium(double size) {
    return getFont("Geist-Medium.otf", size);
  }

  public static Font semiBold(double size) {
    return getFont("Geist-SemiBold.otf", size);
  }

  public static Font bold(double size) {
    return getFont("Geist-Bold.otf", size);
  }

  public static Font black(double size) {
    return getFont("Geist-Black.otf", size);
  }
}
