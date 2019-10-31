package com.example.game.TapiocaLauncher;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.game.R;
import com.example.game.ScoreManager;

import java.util.List;

// Tapioca that the user launches to clear the stage
class Launcher {

  // Used to animate the ball and move it
  private boolean isMoving = false;
  private boolean readyToLaunch = true;
  // The top left coordinates of the ball
  private int x, y;
  // The width, height, radius of the ball
  private int width, height, radius;
  // Used to animate the ball
  private int turnCounter = 0;
  // Used to reset the ball after a turn
  private int count = 0;
  // Keeps track of scores
  private ScoreManager scoreManager;
  // The bitmaps of the ball to animate the ball's spinning
  private Bitmap orientation1, orientation2, orientation3, orientation4;
  // The screen length and width to calculate if ball is out of bounds
  private int screenX, screenY;
  private double gravity, speedX, speedY;
  private double gravityX, gravityY;

  Launcher(int screenX, int screenY, Resources res, ScoreManager scoreManager) {
    this.scoreManager = scoreManager;
    orientation1 = BitmapFactory.decodeResource(res, R.drawable.tapioca1);
    orientation2 = BitmapFactory.decodeResource(res, R.drawable.tapioca2);
    orientation3 = BitmapFactory.decodeResource(res, R.drawable.tapioca3);
    orientation4 = BitmapFactory.decodeResource(res, R.drawable.tapioca4);

    // Scales the bitmap down
    width = orientation1.getWidth() / 2;
    height = orientation1.getHeight() / 2;

    orientation1 = Bitmap.createScaledBitmap(orientation1, width, height, false);
    orientation2 = Bitmap.createScaledBitmap(orientation2, width, height, false);
    orientation3 = Bitmap.createScaledBitmap(orientation3, width, height, false);
    orientation4 = Bitmap.createScaledBitmap(orientation4, width, height, false);

    // Start the ball halfway horizontally in the screen further down
    y = 1850;
    x = screenX / 2;
    this.screenX = screenX;
    this.screenY = screenY;
    radius = width / 2;
    gravity = 5;
  }

  // Returns the bitmap of this tapioca, whether it is spinning or not
  Bitmap getLauncher() {
    if (isMoving) {
      switch (turnCounter) {
        case 0:
          turnCounter++;
          return orientation1;
        case 1:
          turnCounter++;
          return orientation2;
        case 2:
          turnCounter++;
          return orientation3;
        case 3:
          turnCounter = 0;
          return orientation4;
        default:
          turnCounter = 0;
          return orientation1;
      }
    } else return orientation1;
  }

  private void move(List<Ball> balls) {
    x += speedX;
    y += speedY;

    if (x < 0) {
      x = 0;
      speedX = -speedX;
    } else if (x + width > screenX) {
      x = screenX - width;
      speedX = -speedX;
    }
    if (y < 0) {
      y = 0;
      speedY = -speedY;
    } else if (y + height > screenY) {
      y = screenY - height;
      speedY = -speedY;
    }
    detectCollisions(balls);
  }

  void moveBall(double startX, double startY, double endX, double endY) {
    isMoving = true;
    readyToLaunch = false;
    speedX = Math.cos(Math.atan2(endY - startY, endX - startX)) * 300;
    speedY = Math.sin(Math.atan2(endY - startY, endX - startX)) * 300;
    gravityX = Math.abs(speedX) / 50;
    gravityY = Math.abs(speedY) / 50;
  }

  void update(List<Ball> balls) {
    if (isMoving) {
      move(balls);
      speedX = decrement(speedX, gravityX);
      speedY = decrement(speedY, gravityY);
      if (speedX == 0 && speedY == 0) {
        isMoving = false;
      }
    }
    if (!isMoving
        && count != 60) { // resets ball after 1 second of non-movement by counting 60 frames
      count++;
    }
    if (count == 60) {
      count = 0;
      y = 1850;
      x = screenX / 2;
      readyToLaunch = true;
      for (int i = 0; i < balls.size(); i++) {
        if (balls.get(i).isHit()) {
          balls.get(i).setHit(false);
        }
      }
    }
  }

  private double decrement(double speed, double gravity) { // reduces speed by gravity amount
    // Log.d("", "decremented " + speed);
    if (speed >= 0) {
      speed -= gravity;
      if (speed < 0) {
        speed = 0;
      }
    } else {
      speed += gravity;
      if (speed > 0) {
        speed = 0;
      }
    }
    return speed;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  boolean isReadyToLaunch() {
    return readyToLaunch;
  }

  private int getCentreX() {
    return this.x + radius;
  }

  private int getCentreY() {
    return this.y + radius;
  }

  // Checks for collisions between this and other tapioca, also adds points in scoreManager.
  private void detectCollisions(List<Ball> balls) {

    for (int i = 0; i < balls.size(); i++) {
      if (Math.hypot(getCentreX() - balls.get(i).centreX, getCentreY() - balls.get(i).centreY)
          <= 2 * radius) {
        if (!balls.get(i).isHit()) {
          balls.get(i).setHp(balls.get(i).getHp() - 1);
          Log.d("", balls.get(i).getHp() + " ");
          balls.get(i).setHit(true);
          if (balls.get(i).getHp() == 0) {
            balls.remove(i);
            i--;
            scoreManager.addScore();
          }
        }
      }
    }
  }
}
