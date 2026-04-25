package com.zdroba.multipitchbuddy.ui;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

public class GradientGenerator {

    private static final Random random = new Random();

    public static Bitmap generate(int size) {
            return bilinear(size);
    }

    private static Bitmap bilinear(int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        int topLeft = randomColor();
        int topRight = randomColor();
        int bottomLeft = randomColor();
        int bottomRight = randomColor();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                float tx = (float) x / (size - 1);
                float ty = (float) y / (size - 1);

                int top = lerpColor(topLeft, topRight, tx);
                int bottom = lerpColor(bottomLeft, bottomRight, tx);
                int color = lerpColor(top, bottom, ty);

                bitmap.setPixel(x, y, color);
            }
        }

        return bitmap;
    }


    private static int lerpColor(int c1, int c2, float t) {
        int r = (int) (Color.red(c1) * (1 - t) + Color.red(c2) * t);
        int g = (int) (Color.green(c1) * (1 - t) + Color.green(c2) * t);
        int b = (int) (Color.blue(c1) * (1 - t) + Color.blue(c2) * t);
        return Color.rgb(r, g, b);
    }

    private static int randomColor() {
        return Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }
}