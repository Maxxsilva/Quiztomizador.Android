package com.alpha.quiztomizador.grafico;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Usuario on 25/09/2015.
 */
public class GraficoPizza extends View {


        private ArrayList<Pizza> slices = new ArrayList<Pizza>();
        private Paint paint = new Paint();
        private Path path = new Path();

        private int indexSelected = -1;
        private int thickness = 50;
        private OnSliceClickedListener listener;


        public GraficoPizza(Context context) {
            super(context);
        }
        public GraficoPizza(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawColor(Color.TRANSPARENT);
            paint.reset();
            paint.setAntiAlias(true);
            float midX, midY, radius, innerRadius;
            path.reset();

            float currentAngle = 270;
            float currentSweep = 0;
            int totalValue = 0;
            float padding = 2;

            midX = getWidth()/2;
            midY = getHeight()/2;
            if (midX < midY){
                radius = midX;
            } else {
                radius = midY;
            }
            radius -= padding;
            innerRadius = radius - thickness;

            for (Pizza slice : slices){
                totalValue += slice.getValue();
            }

            int count = 0;
            for (Pizza slice : slices){
                Path p = new Path();
                paint.setColor(slice.getColor());
                currentSweep = (slice.getValue()/totalValue)*(360);
                p.arcTo(new RectF(midX-radius, midY-radius, midX+radius, midY+radius), currentAngle+padding, currentSweep - padding);
                p.arcTo(new RectF(midX-innerRadius, midY-innerRadius, midX+innerRadius, midY+innerRadius), (currentAngle+padding) + (currentSweep - padding), -(currentSweep-padding));
                p.close();

                slice.setPath(p);
                slice.setRegion(new Region((int)(midX-radius), (int)(midY-radius), (int)(midX+radius), (int)(midY+radius)));
                canvas.drawPath(p, paint);

                if (indexSelected == count && listener != null){
                    path.reset();
                    paint.setColor(slice.getColor());
                    paint.setColor(Color.parseColor("#33B5E5"));
                    paint.setAlpha(100);

                    if (slices.size() > 1) {
                        path.arcTo(new RectF(midX-radius-(padding*2), midY-radius-(padding*2), midX+radius+(padding*2), midY+radius+(padding*2)), currentAngle, currentSweep+padding);
                        path.arcTo(new RectF(midX-innerRadius+(padding*2), midY-innerRadius+(padding*2), midX+innerRadius-(padding*2), midY+innerRadius-(padding*2)), currentAngle + currentSweep + padding, -(currentSweep + padding));
                        path.close();
                    } else {
                        path.addCircle(midX, midY, radius+padding, Direction.CW);
                    }

                    canvas.drawPath(path, paint);
                    paint.setAlpha(255);
                }

                currentAngle = currentAngle+currentSweep;

                count++;
            }


        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {

            Point point = new Point();
            point.x = (int) event.getX();
            point.y = (int) event.getY();

            int count = 0;
            for (Pizza slice : slices){
                Region r = new Region();
                r.setPath(slice.getPath(), slice.getRegion());
                if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN){
                    indexSelected = count;
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    if (r.contains(point.x, point.y) && listener != null){
                        if (indexSelected > -1){
                            listener.onClick(indexSelected);
                        }
                        indexSelected = -1;
                    }

                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL)
                    indexSelected = -1;
                count++;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                postInvalidate();
            }

           return true;
        }


        public ArrayList<Pizza> getSlices() {
            return slices;
        }
        public void setSlices(ArrayList<Pizza> slices) {
            this.slices = slices;
            postInvalidate();
        }
        public Pizza getSlice(int index) {
            return slices.get(index);
        }
        public void addSlice(Pizza slice) {
            this.slices.add(slice);
            postInvalidate();
        }
        public void setOnSliceClickedListener(OnSliceClickedListener listener) {
            this.listener = listener;
        }

        public int getThickness() {
            return thickness;
        }
        public void setThickness(int thickness) {
            this.thickness = thickness;
            postInvalidate();
        }

        public void removeSlices(){
            for (int i = slices.size()-1; i >= 0; i--){
                slices.remove(i);
            }
            postInvalidate();
        }

        public interface OnSliceClickedListener {
            void onClick(int index);
        }



}

