package com.example.game.TapiocaLauncher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.game.R;
import com.example.game.ScoreManager;
import java.util.Observable;
import java.util.Observer;
import java.util.List;

class VisualView implements Observer{

  private Background background;
  // Dimensions of the screen
//  private int screenX, screenY;
//  // Used for resizing and display on different devices
//  private float screenRatioX, screenRatioY;
//  private Paint paint;
//  private SurfaceHolder surfaceHolder;
  //private int level = 1;

  // Keeps track of scores, observer
  private ScoreManager scoreManager;
  private Context context;

//  static GameManager gameMan;

  GameFacade gameFacade;
  Launcher launcher;
  List<Ball> balls;
  int score, level;
  int turnCounter = 0;
  Resources res;
  private Paint paint;
  private SurfaceHolder surfaceHolder;
  private int screenX, screenY;

  private Bitmap launcherOrientation1, launcherOrientation2, launcherOrientation3, launcherOrientation4;
  private Bitmap tapiocaRed, tapiocaBrown;

  VisualView(int screenX, int screenY, Resources res, SurfaceHolder surfaceHolder, Context context, ScoreManager scoreManager) {
    this.screenX = screenX;
    this.screenY = screenY;
    this.res = res;
    this.surfaceHolder = surfaceHolder;
    this.scoreManager = scoreManager;
    this.context = context;

    // Create screen ratio
    background = new Background(screenX, screenY, res);

    paint = new Paint();
    paint.setTextSize(64);
    paint.setColor(Color.BLACK);

    createBitmaps();
  }

  void draw() {

    if (surfaceHolder.getSurface().isValid()) {

      Canvas canvas = surfaceHolder.lockCanvas();
      canvas.drawBitmap(background.getBackground(), background.getX(), background.getY(), paint);
      drawBalls(canvas); //Draw the balls
      drawLauncher(canvas); // Draw Launcher
      drawText(canvas);  // Draw the level and score
      surfaceHolder.unlockCanvasAndPost(canvas);
    }
  }


  private void drawLauncher(Canvas canvas) {
    canvas.drawBitmap(getLauncherOrientation(), launcher.getX(), launcher.getY(), paint);
  }

  private void createBitmaps() {
    createLauncherBitmaps();
    createTapiocaBitmaps();
  }

  private void createTapiocaBitmaps() {
    tapiocaBrown = BitmapFactory.decodeResource(res, R.drawable.brown);
    tapiocaRed = BitmapFactory.decodeResource(res, R.drawable.red);

    int width = tapiocaBrown.getWidth() / 2; // 157
    int height = tapiocaBrown.getHeight() / 2; // 136
    tapiocaBrown = Bitmap.createScaledBitmap(tapiocaBrown, width, height, false);

    width = tapiocaRed.getWidth() / 2; // 157
    height = tapiocaRed.getHeight() / 2; // 136
    tapiocaRed = Bitmap.createScaledBitmap(tapiocaRed, width, height, false);

  }

  private void createLauncherBitmaps() {
    launcherOrientation1 = BitmapFactory.decodeResource(res, R.drawable.tapioca1);
    launcherOrientation2 = BitmapFactory.decodeResource(res, R.drawable.tapioca2);
    launcherOrientation3 = BitmapFactory.decodeResource(res, R.drawable.tapioca3);
    launcherOrientation4 = BitmapFactory.decodeResource(res, R.drawable.tapioca4);
    int width = launcherOrientation1.getWidth() / 2;
    int height = launcherOrientation1.getHeight() / 2;

    launcherOrientation1 = Bitmap.createScaledBitmap(launcherOrientation1, width, height, false);
    launcherOrientation2 = Bitmap.createScaledBitmap(launcherOrientation2, width, height, false);
    launcherOrientation3 = Bitmap.createScaledBitmap(launcherOrientation3, width, height, false);
    launcherOrientation4 = Bitmap.createScaledBitmap(launcherOrientation4, width, height, false);
    Log.e("", "" + width + " "+ height);
  }

   private Bitmap getLauncherOrientation() {
    // Scales the bitmap down
      if (launcher.getSpeedX() != 0 && launcher.getSpeedY() != 0) {
        switch (turnCounter) {
          case 0:
            turnCounter++;
            return launcherOrientation1;
          case 1:
            turnCounter++;
            return launcherOrientation2;
          case 2:
            turnCounter++;
            return launcherOrientation3;
          case 3:
            turnCounter = 0;
            return launcherOrientation4;
          default:
            turnCounter = 0;
            return launcherOrientation1;
        }
      } else {
        return launcherOrientation1;
      }
  }

  private void drawBalls(Canvas canvas) {
    for(Ball ball : balls) {
      drawBall(ball, canvas);
    }
  }

  private void drawBall(Ball ball, Canvas canvas) {
    Bitmap orientation1;
    if (ball.getHp() == 1) {
      orientation1 = tapiocaBrown;
    } else { //ball hp == 2
      orientation1 = tapiocaRed;
    }
    canvas.drawBitmap(orientation1, ball.getX(), ball.getY(), paint);
  }
  private void drawText(Canvas canvas) {
    canvas.drawText(context.getString(R.string.score) + score + "", 5, screenY - 30, paint);
    canvas.drawText(context.getString(R.string.level) + (level-1 ) + "", 5, screenY - 100, paint);
  }

  @Override
  public void update(Observable o, Object arg) {
    gameFacade = (GameFacade) arg;
    launcher = gameFacade.getLauncher();
    balls = gameFacade.getBalls();
    score = gameFacade.getScore();
    level = gameFacade.getLevel();
  }
}
