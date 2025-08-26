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


public class circleVisualizer extends Activity {

    private MediaPlayer mediaPlayer;
    private MyView myView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ask runtime RECORD_AUDIO permission
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO}, 123);
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.abc);
        mediaPlayer.setLooping(true);

        myView = new MyView(this, mediaPlayer);
        setContentView(myView);

        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (myView != null) {
            myView.release();
        }
    }

    public static class MyView extends View {
        private int bt;
        private Paint pnt;
        private byte[] bytes;
        private Visualizer visualizer;
        private float angle;
        private float length;
        double bassMagnitude = 0;
        public MyView(Context ctx, MediaPlayer player) {
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
            float cy = getHeight() / 2f;
            float x1 = 0,y1 = 0,x2 = 0,y2 = 0;
            float radius = 200;

            // base circle
            // canvas.drawCircle(cx, cy, radius, pnt);

            if (bytes == null) return; // nothing yet
            angle = 0;
            for (int i = 0; i < 90 ; i++, angle += 4) {
                int t =Math.abs(bytes[i]) / 2;
                
                


                Log.d(String.valueOf(
                        t),"value");
                x1 = (float) (cx + radius * Math.cos(Math.toRadians(angle)));
                y1 = (float) (cy + radius * Math.sin(Math.toRadians(angle)));
                x2 = (float) (cx + (radius + t) * Math.cos(Math.toRadians(angle)));
                y2 = (float) (cy + (radius +t) * Math.sin(Math.toRadians(angle)));


                invalidate();


                canvas.drawLine(x1,y1,x2,y2,new Paint(){{
                    setColor(Color.GRAY);
                    setStrokeWidth(5);
                    setStrokeCap(Paint.Cap.ROUND);
                    setStyle(Paint.Style.FILL);

                }});

                canvas.drawCircle(x2,y2,6,new Paint(Paint.ANTI_ALIAS_FLAG){{
                    setColor(Color.BLACK);
                    setStyle(Paint.Style.FILL);
                }});







            }
        }
    }
}
