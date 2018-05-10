package am.l5z1.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

class Pad {

    private RectF pad;
    private Paint paint;

    private float max;
    private float speed;

    public Pad(float x, float y, float width, float height, float max) {
        pad = new RectF(x, y, x + width, y + height);
        this.max = max;
        paint = new Paint();
        paint.setColor(Color.GREEN);

        speed = 0;
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(pad, paint);
    }

    public RectF getBounds() {
        return pad;
    }

    public void move() {
        pad.offset(speed, 0);
        if (pad.left < 0) {
            float width = pad.width();
            pad.left = 0;
            pad.right = width;
        } else if (pad.right > max) {
            pad.left = max - pad.width();
            pad.right = max;
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed*max/130;
    }
}
