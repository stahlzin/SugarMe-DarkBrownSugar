package br.com.mateus.sugarme.Builder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;

import java.util.List;

public class DetailLineChart extends LineChart {

    protected Paint mYAxisSafeZonePaint;

    public DetailLineChart(Context context) {
        super(context);
    }

    public DetailLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mYAxisSafeZonePaint = new Paint();
        mYAxisSafeZonePaint.setStyle(Paint.Style.FILL);
        mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        List<LimitLine> limitLines = mAxisLeft.getLimitLines();

        if (limitLines == null || limitLines.size() != 2)
            return;

        float[] pts = new float[4];
        LimitLine l1 = limitLines.get(0);
        LimitLine l2 = limitLines.get(1);

        pts[1] = l1.getLimit();
        pts[3] = l2.getLimit();

        mLeftAxisTransformer.pointValuesToPixel(pts);

        canvas.drawRect(mViewPortHandler.contentLeft(), pts[1], mViewPortHandler.contentRight(), pts[3], mYAxisSafeZonePaint);

        super.onDraw(canvas);
    }

    public void setSafeZoneColor(int color) {
        mYAxisSafeZonePaint.setColor(color);
    }
}