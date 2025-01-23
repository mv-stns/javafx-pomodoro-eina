package com.pomodoro.presentation.views;

import javafx.scene.layout.Pane;

public class Controller<P extends Pane> {
  protected P root;

  public P getRoot() {
    return root;
  }
}
