package com.pomodoro.presentation.scenes;

import javafx.scene.layout.Pane;

public class Controller<P extends Pane> {
  protected P root;

  public P getRoot() {
    return root;
  }
}
