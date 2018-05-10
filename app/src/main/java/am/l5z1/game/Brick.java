package am.l5z1.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

class Brick {

    private RectF brick;
    private Paint paint;

    public Brick(float x, float y, float width, float height) {
        brick = new RectF(x, y, x + width, y + height);
        paint = new Paint();
        int tempX = Math.round(x/width);
        int tempY = Math.round(y/height);
        if ((tempX+tempY) % 2 == 0) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.CYAN);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(brick, paint);
    }

    public RectF getBounds() {
        return brick;
    }
}
