package com.locator_app.locator.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class StrokeTransformation extends BitmapTransformation {

    private final int strokeWidth;
    private final int strokeColor;

    public StrokeTransformation(Context context, int strokeWidth, int strokeColor) {
        super(context);
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {

        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(strokeColor);
        float circleMiddle = size / 2f;
        canvas.drawCircle(circleMiddle, circleMiddle, circleMiddle, paint);

        paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float imageRadius = (size - strokeWidth) / 2;
        canvas.drawCircle(circleMiddle, circleMiddle, imageRadius, paint);

        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
