package opencv.ar.ashwin.opcv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase.*;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class MarkerFinderActivity extends Activity implements CvCameraViewListener2,View.OnClickListener {
    static{ System.loadLibrary("opencv_java3");}
    private CameraView mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    private ModelRenderer modelRenderer;
    DbHelper db;
    private MarkerTracker mrk;
    private Map<String, Template> map;
    private Button captureButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        db = new DbHelper(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mOpenCvCameraView = (CameraView) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();
      //  mOpenCvCameraView.setOnTouchListener(this);

        captureButton = (Button)findViewById(R.id.captureButton);
        captureButton.setVisibility(Button.VISIBLE);
        captureButton.setOnClickListener(this);
        //final RajawaliSurfaceView rajawaliSurface = new RajawaliSurfaceView(this);
        //rajawaliSurface.setFrameRate(60.0);
       // rajawaliSurface.setTransparent(true);
        //rajawaliSurface.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        //addContentView(rajawaliSurface, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));

        //modelRenderer = new ModelRenderer(this);
        //rajawaliSurface.setSurfaceRenderer(modelRenderer);
       //  ImageView imgView = (ImageView) findViewById(R.templName.camera_view);
      //  identifyObjOnImage(R.drawable.stop_img4,R.drawable.stop_template,imgView);



    }

    private void getTemplates(){
        map = db.getTemplates();
 }


    private void identifyObjOnImage(int imageResourceId, int templateResourceId, ImageView imgView){
        Mat img;
        Mat template;
        try {

            img = Utils.loadResource(MarkerFinderActivity.this,imageResourceId);
            template = Utils.loadResource(MarkerFinderActivity.this,templateResourceId);
            if(null == mrk)
            mrk = new MarkerTracker(img, template);
            else{
                mrk.setImage(img);
                mrk.setTemplate(template);
            }

            mrk.performMatch();
            MatOfPoint contour = mrk.getContourOfBestMatch();
            Mat result = mrk.drawContourOnIdentifiedImg(contour);
            Bitmap bm = Bitmap.createBitmap(result.cols(), result.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, bm);
            imgView.setImageBitmap(bm);


        }catch (IOException e){
            Log.e("EXCEPTION","caught exception in identifyObjOnImage",e);
        }
    }

    private Mat identifyObjOnFrame(CvCameraViewFrame frame, int templateResourceId){
        Mat img;
        Mat template;
        Mat result = null;
  //      Log.i("frame is",""+frame);
          try {


            img = frame.rgba();
            //Utils.loadResource(MarkerFinderActivity.this,imageResourceId);
            template = Utils.loadResource(MarkerFinderActivity.this,templateResourceId);
              if(null == mrk)
                  mrk = new MarkerTracker(img, template);
              else{
                  mrk.setImage(img);
                  mrk.setTemplate(template);
              }
            mrk.performMatch();
            MatOfPoint contour = mrk.getContourOfBestMatch();
            result = mrk.drawContourOnIdentifiedImg(contour);

//
        }catch (IOException e){
            Log.e("EXCEPTION","caught exception in identifyObjOnImage",e);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        captureButton.setVisibility(Button.GONE);
    }


    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        captureButton.setVisibility(Button.GONE);
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    @Override
    public void onClick(View v) {
        Log.i("MarkerFinder","onClick event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/sample_picture_" + currentDateandTime + ".jpg";

        mOpenCvCameraView.takePicture(fileName);

      //  mOpenCvCameraView.
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        //return false;
    }
    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat result = identifyObjOnFrame(inputFrame,R.drawable.stop_template);
        if(result !=null)
            return result;

        return inputFrame.rgba();
    }

}
