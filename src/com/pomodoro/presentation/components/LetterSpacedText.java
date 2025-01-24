package com.pomodoro.presentation.components;

import javafx.beans.DefaultProperty;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

@DefaultProperty("text")
public class LetterSpacedText extends FlowPane {
  private final StringProperty text = new SimpleStringProperty("");
  private final ObjectProperty<Font> font = new SimpleObjectProperty<>();
  private final ObjectProperty<Color> fill = new SimpleObjectProperty<>();
  private final DoubleProperty letterSpacing = new SimpleDoubleProperty(-2);

  public LetterSpacedText() {
    this("");
  }

  public LetterSpacedText(String s) {
    setupComponent();
    setText(s);
  }

  public LetterSpacedText(String s, double spacing) {
    setupComponent();
    setText(s);
    setLetterSpacing(spacing);
  }

  private void setupComponent() {
    setVgap(0);
    setRowValignment(javafx.geometry.VPos.CENTER);

    // Add listeners to properties
    text.addListener((obs, old, newValue) -> updateText());
    font.addListener((obs, old, newValue) -> updateFont());
    fill.addListener((obs, old, newValue) -> updateFill());
    letterSpacing.addListener((obs, old, newValue) -> setHgap(newValue.doubleValue()));
  }

  // Text Property
  public String getText() {
    return text.get();
  }

  public void setText(String value) {
    text.set(value);
  }

  public StringProperty textProperty() {
    return text;
  }

  // Font Property
  public Font getFont() {
    return font.get();
  }

  public void setFont(Font value) {
    font.set(value);
  }

  public ObjectProperty<Font> fontProperty() {
    return font;
  }

  // Fill Property
  public Color getFill() {
    return fill.get();
  }

  public void setFill(Color value) {
    fill.set(value);
  }

  public ObjectProperty<Color> fillProperty() {
    return fill;
  }

  // Letter Spacing Property
  public double getLetterSpacing() {
    return letterSpacing.get();
  }

  public void setLetterSpacing(double value) {
    letterSpacing.set(value);
  }

  public DoubleProperty letterSpacingProperty() {
    return letterSpacing;
  }

  // Private update methods
  private void updateText() {
    getChildren().clear();
    String value = getText();
    if (value != null) {
      for (int i = 0; i < value.length(); i++) {
        Text textNode = new Text("" + value.charAt(i));
        if (getFont() != null) textNode.setFont(getFont());
        if (getFill() != null) textNode.setFill(getFill());
        getChildren().add(textNode);
      }
    }
  }

  private void updateFont() {
    for (Node t : getChildren()) {
      ((Text) t).setFont(getFont());
    }
  }

  private void updateFill() {
    for (Node t : getChildren()) {
      ((Text) t).setFill(getFill());
    }
  }
}
