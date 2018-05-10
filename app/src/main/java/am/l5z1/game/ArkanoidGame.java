package am.l5z1.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

import am.l5z1.ArkanoidView;
import am.l5z1.R;

public class ArkanoidGame extends Thread implements SensorEventListener {

    private Context context;
    private ArkanoidView arkanoidView;
    private SensorManager sensorManager;
    private Sensor sensor;

    private Vector<Brick> bricks;
    private Pad pad;
    private Ball ball;

    private Handler handler;
    private Runnable invalidator;

    private int width;
    private int height;
    private int remainingBalls;
    private int gameSpeed;
    private boolean speedPhase;
    private boolean stopped;

    public ArkanoidGame(final Context context, final ArkanoidView arkanoidView) {
        this.context = context;
        this.arkanoidView = arkanoidView;
        handler = new Handler(Looper.getMainLooper());
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (sensor == null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, R.string.error_no_sensor, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        bricks = new Vector<>();
        width = arkanoidView.getWidth();
        height = arkanoidView.getHeight();
        remainingBalls = 3;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                bricks.add(new Brick(j*width/10, i*height/30, width/10, height/30));
            }
        }
        pad = new Pad(width/2 - width/20*2f, height - height/60, width/10*2f, height/60, width);
        ball = new Ball(width/2, height - height/60 - height/25 + 1, height/50);
        ball.setSpeed(height/70);

        invalidator = new Runnable() {
            @Override
            public void run() {
                arkanoidView.invalidate();
            }
        };

        gameSpeed = 15;
        speedPhase = false;
        stopped = false;
    }

    @Override
    public void run() {
        stopped = false;
        while (!stopped) {
            try {
                Thread.sleep(gameSpeed);
            } catch (InterruptedException e) {
                Log.w("thread-", "Interrupted!");
            }
            if (stopped) {
                break;
            }
            ball.move();
            pad.move();
            checkCollision();
            handler.post(invalidator);
            if (won()) {
                stopGame();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(context, R.string.won, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float speed = event.values[1];
        pad.setSpeed(speed);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void checkCollision() {

        RectF ballRect = ball.getBounds();

        if (ballRect.left <= 0) {
            ball.setSpeedX(Math.abs(ball.getSpeedX()));
        }
        if (ballRect.right >= width) {
            ball.setSpeedX(-Math.abs(ball.getSpeedX()));
        }
        if (ballRect.top <= 0) {
            ball.setSpeedY(Math.abs(ball.getSpeedY()));
        }
        if (ballRect.bottom >= height) {
            if (remainingBalls > 1 ) {
                remainingBalls--;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (remainingBalls > 1) {
                            Toast toast = Toast.makeText(context, remainingBalls + " " + context.getString(R.string.balls_left), Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(context, remainingBalls + " " + context.getString(R.string.ball_left), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
                pad = new Pad(width/2 - width/20*2f, height - height/60, width/10*2f, height/60, width);
                ball = new Ball(width/2, height - height/60 - height/25 + 1, height/50);
                ball.setSpeed(height/70);
                if (speedPhase) {
                    ball.setSpeed(ball.getSpeed()*1.25f);
                }
            } else {
                lost();
                ball.setSpeedY(-Math.abs(ball.getSpeedY()));
            }
        }

        float speedX = ball.getSpeedX()*ball.getSpeed();
        float speedY = ball.getSpeedY()*ball.getSpeed();

        Vector<Brick> temp;
        synchronized (this) {
            temp = new Vector<>(bricks);
        }
        ArrayList<Brick> bricksToRemove = new ArrayList<>();

        boolean xCollision = false;
        boolean yCollision = false;
        for (Brick brick : temp) {
            RectF brickRect = brick.getBounds();
            if ((ballRect.right + speedX >= brickRect.left) && (ballRect.left + speedX <= brickRect.right) &&
                (ballRect.bottom + speedY >= brickRect.top) && (ballRect.top + speedY <= brickRect.bottom)) {
                if ((ballRect.right < brickRect.left) && (ballRect.right + speedX >= brickRect.left)) {
                    xCollision = true;
                    bricksToRemove.add(brick);
                } else if ((ballRect.left > brickRect.right) && (ballRect.left + speedX <= brickRect.right)) {
                    xCollision = true;
                    bricksToRemove.add(brick);
                }
                if ((ballRect.bottom < brickRect.top) && (ballRect.bottom + speedY >= brickRect.top)) {
                    yCollision = true;
                    bricksToRemove.add(brick);
                } else if ((ballRect.top > brickRect.bottom) && (ballRect.top + speedY) <= brickRect.bottom) {
                    yCollision = true;
                    bricksToRemove.add(brick);
                }
            }
        }
        if (xCollision) {
            ball.setSpeedX(-ball.getSpeedX());
        }
        if (yCollision) {
            ball.setSpeedY(-ball.getSpeedY());
        }
        synchronized (this) {
            for (Brick brick : bricksToRemove) {
                bricks.remove(brick);
            }
        }

        if (!speedPhase && bricks.size() < 31) {
            ball.setSpeed(ball.getSpeed()*1.25f);
            speedPhase = true;
        }

        RectF padRect = pad.getBounds();
        float padSpeed = pad.getSpeed();
        if (padRect.right + padSpeed > width) {
            padSpeed = 0;
        }
        if (padRect.left + padSpeed < 0) {
            padSpeed = 0;
        }
        if ((ballRect.right + speedX >= padRect.left + padSpeed) && (ballRect.left + speedX <= padRect.right + padSpeed) &&
                (ballRect.bottom + speedY >= padRect.top) && (ballRect.top + speedY <= padRect.bottom)) {
            if ((ballRect.right < padRect.left) && (ballRect.right + speedX >= padRect.left)) {
                ball.setSpeedX(-ball.getSpeedX());
            } else if ((ballRect.left > padRect.right) && (ballRect.left + speedX <= padRect.right + padSpeed)) {
                ball.setSpeedX(-ball.getSpeedX());
            }
            if ((ballRect.bottom < padRect.top) && (ballRect.bottom + speedY >= padRect.top)) {
                ball.setSpeedY(-ball.getSpeedY());
            } else if ((ballRect.top > padRect.bottom) && (ballRect.top + speedY) <= padRect.bottom) {
                ball.setSpeedY(-ball.getSpeedY());
            }
        }
    }

    private boolean won() {
        return (bricks.size() < 1);
    }

    private void lost() {
        stopGame();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, R.string.lost, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void draw(Canvas canvas) {
        Vector<Brick> temp;
        synchronized (this) {
            temp = new Vector<>(bricks);
        }
        for (Brick brick : temp) {
            brick.draw(canvas);
        }
        pad.draw(canvas);
        ball.draw(canvas);
    }

    public void stopGame() {
        stopped = true;
    }

    public void registerListener() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME, Sensor.REPORTING_MODE_ON_CHANGE);
    }

    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    public ArkanoidGame copyGame() {
        ArkanoidGame game = new ArkanoidGame(context, arkanoidView);
        game.sensorManager = sensorManager;
        game.pad = pad;
        game.ball = ball;
        game.bricks = bricks;
        game.gameSpeed = gameSpeed;
        game.handler = handler;
        game.invalidator = invalidator;
        game.height = height;
        game.width = width;
        game.remainingBalls = remainingBalls;
        game.speedPhase = speedPhase;
        arkanoidView.setGame(game);
        return game;
    }
}
