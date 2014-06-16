package org.es.common.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.es.uremote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cyril Leroux
 *         Created 16/06/2014.
 */
public class Console extends LinearLayout {

    private final static int MAX_LINE = 4;
    private LinearLayout mConsoleContent;
    private List<TextView> mLines = new ArrayList<>(MAX_LINE);

    private Typeface mTypeface;

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
        mConsoleContent = ((LinearLayout) findViewById(R.id.consoleContent));
        mTypeface = ((TextView) findViewById(R.id.tvConsoleTitle)).getTypeface();

        addLine("line 1 : hello, I'm line one !");
        addLine("line 2 : hello, I'm line two !");
        addLine("line 3 : hello, I'm line three !");
        addLine("line 4 : hello, I'm line four !");
        addLine("line 5 : hello, I'm line five !");
        addLine("line 6 : hello, I'm line Six. Goodbye line one !");
    }

    public void addLine(String consoleLine) {

        if (mLines.size() >= MAX_LINE) {
            removeLine();
        }

        final TextView tv = new TextView(getContext());
        tv.setTypeface(mTypeface);
        mLines.add(tv);
        mConsoleContent.addView(tv);

        (new AsyncTask<String, String, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                for (char c : strings[0].toCharArray()) {

                    try {
                        Thread.sleep(75);
                        publishProgress(String.valueOf(c));
                    } catch (InterruptedException ignored) { }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                tv.append(String.valueOf(values[0]));
            }
        }).execute(consoleLine);
    }

    private void removeLine() {

        final TextView tv = mLines.get(0);
        final int height = tv.getHeight();

        for (TextView line : mLines) {
            ObjectAnimator.ofInt(line, "translationY", 0, -height).setDuration(150).start();
        }

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(tv, "alpha", 1f, 0f).setDuration(150);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                mConsoleContent.removeView(tv);
            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        fadeOut.start();

    }


}
