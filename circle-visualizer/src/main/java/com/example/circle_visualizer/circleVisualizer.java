package com.example.circle_visualizer;


import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.os.Debug;
import android.util.Log;
import android.graphics.Path;
import android.util.AttributeSet;

public  class circleVisualizer extends View {
        private int bt;
        private Paint pnt;
        private byte[] bytes;
        private Visualizer visualizer;
        private float angle;
        private float length;
        double bassMagnitude = 0;
        public circleVisualizer(Context ctx, MediaPlayer player) {
            super(ctx);


            // attach visualizer to MediaPlayer
            setPlayer(player.getAudioSessionId());
            Toast.makeText(ctx, String.valueOf(length), Toast.LENGTH_SHORT).show();

            // optional "breathing" radius animation
            ValueAnimator vl = ValueAnimator.ofFloat(0, 40);
            vl.setDuration(1000);
            vl.setRepeatMode(ValueAnimator.REVERSE);
            vl.setRepeatCount(ValueAnimator.INFINITE);
            // vl.addUpdateListener(anim -> invalidate());
            //vl.start();
        }
    public circleVisualizer(Context ctx, AttributeSet attr) {
        
        super(ctx,attr);
    }
    public circleVisualizer(Context ctx, AttributeSet attr, int def) {
        super(ctx, attr,def);
        }
    
        private void setPlayer(int audioSessionId) {
            visualizer = new Visualizer(audioSessionId);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {




                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] data,
                                                  int samplingRate) {
                    bytes = data;

                    invalidate();
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft,
                                             int samplingRate) {


                    // Analyze first 10 bins (low frequency range)
                    for (int i = 2; i < 100; i+=2) {
                        float re = fft[2 * i];
                        float im = fft[2 * i + 1];
                        bassMagnitude += Math.hypot(re, im);
                    }




                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);
            visualizer.setEnabled(true);
        }

        public void release() {
            if (visualizer != null) {
                visualizer.release();
                visualizer = null;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float cx = getWidth() / 2f;
            float cy = 65f;
            float x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            float radius = 40;

            // base circle
            // canvas.drawCircle(cx, cy, radius, pnt);

            if (bytes == null) return; // nothing yet
            angle = 0;
            for (int i = 0; i < 40; i++, angle += 9) {
                int t = Math.abs(bytes[i]) / 5;
                Log.d(String.valueOf(t),"value");

                // Add +20 only for indices 0–20
                if (i == 38 || i == 39 || i == 21 || i == 22) {
                    if (t>20) {
                        t += 50;
                    }

                }

                Log.d("t_value", String.valueOf(t));

                x1 = (float) (cx + radius * Math.cos(Math.toRadians(angle)));
                y1 = (float) (cy + radius * Math.sin(Math.toRadians(angle)));
                x2 = (float) (cx + (radius + t) * Math.cos(Math.toRadians(angle)));
                y2 = (float) (cy + (radius + t) * Math.sin(Math.toRadians(angle)));

                invalidate();






            canvas.drawLine(x1,y1,x2,y2,new Paint(){{
                    setColor(Color.BLACK);
                    setStrokeWidth(2);
                    setStrokeCap(Paint.Cap.ROUND);
                    setStyle(Paint.Style.FILL);


                }});

                canvas.drawCircle(x2,y2,2,new Paint(Paint.ANTI_ALIAS_FLAG){{
                    setColor(Color.WHITE);
                    setStyle(Paint.Style.FILL);
                }});







            }
        }
    }
