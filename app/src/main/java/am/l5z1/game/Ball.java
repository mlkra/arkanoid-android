package am.l5z1.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

class Ball {

    private RectF ball;
    private Paint paint;

    private float speed;
    private float speedX;
    private float speedY;

    public Ball(float x, float y, float r) {
        ball = new RectF(x - r, y - r, x + r, y + r);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        speed = 4f;
        speedX = 0.5f;
        speedY = -0.5f;
    }

    public void draw(Canvas canvas) {
        canvas.drawOval(ball, paint);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
//        if (speedY > 0) {
//            speedY = 1 - Math.abs(speedX);
//        } else {
//            speedY = -(1 - Math.abs(speedX));
//        }
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
//        if (speedX > 0) {
//            speedX = 1 - Math.abs(speedY);
//        } else {
//            speedX = -(1 - Math.abs(speedY));
//        }
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public RectF getBounds() {
        return ball;
    }

    public void move() {
        ball.offset(speed*speedX, speed*speedY);
    }
}
