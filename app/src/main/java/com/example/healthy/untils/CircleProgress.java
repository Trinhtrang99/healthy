package com.example.healthy.untils;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.healthy.BuildConfig;
import com.example.healthy.R;

public class CircleProgress extends View {

    private static final String TAG = CircleProgress.class.getSimpleName();

    private Context mContext;
    //Default size
    private int mDefaultSize;
    //Whether to enable anti-aliasing
    private boolean antiAlias;
    //Draw tips
    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    //Draw units
    private TextPaint mUnitPaint;
    private CharSequence mUnit;
    private int mUnitColor;
    private float mUnitSize;
    private float mUnitOffset;

    //Draw numeric values
    private TextPaint mValuePaint;
    private float mValue;
    private float mMaxValue;
    private float mValueOffset;
    private int mPrecision;
    private String mPrecisionFormat;
    private int mValueColor;
    private float mValueSize;

    //Draw arc
    private Paint mArcPaint;
    private float mArcWidth;
    private float mStartAngle, mSweepAngle;
    private RectF mRectF;
    //Current progress, [0.0f,1.0f]
    private float mPercent;
    //Animation time
    private long mAnimTime;
    //Attribute animation
    private ValueAnimator mAnimator;

    //Draw background arc
    private Paint mBgArcPaint;
    private int mBgArcColor;
    private int mArcColor;
    private float mBgArcWidth;

    //Center coordinates, radius
    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;

    private int mArcCenterX;
    // The outer radius of the inner dashed line
    private float mExternalDottedLineRadius;
    // inner radius of inner dotted line
    private float mInsideDottedLineRadius;

    // Number of lines
    private int mDottedLineCount = 100;
    // distance between arc and dotted line
    private int mLineDistance = 20;
    // line width
    private float mDottedLineWidth = 40;
    //Whether to use gradient
    protected boolean useGradient=true;
    //The starting color of the foreground color
    private int foreStartColor;
    //The end color of the foreground color
    private int foreEndColcor;
    protected int mWidth;
    protected int mHeight;

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize = dipToPx(mContext, 150);
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();
        initAttrs(attrs);
        initPaint();
        setValue(mValue);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

        antiAlias = typedArray.getBoolean(R.styleable.CircleProgressBar_antiAlias, true);

        mHint = typedArray.getString(R.styleable.CircleProgressBar_hint);
        mHintColor = typedArray.getColor(R.styleable.CircleProgressBar_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.CircleProgressBar_hintSize, 15);

