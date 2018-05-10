package am.l5z1;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import am.l5z1.game.ArkanoidGame;

public class ArkanoidView extends View {

    private ArkanoidGame game;

    public ArkanoidView(Context context) {
        super(context);
    }

    public ArkanoidView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (game != null) {
            game.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int newHeight = width * 5 / 6;
        int newWidth;

        if (newHeight > height) {
            newWidth = height * 6 / 5;
            newHeight = height;
        } else {
            newWidth = width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY));
    }

    public void setGame(ArkanoidGame game) {
        this.game = game;
    }
}
