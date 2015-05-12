package com.cyrillrx.android.component;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyrillrx.uremote.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Cyril Leroux
 *         Created on 16/06/2014.
 */
public class Console extends LinearLayout {

    private final static int MAX_LINES = 5;
    private final static int REMOVE_LINE_DURATION = 200;
    private final static int LETTER_FIXED_DELAY = 10;
    private final static int LETTER_VARIABLE_DELAY = 40;

    private LinearLayout mConsoleContent;
    private List<TextView> mLines = new ArrayList<>();

    private TextView mConsoleLineModel;

    public Console(Context context) {
        super(context);
        initViews(context);
    }

    public Console(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public Console(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    /** Init the component layout. */
    private void initViews(final Context context) {

        inflate(context, R.layout.console, this);
        mConsoleLineModel = (TextView) findViewById(R.id.tvConsoleLine);
        mConsoleContent = (LinearLayout) findViewById(R.id.consoleContent);
        // Remove the model from the view
        mConsoleContent.removeAllViews();
    }

    private static TextView createTextView(Context context, TextView model) {

        final TextView textView = new TextView(context);

        textView.setLayoutParams(model.getLayoutParams());
        textView.setTypeface(model.getTypeface());
        textView.setTextSize(model.getTextSize());
        textView.setTextColor(model.getTextColors());
        textView.setBackground(model.getBackground());
        textView.setVisibility(VISIBLE);

        return textView;
    }

    /**
     * Add an entry to the console.
     *
     * @param consoleLine
     */
    public void addLine(String consoleLine) {

        final Context context = getContext();
        if (context == null) {
            return;
        }

        final TextView textView = createTextView(context, mConsoleLineModel);

        (new AsyncTask<String, String, Void>() {

            private boolean firstLetter = true;

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(String... lines) {
                final String line = lines[0];

                final Random random = new Random();
                // Publish char one by one
                for (char c : line.toCharArray()) {
                    try {
                        publishProgress(String.valueOf(c));
                        Thread.sleep(random.nextInt(LETTER_VARIABLE_DELAY) + LETTER_FIXED_DELAY);
                    } catch (InterruptedException ignored) { }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if (firstLetter) {
                    mLines.add(textView);
                    mConsoleContent.addView(textView);
                    if (mLines.size() > MAX_LINES) {
                        removeLine();
                    }
                    firstLetter = false;
                }
                textView.append(String.valueOf(values[0]));
            }
        }).execute(consoleLine);
    }

    /**
     * Removes the first line from the console.
     * Translates all lines.
     */
    private void removeLine() {
        final int lineId = 0;
        final TextView lineToRemove = mLines.get(lineId);

        // Translate all lines
        final int startValue = 0;
        final int endValue = startValue - lineToRemove.getHeight();
        for (TextView line : mLines) {
            ObjectAnimator
                    .ofInt(line, "translationY", startValue, endValue)
                    .setDuration(REMOVE_LINE_DURATION)
                    .start();
        }

        // Fade only the line to remove
        final ObjectAnimator fadeOut = ObjectAnimator
                .ofFloat(lineToRemove, "alpha", 1f, 0f)
                .setDuration(REMOVE_LINE_DURATION);

        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                lineToRemove.setVisibility(GONE);
                mLines.remove(lineId);
                mConsoleContent.removeView(lineToRemove);
            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });

        fadeOut.start();
    }
}
