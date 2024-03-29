package com.shovon.mathology.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class RoundedBackgroundSpan extends ReplacementSpan {

    private int backgroundColor = 0;

    public RoundedBackgroundSpan(int backgroundColor) {
        super();
        this.backgroundColor = backgroundColor;
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);

        paint.setColor(backgroundColor);
        int CORNER_RADIUS = 8;
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText(text, start, end, x, y, paint);

    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end));
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }
}