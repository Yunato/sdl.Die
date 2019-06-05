package jp.ac.titech.itpro.sdl.die;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private GLSurfaceView glView;
    private SimpleRenderer renderer;

    private Cube cube;
    private Pyramid pyramid;

    private Thread thread;
    private int seed = 0;
    private int dx = 0;
    private int dy = 0;
    private int dz = 0;
    private boolean isLoop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        glView = findViewById(R.id.gl_view);
        SeekBar seekBarX = findViewById(R.id.seekbar_x);
        SeekBar seekBarY = findViewById(R.id.seekbar_y);
        SeekBar seekBarZ = findViewById(R.id.seekbar_z);
        seekBarX.setMax(360);
        seekBarY.setMax(360);
        seekBarZ.setMax(360);
        seekBarX.setOnSeekBarChangeListener(this);
        seekBarY.setOnSeekBarChangeListener(this);
        seekBarZ.setOnSeekBarChangeListener(this);

        while(seed == 0) seed = (int)(Math.random() * 10);
        while(dx == 0) dx = getRandDiff();
        while(dy == 0) dy = getRandDiff();
        while(dz == 0) dz = getRandDiff();

        renderer = new SimpleRenderer();
        cube = new Cube();
        pyramid = new Pyramid();
        renderer.setObj(cube);
        glView.setRenderer(renderer);
    }

    private int getRandDiff(){
        int rand = (int)(Math.random() * seed);
        rand *= Math.random() > 0.5 ? -1 : 1;
        return rand;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(thread != null){
            isLoop = false;
            thread = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLoop = true;
        final Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isLoop) {
                    final SeekBar seekBarX = findViewById(R.id.seekbar_x);
                    final SeekBar seekBarY = findViewById(R.id.seekbar_y);
                    final SeekBar seekBarZ = findViewById(R.id.seekbar_z);
                    final int nx = ((seekBarX.getProgress() + dx) + 360) % 360;
                    final int ny = ((seekBarY.getProgress() + dy) + 360) % 360;
                    final int nz = ((seekBarZ.getProgress() + dz) + 360) % 360;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBarX.setProgress(nx);
                            seekBarY.setProgress(ny);
                            seekBarZ.setProgress(nz);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    }catch(InterruptedException e){

                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        glView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
        case R.id.menu_cube:
            renderer.setObj(cube);
            break;
        case R.id.menu_pyramid:
            renderer.setObj(pyramid);
            break;
        case R.id.menu_reload:
            if(thread != null){
                isLoop = false;
                seed = dx = dy = dz = 0;
                while(seed == 0) seed = (int)(Math.random() * 10);
                while(dx == 0) dx = getRandDiff();
                while(dy == 0) dy = getRandDiff();
                while(dz == 0) dz = getRandDiff();
                isLoop = true;
            }
            break;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
        case R.id.seekbar_x:
            renderer.rotateObjX(progress);
            break;
        case R.id.seekbar_y:
            renderer.rotateObjY(progress);
            break;
        case R.id.seekbar_z:
            renderer.rotateObjZ(progress);
            break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
