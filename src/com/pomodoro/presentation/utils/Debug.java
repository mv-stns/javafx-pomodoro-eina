package com.pomodoro.presentation.utils;

import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class Debug {
  public static void debugNode(Node... nodes) {
    for (Node node : nodes) {
      if (node instanceof Region) {
        ((Region) node)
            .setBorder(
                new Border(
                    new BorderStroke(
                        Color.RED,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        BorderWidths.DEFAULT)));
      }
    }
  }
}
