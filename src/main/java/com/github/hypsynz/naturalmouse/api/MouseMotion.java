package com.github.hypsynz.naturalmouse.api;

import com.github.hypsynz.naturalmouse.support.DoublePoint;
import com.github.hypsynz.naturalmouse.support.Flow;
import com.github.hypsynz.naturalmouse.support.MouseMotionNature;
import com.github.hypsynz.naturalmouse.support.mousemotion.Movement;
import com.github.hypsynz.naturalmouse.support.mousemotion.MovementFactory;
import com.github.hypsynz.naturalmouse.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayDeque;
import java.util.Random;

/**
 * Contains instructions to move cursor smoothly to the destination coordinates from where ever the cursor
 * currently is. The class is reusable, meaning user can keep calling it and the cursor returns in a random,
 * but reliable way, described in this class, to the destination.
 */
public class MouseMotion {
  private static final Logger log = LoggerFactory.getLogger(MouseMotion.class);
  private static final int SLEEP_AFTER_ADJUSTMENT_MS = 2;
  private final int minSteps;
  private final int effectFadeSteps;
  private final int reactionTimeBaseMs;
  private final int reactionTimeVariationMs;
  private final double timeToStepsDivider;
  private final Dimension screenSize;
  private final SystemCalls systemCalls;
  private final DeviationProvider deviationProvider;
  private final NoiseProvider noiseProvider;
  private final SpeedManager speedManager;
  private final OvershootManager overshootManager;
  private final int xDest;
  private final int yDest;
  private final Random random;
  private final MouseInfoAccessor mouseInfo;
  private Point mousePosition;

  /**
   * @param nature the nature that defines how mouse is moved
   * @param xDest  the x-coordinate of destination
   * @param yDest  the y-coordinate of destination
   * @param random the random used for unpredictability
   */
  public MouseMotion(MouseMotionNature nature, Random random, int xDest, int yDest) {
    this.deviationProvider = nature.getDeviationProvider();
    this.noiseProvider = nature.getNoiseProvider();
    this.systemCalls = nature.getSystemCalls();
    this.screenSize = systemCalls.getScreenSize();
    this.xDest = limitByScreenWidth(xDest);
    this.yDest = limitByScreenHeight(yDest);
    this.random = random;
    this.mouseInfo = nature.getMouseInfo();
    this.speedManager = nature.getSpeedManager();
    this.timeToStepsDivider = nature.getTimeToStepsDivider();
    this.minSteps = nature.getMinSteps();
    this.effectFadeSteps = nature.getEffectFadeSteps();
    this.reactionTimeBaseMs = nature.getReactionTimeBaseMs();
    this.reactionTimeVariationMs = nature.getReactionTimeVariationMs();
    this.overshootManager = nature.getOvershootManager();
  }

  /**
   * Blocking call, starts to move the cursor to the specified location from where it currently is.
   *
   * @throws InterruptedException when interrupted
   */
  public void move() throws InterruptedException {
    move((x, y) -> {
    });
  }

  public void moveClick() throws InterruptedException {
    moveClick((x, y) -> {
    });
  }