        mValue = typedArray.getFloat(R.styleable.CircleProgressBar_value, 50);
        mMaxValue = typedArray.getFloat(R.styleable.CircleProgressBar_maxValue, 50);
        //Content numerical precision format
        mPrecision = typedArray.getInt(R.styleable.CircleProgressBar_precision, 0);
        mPrecisionFormat = getPrecisionFormat(mPrecision);
        mValueColor = typedArray.getColor(R.styleable.CircleProgressBar_valueColor, Color.BLACK);
        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, 15);

        mUnit = typedArray.getString(R.styleable.CircleProgressBar_unit);
        mUnitColor = typedArray.getColor(R.styleable.CircleProgressBar_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.CircleProgressBar_unitSize, 30);

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, 15);
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressBar_startAngle, 270);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweepAngle, 360);

        mBgArcColor = typedArray.getColor(R.styleable.CircleProgressBar_bgArcColor, Color.WHITE);
        mArcColor = typedArray.getColor(R.styleable.CircleProgressBar_arcColors, Color.RED);
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, 15);
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.CircleProgressBar_textOffsetPercentInRadius, 0.33f);
        mAnimTime = typedArray.getInt(R.styleable.CircleProgressBar_animTime, 1000);
        mDottedLineCount = typedArray.getInteger(R.styleable.CircleProgressBar_dottedLineCount, mDottedLineCount);
        mLineDistance = typedArray.getInteger(R.styleable.CircleProgressBar_lineDistance, mLineDistance);
        mDottedLineWidth = typedArray.getDimension(R.styleable.CircleProgressBar_dottedLineWidth, mDottedLineWidth);
        foreStartColor = typedArray.getColor(R.styleable.CircleProgressBar_foreStartColor, Color.BLUE);
        foreEndColcor = typedArray.getColor(R.styleable.CircleProgressBar_foreEndColor, Color.BLUE);
        typedArray.recycle();
    }

    private void initPaint() {
        mHintPaint = new TextPaint();
        // Setting anti-aliasing will consume more resources and the drawing speed will be slower.
        mHintPaint.setAntiAlias(antiAlias);
        // Set the drawing text size
        mHintPaint.setTextSize(mHintSize);
        // Set the brush color
        mHintPaint.setColor(mHintColor);
        // Draw from the middle to both sides, no need to calculate the text again
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(mValueColor);
        // Set the Typeface object, that is, the font style, including bold, italic, serif, non-serif, etc.
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        // Set the style of the brush to FILL, FILL_OR_STROKE, or STROKE
        mArcPaint.setStyle(Paint.Style.STROKE);
        // Set the brush thickness
        mArcPaint.setStrokeWidth(mArcWidth);
        // When the brush style is STROKE or FILL_OR_STROKE, set the graphic style of the brush, such as the round style
        // Cap.ROUND (round style) or Cap.SQUARE (square style)
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setColor(mBgArcColor);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mBgArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, mDefaultSize),
                measureView(heightMeasureSpec, mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcCenterX = (int) (w / 2.f);
        Log.d(TAG, "onSizeChanged: w = " + w + "; h = " + h + "; oldw = " + oldw + "; oldh = " + oldh);
        // Find the maximum width of the arc and the background arc
        float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);
        //Find the minimum value as the actual value
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) maxArcWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int) maxArcWidth);
        //Subtract the width of the arc, otherwise it will cause part of the arc to be drawn on the periphery
        mRadius = minSize / 2;
        //Get the relevant parameters of the circle
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;
        //Draw the border of the arc
        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;
        //Calculate the baseline when the text is drawn
        //Because the attributes such as the baseline, descent, and ascent of the text are only related to the textSize and typeface, they can be directly calculated at this time
        //If the value, hint, and unit are drawn by the same brush or if the size of the text needs to be set dynamically, it needs to be calculated again after each update
        mValueOffset = mCenterPoint.y + getBaselineOffsetFromY(mValuePaint);
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mHintPaint);
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);

        if (useGradient) {
            LinearGradient gradient = new LinearGradient(0, 0, w, h, foreEndColcor, foreStartColor, Shader.TileMode.CLAMP);
            mArcPaint.setShader(gradient);
        } else {
            mArcPaint.setColor(mArcColor);
        }

        Log.d(TAG, "onSizeChanged: control size = " + "(" + w + ", " + h + ")"
                + "Circle center coordinate = " + mCenterPoint.toString()
                + ";Circle radius = " + mRadius
                + ";Circumscribed circumscribed rectangle = " + mRectF.toString());

        // The outer radius of the dotted line
        mExternalDottedLineRadius = (int) (mRectF.width() / 2)+mLineDistance;
        // The inner radius of the dotted line
        mInsideDottedLineRadius = mExternalDottedLineRadius - mDottedLineWidth;
    }

    private float getBaselineOffsetFromY(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return ((Math.abs(fontMetrics.ascent) - fontMetrics.descent))/ 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArc(canvas);
    }

    /**
     * Draw content text
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset, mValuePaint);

        if (mHint != null) {
            canvas.drawText(mHint.toString(), mCenterPoint.x, mHintOffset, mHintPaint);
        }

        if (mUnit != null) {
            canvas.drawText(mUnit.toString(), mCenterPoint.x, mUnitOffset, mUnitPaint);
        }
    }

    private void drawArc(Canvas canvas) {
        // draw background arc
        // Redraw from the end of the progress arc to optimize performance
        canvas.save();

        // 360 * Math.PI / 180
        float evenryDegrees = (float) (2.0f * Math.PI / mDottedLineCount);
        float startDegress = (float) (135 * Math.PI / 180);
        float endDegress = (float) (225 * Math.PI / 180);
        for (int i = 0; i < mDottedLineCount; i++) {
            float degrees = i * evenryDegrees;
            // Filter the arc length at the bottom 90 degrees
            if (degrees > startDegress && degrees < endDegress) {
                continue;
            }
            float startX = mArcCenterX + (float) Math.sin(degrees) * mInsideDottedLineRadius;
            float startY = mArcCenterX - (float) Math.cos(degrees) * mInsideDottedLineRadius;

            float stopX = mArcCenterX + (float) Math.sin(degrees) * mExternalDottedLineRadius;
            float stopY = mArcCenterX - (float) Math.cos(degrees) * mExternalDottedLineRadius;

            canvas.drawLine(startX, startY, stopX, stopY, mBgArcPaint);
        }

        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);

        // The first parameter oval is RectF type, which is the arc display area
        // startAngle and sweepAngle are both of type float, representing the starting angle of the arc and the degree of arc
        // 3 o'clock direction is 0 degrees, increasing clockwise
        // If startAngle <0 or> 360, it is equivalent to startAngle% 360
        // useCenter: If True, include the center of the circle when drawing the arc, usually used to draw a fan shape
        float currentAngle = mSweepAngle * mPercent;
        canvas.drawArc(mRectF, 2, currentAngle, false, mArcPaint);
        canvas.restore();
    }

    public boolean isAntiAlias() {
        return antiAlias;
    }

    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        mHint = hint;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    /**
     * Set current value
     *
     * @param value
     */
    public void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent
                            + ";currentAngle = " + (mSweepAngle * mPercent)
                            + ";value = " + mValue);
                }
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * Get the maximum value
     *
     * @return
     */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * Set the maximum value
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }

    /**
     * Get accuracy
     *
     * @return
     */
    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int precision) {
        mPrecision = precision;
        mPrecisionFormat = getPrecisionFormat(precision);
    }

    public long getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        mAnimTime = animTime;
    }

    /**
     * Reset
     */
    public void reset() {
        startAnimator(mPercent, 0.0f, 1000L);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //Release resources
    }

    /**
     * Measurement View
     *
     * @param measureSpec
     * @param  defaultSize View default size
     * @return
     */
    private static int measureView(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    /**
     * dip to px
     *
     * @param dip
     * @return
     */
    public static int dipToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * Get numeric precision format string
     *
     * @param precision
     * @return
     */
    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }
}