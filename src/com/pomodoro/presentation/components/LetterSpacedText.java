package com.pomodoro.presentation.components;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LetterSpacedText extends FlowPane {
  private Font font;
  private Color fill;
  private double letterSpacing = -2;

  public LetterSpacedText() {
    this("");
  }

  public LetterSpacedText(String s) {
    setVgap(0);
    setRowValignment(javafx.geometry.VPos.CENTER);
    setText(s);
  }

  public void setText(String s) {
    getChildren().clear();
    for (int i = 0; i < s.length(); i++) {
      Text text = new Text("" + s.charAt(i));
      getChildren().add(text);
    }
    setHgap(letterSpacing); // Setze spacing nach Erstellung der Texte
    setFont(this.font);
    setFill(this.fill);
  }

  public void setFont(Font font) {
    if (font != null) {
      this.font = font;
      for (Node t : getChildren()) {
        ((Text) t).setFont(font);
      }
    }
  }

  public void setFill(Color fill) {
    if (fill != null) {
      this.fill = fill;
      for (Node t : getChildren()) {
        ((Text) t).setFill(fill);
      }
    }
  }

  public void setLetterSpacing(double spacing) {
    this.letterSpacing = spacing;
    setHgap(spacing);
  }

  public double getLetterSpacing() {
    return letterSpacing;
  }
}
