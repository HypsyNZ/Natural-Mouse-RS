package com.github.hypsynz.naturalmouse.support;

import com.github.hypsynz.naturalmouse.api.MouseInfoAccessor;

import java.awt.*;

public class DefaultMouseInfoAccessor implements MouseInfoAccessor {

  @Override
  public Point getMousePosition() {
    return MouseInfo.getPointerInfo().getLocation();
  }
}
