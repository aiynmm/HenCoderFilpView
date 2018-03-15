package com.sinosoft.hencodercustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Mars on 2018/3/15.
 */

public class FlipMapView extends View {

    //Y轴方向旋转角度
    private float degreeY;
    //不变的那一半，Y轴方向旋转角度
    private float fixDegreeY;
    //Z轴方向（平面内）旋转的角度
    private float degreeZ;

    private Paint paint;
    private Bitmap bitmap;
    private Camera camera;

    public FlipMapView(Context context) {
        this(context, null);
    }

    public FlipMapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //obtainStyledAttributes()方法第一个参数为attr.xml中declare-styleable标签的name
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlipMapView);
        //getXXX()方法中参数为   declare-styleable的name_attr的属性name
        BitmapDrawable bitmapDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.FlipMapView_fv_background);
        //记得使用完，要调用recycle()方法释放资源
        typedArray.recycle();

        if (bitmapDrawable == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps);
        } else {
            bitmap = bitmapDrawable.getBitmap();
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        camera = new Camera();

        //不明白这一句是干嘛的？
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = -displayMetrics.density * 6;
        camera.setLocation(0, 0, newZ);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;


        //画变换的一半
        //先旋转，再裁切，再使用camera执行3D动效,**然后保存camera效果**,最后再旋转回来
        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY);
        //rotate为canvas绕某一个点旋转
        canvas.rotate(-degreeZ);
        camera.rotateY(degreeY);
        camera.applyToCanvas(canvas);
        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
        //当degreeZ为0时，裁剪的是右上角的1/4的位置
        canvas.clipRect(0, -centerY, centerX, centerY);
        canvas.rotate(degreeZ);
        canvas.translate(-centerX, -centerY);
        camera.restore();
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        //画不变换的另一半
        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(-degreeZ);
        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
        //当degreeZ为0时，裁剪的是左上角的1/4的位置
        canvas.clipRect(-centerX, -centerY, 0, centerY);
        //此时的canvas的坐标系已经旋转，所以这里是rotateY
        camera.rotateY(fixDegreeY);
        camera.applyToCanvas(canvas);
        canvas.rotate(degreeZ);
        canvas.translate(-centerX, -centerY);
        camera.restore();
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();
    }


    @Keep
    public void setFixDegreeY(float fixDegreeY) {
        this.fixDegreeY = fixDegreeY;
        invalidate();
    }

    @Keep
    public void setDegreeY(float degreeY) {
        this.degreeY = degreeY;
        invalidate();
    }

    @Keep
    public void setDegreeZ(float degreeZ) {
        this.degreeZ = degreeZ;
        invalidate();
    }

    public void reset() {
        degreeY = 0;
        fixDegreeY = 0;
        degreeZ = 0;
    }
}