  private static Robot r;
  {
    try {
      r = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  private static int rand20to40 = getRandomNumberInRange(20, 40);
  private static int getRandomNumberInRange(int min, int max) {

    if (min >= max) {
      log.info("Min was greater than Max while generating a Random Number");
      return 0;
    }

    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }


  /**
   * Blocking call, starts to move the cursor to the specified location from where it currently is.
   *
   * @param observer Provide observer if you are interested receiving the location of mouse on every step
   * @throws InterruptedException when interrupted
   */
  public void moveClick(MouseMotionObserver observer) throws InterruptedException {
    updateMouseInfo();
    MovementFactory movementFactory = new MovementFactory(xDest, yDest, speedManager, overshootManager, screenSize);
    ArrayDeque<Movement> movements = movementFactory.createMovements(mousePosition);

    while (mousePosition.x != xDest || mousePosition.y != yDest) {
      if (movements.isEmpty()) {
        updateMouseInfo();
        movements = movementFactory.createMovements(mousePosition);
      }

      Movement movement = movements.removeFirst();

      double distance = movement.distance;
      long mouseMovementMs = movement.time;
      Flow flow = movement.flow;
      double xDistance = movement.xDistance;
      double yDistance = movement.yDistance;

      int steps = (int) Math.ceil(Math.min(distance, Math.max(mouseMovementMs / timeToStepsDivider, minSteps)));

      long startTime = systemCalls.currentTimeMillis();
      long stepTime = (long) (mouseMovementMs / (double) steps);

      updateMouseInfo();
      double simulatedMouseX = mousePosition.x;
      double simulatedMouseY = mousePosition.y;

      double deviationMultiplierX = (random.nextDouble() - 0.5) * 2;
      double deviationMultiplierY = (random.nextDouble() - 0.5) * 2;

      double completedXDistance = 0;
      double completedYDistance = 0;
      double noiseX = 0;
      double noiseY = 0;

      for (int i = 0; i < steps; i++) {
        double timeCompletion = i / (double) steps;

        double effectFadeStep = Math.max(i - (steps - effectFadeSteps) + 1, 0);
        double effectFadeMultiplier = (effectFadeSteps - effectFadeStep) / effectFadeSteps;

        double xStepSize = flow.getStepSize(xDistance, steps, timeCompletion);
        double yStepSize = flow.getStepSize(yDistance, steps, timeCompletion);

        completedXDistance += xStepSize;
        completedYDistance += yStepSize;
        double completedDistance = Math.hypot(completedXDistance, completedYDistance);
        double completion = Math.min(1, completedDistance / distance);
        //log.trace("Step: x: {} y: {} tc: {} c: {}", xStepSize, yStepSize, timeCompletion, completion);

        DoublePoint noise = noiseProvider.getNoise(random, xStepSize, yStepSize);
        DoublePoint deviation = deviationProvider.getDeviation(distance, completion);

        noiseX += noise.getX();
        noiseY += noise.getY();
        simulatedMouseX += xStepSize;
        simulatedMouseY += yStepSize;

        long endTime = startTime + stepTime * (i + 1);
        int mousePosX = MathUtil.roundTowards(
            simulatedMouseX +
            deviation.getX() * deviationMultiplierX * effectFadeMultiplier +
            noiseX * effectFadeMultiplier,
            movement.destX
        );

        int mousePosY = MathUtil.roundTowards(
            simulatedMouseY +
            deviation.getY() * deviationMultiplierY * effectFadeMultiplier +
            noiseY * effectFadeMultiplier,
            movement.destY
        );

        mousePosX = limitByScreenWidth(mousePosX);
        mousePosY = limitByScreenHeight(mousePosY);

        systemCalls.setMousePosition(
            mousePosX,
            mousePosY
        );

        // Allow other action to take place or just observe, we'll later compensate by sleeping less.
        observer.observe(mousePosX, mousePosY);

        long timeLeft = endTime - systemCalls.currentTimeMillis();
        sleepAround(Math.max(timeLeft, 0), 0);
      }

      updateMouseInfo();

      if (mousePosition.x != movement.destX || mousePosition.y != movement.destY) {

        systemCalls.setMousePosition(movement.destX, movement.destY);
        // Let's wait a bit before getting mouse info.
        sleepAround(SLEEP_AFTER_ADJUSTMENT_MS, 0);
        updateMouseInfo();
      }

      if (mousePosition.x != xDest || mousePosition.y != yDest) {
        sleepAround(reactionTimeBaseMs, reactionTimeVariationMs);
      }

      if (mousePosition.x == xDest && mousePosition.y == yDest) {
        Delay(rand20to40);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Delay(rand20to40);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Delay(rand20to40);
      }
    }
  }


  public void move(MouseMotionObserver observer) throws InterruptedException {
    updateMouseInfo();
    MovementFactory movementFactory = new MovementFactory(xDest, yDest, speedManager, overshootManager, screenSize);
    ArrayDeque<Movement> movements = movementFactory.createMovements(mousePosition);

    while (mousePosition.x != xDest || mousePosition.y != yDest) {
      if (movements.isEmpty()) {
        updateMouseInfo();
        movements = movementFactory.createMovements(mousePosition);
      }

      Movement movement = movements.removeFirst();

      double distance = movement.distance;
      long mouseMovementMs = movement.time;
      Flow flow = movement.flow;
      double xDistance = movement.xDistance;
      double yDistance = movement.yDistance;

      int steps = (int) Math.ceil(Math.min(distance, Math.max(mouseMovementMs / timeToStepsDivider, minSteps)));

      long startTime = systemCalls.currentTimeMillis();
      long stepTime = (long) (mouseMovementMs / (double) steps);

      updateMouseInfo();
      double simulatedMouseX = mousePosition.x;
      double simulatedMouseY = mousePosition.y;

      double deviationMultiplierX = (random.nextDouble() - 0.5) * 2;
      double deviationMultiplierY = (random.nextDouble() - 0.5) * 2;

      double completedXDistance = 0;
      double completedYDistance = 0;
      double noiseX = 0;
      double noiseY = 0;

      for (int i = 0; i < steps; i++) {
        double timeCompletion = i / (double) steps;

        double effectFadeStep = Math.max(i - (steps - effectFadeSteps) + 1, 0);
        double effectFadeMultiplier = (effectFadeSteps - effectFadeStep) / effectFadeSteps;

        double xStepSize = flow.getStepSize(xDistance, steps, timeCompletion);
        double yStepSize = flow.getStepSize(yDistance, steps, timeCompletion);

        completedXDistance += xStepSize;
        completedYDistance += yStepSize;
        double completedDistance = Math.hypot(completedXDistance, completedYDistance);
        double completion = Math.min(1, completedDistance / distance);
        //log.trace("Step: x: {} y: {} tc: {} c: {}", xStepSize, yStepSize, timeCompletion, completion);

        DoublePoint noise = noiseProvider.getNoise(random, xStepSize, yStepSize);
        DoublePoint deviation = deviationProvider.getDeviation(distance, completion);

        noiseX += noise.getX();
        noiseY += noise.getY();
        simulatedMouseX += xStepSize;
        simulatedMouseY += yStepSize;

        long endTime = startTime + stepTime * (i + 1);
        int mousePosX = MathUtil.roundTowards(
                simulatedMouseX +
                        deviation.getX() * deviationMultiplierX * effectFadeMultiplier +
                        noiseX * effectFadeMultiplier,
                movement.destX
        );

        int mousePosY = MathUtil.roundTowards(
                simulatedMouseY +
                        deviation.getY() * deviationMultiplierY * effectFadeMultiplier +
                        noiseY * effectFadeMultiplier,
                movement.destY
        );

        mousePosX = limitByScreenWidth(mousePosX);
        mousePosY = limitByScreenHeight(mousePosY);

        systemCalls.setMousePosition(
                mousePosX,
                mousePosY
        );

        // Allow other action to take place or just observe, we'll later compensate by sleeping less.
        observer.observe(mousePosX, mousePosY);

        long timeLeft = endTime - systemCalls.currentTimeMillis();
        sleepAround(Math.max(timeLeft, 0), 0);
      }

      // click




      updateMouseInfo();

      if (mousePosition.x != movement.destX || mousePosition.y != movement.destY) {

        systemCalls.setMousePosition(movement.destX, movement.destY);
        // Let's wait a bit before getting mouse info.
        sleepAround(SLEEP_AFTER_ADJUSTMENT_MS, 0);
        updateMouseInfo();
      }

      if (mousePosition.x != xDest || mousePosition.y != yDest) {
        sleepAround(reactionTimeBaseMs, reactionTimeVariationMs);
      }
    }
  }


  /**
   * Wait for a set delay using the Input Robot
   *
   * @param delay the amount of time to wait in Milliseconds
   */
  public static void Delay(int delay) {
    r.delay(delay);
  }

  private int limitByScreenWidth(int value) {
    return Math.max(0, Math.min(screenSize.width - 1, value));
  }

  private int limitByScreenHeight(int value) {
    return Math.max(0, Math.min(screenSize.height - 1, value));
  }

  private void sleepAround(long sleepMin, long randomPart) throws InterruptedException {
    long sleepTime = (long) (sleepMin + random.nextDouble() * randomPart);
    if (log.isTraceEnabled() && sleepTime > 0) {
      updateMouseInfo();
     // log.trace("Sleeping at ({}, {}) for {} ms", mousePosition.x, mousePosition.y, sleepTime);
    }
    systemCalls.sleep(sleepTime);
  }

  private void updateMouseInfo() {
    mousePosition = mouseInfo.getMousePosition();
  }

}
