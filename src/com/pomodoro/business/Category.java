package com.pomodoro.business;

public class Category {
  private String name;
  private boolean isRemovable = true;

  public Category(String name) {
    this.name = name;
  }

  public Category(String name, boolean isRemovable) {
    this.name = name;
    this.isRemovable = isRemovable;
  }

  public String getName() {
    return name;
  }

  public boolean isRemovable() {
    return isRemovable;
  }

  @Override
  public String toString() {
    return name;
  }
}
