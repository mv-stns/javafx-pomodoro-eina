package com.pomodoro.business;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Category category = (Category) o;
    return Objects.equals(name.toLowerCase(), category.name.toLowerCase());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name.toLowerCase());
  }
}
