/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import tool.compet.core.config.DkConfig;
import tool.compet.core.type.DkCallback;

/**
 * Utility class for text processing for TextView.
 */
public class DkTextViews {
    public static float calcTextSize(int fontSize) {
        return fontSize * DkConfig.device.density;
    }

    public static Spanned getSpannedText(String text, boolean hasColor, int color, boolean isBold, boolean hasUnderline) {
        if (hasColor) {
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            text = "<font color=\"%s\">" + text + "</font>";
            text = String.format(text, hexColor);
        }

        if (isBold) {
            text = "<b>" + text + "</b>";
        }

        if (hasUnderline) {
            text = "<u>" + text + "</u>";
        }

        return Html.fromHtml(text);
    }

    public static void makeUnderlineTagClickable(TextView textView, DkCallback<View> clickCb) {
        makeUnderlineTagClickable(textView, textView.getText().toString(), clickCb);
    }

    public static void makeUnderlineTagClickable(TextView textView, String textInHtml, DkCallback<View> clickCb) {
        Spanned spanned = Html.fromHtml(textInHtml);
        SpannableStringBuilder builder = new SpannableStringBuilder(spanned);
        UnderlineSpan[] urls = builder.getSpans(0, spanned.length(), UnderlineSpan.class);

        if (urls != null) {
            for (UnderlineSpan span : urls) {
                int start = builder.getSpanStart(span);
                int end = builder.getSpanEnd(span);
                int flags = builder.getSpanFlags(span);

                ClickableSpan clickable = new ClickableSpan() {
                    public void onClick(@NonNull View view) {
                        if (clickCb != null) {
                            clickCb.call(view);
                        }
                    }
                };

                builder.setSpan(clickable, start, end, flags);
            }
        }

        textView.setText(builder);
        textView.setLinksClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void applyFont(Context context, TextView tv) {
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/customFont"));
    }

    public static float[] calcTextViewDrawPoint(Rect bounds, float cx, float cy) {
        float halfWidth = (bounds.right - bounds.left) / 2f;
        float halfHeight = (bounds.bottom - bounds.top) / 2f;

        return new float[]{cx - halfWidth - bounds.left, cy + halfHeight - bounds.bottom};
    }

    public static float[] getTextViewDrawPoint(Rect bounds, float leftBottomX, float leftBottomY) {
        return new float[]{leftBottomX - bounds.left, leftBottomY - bounds.bottom};
    }

    public static void scaleTextSize(TextView tv, float factor) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize() * factor);
    }
}
