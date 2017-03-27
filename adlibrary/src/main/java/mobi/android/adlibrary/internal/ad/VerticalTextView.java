package mobi.android.adlibrary.internal.ad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vincent on 2016/10/30.
 */
public class VerticalTextView extends TextView {
    private Rect bounds = new Rect();
    private TextPaint textPaint;
    private int color;

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        color = getCurrentTextColor();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        textPaint = getPaint();
        textPaint.getTextBounds((String) getText(), 0, getText().length(), bounds);
        int w= (int) (-textPaint.ascent()+textPaint.descent())+getPaddingRight()+getPaddingLeft();
        int h=bounds.width()+getPaddingTop();
        setMeasuredDimension(w,h);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint.setColor(color);
        canvas.rotate(90, 0, 0);
        canvas.drawText((String) getText(), getPaddingTop(), -getPaddingLeft(), textPaint);
    }

}
