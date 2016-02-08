package opencv.ar.ashwin.ghostpin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase.*;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import opencv.ar.ashwin.ghostpin.R;


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
    private Button startButton;
    private Button clearButton;
    private boolean runMatching;
    private Template prevBestMatch;

    private TextView templateText;

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

        runMatching = false;

        startButton = (Button)findViewById(R.id.startButton);
        startButton.setVisibility(Button.VISIBLE);
       // captureButton.setOnClickListener(this);

        clearButton = (Button)findViewById(R.id.clear_data);
        clearButton.setVisibility(Button.VISIBLE);

        templateText = (TextView) findViewById(R.id.templateText);

        getTemplates();

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

    public void clearData(View v){
        db.deleteData();
        Log.i("clearData","data deleted");

    }

    private void getTemplates(){
        map = db.getTemplates();
 }

    public  void updateText(final TextView editText,final String text)
    {

        this.runOnUiThread(new Runnable() {
            public void run() {
                if (null == text || text == "")
                    editText.setVisibility(View.INVISIBLE);
                else
                    editText.setVisibility(View.VISIBLE);
                editText.setText(text);


            }

        });
    }

    public  void setVisibility(final TextView textView, final int newVisibility)
    {


        this.runOnUiThread(new Runnable() {
            public void run() {
                if(newVisibility!= View.INVISIBLE || newVisibility!=View.VISIBLE || newVisibility!=View.GONE)
                    textView.setVisibility(View.INVISIBLE);

                int visibility = textView.getVisibility();
                if (visibility==newVisibility)
                    return;

                    textView.setVisibility(newVisibility);

            }

        });
    }


    private void identifyObjOnImage(int imageResourceId, int templateResourceId, ImageView imgView){
        Mat img;
        Mat template;
        try {

            img = Utils.loadResource(MarkerFinderActivity.this, imageResourceId);
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
            Bitmap bm = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, bm);
            imgView.setImageBitmap(bm);


        }catch (IOException e){
            Log.e("EXCEPTION","caught exception in identifyObjOnImage",e);
        }
    }

    private Template identifyObjOnFrame(CvCameraViewFrame frame){
        Mat img;
        Mat result = null;
        Template bestMatch;

            img = frame.rgba();
              if(null == mrk)
                  mrk = new MarkerTracker(img);
              else{
                  mrk.setImage(img);
              }
              if(null!=map && map.size()!=0)
                bestMatch = mrk.performMatches(map);
              else {
                  return null;

              }

//

        return bestMatch;
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
        startButton.setVisibility(Button.GONE);
        clearButton.setVisibility(Button.GONE);
        templateText.setVisibility(View.INVISIBLE);

    }


    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        captureButton.setVisibility(Button.GONE);
        startButton.setVisibility(Button.GONE);
        clearButton.setVisibility(Button.GONE);
        templateText.setVisibility(View.INVISIBLE);

    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public void toggleRunMatching(View v){
        if(runMatching){
            runMatching = false;
        startButton.setText("START");
            templateText.setVisibility(View.INVISIBLE);
        }
        else{
            runMatching=true;
        startButton.setText("STOP");
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("MarkerFinder","onClick event");

        AlertDialog.Builder tagMsgBox = new AlertDialog.Builder(this);

        final EditText input = new EditText(MarkerFinderActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        tagMsgBox.setView(input);

        tagMsgBox.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg = input.getText().toString();
                        if (!msg.equals("")) {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                            String currentDateandTime = sdf.format(new Date());
                            String fileName = Environment.getExternalStorageDirectory().getPath() +
                                    "/sample_picture_" + currentDateandTime + ".jpg";

                            mOpenCvCameraView.takePicture(fileName);
                            storeData(fileName, msg);

                            Toast.makeText(MarkerFinderActivity.this, fileName + " saved", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(MarkerFinderActivity.this, "Please do tag", Toast.LENGTH_SHORT).show();
                        }




                    }
                });

        tagMsgBox.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        tagMsgBox.show();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat result;
        if(runMatching) {

            double random = Math.random();
            Log.i("frame","Run matching and random::"+random);

            if (null != prevBestMatch && random<0.8) {
                mrk.performMatch(prevBestMatch);
                updateText(templateText, prevBestMatch.getDesc());
                Log.i("match","prevMatch desc"+prevBestMatch.getDesc());

                MatOfPoint contour = mrk.getContourOfBestMatch();
                result = mrk.drawContourOnIdentifiedImg(contour);
                return result;



            } else {
                Log.i("frame","best Match find starts");
                Template bestMatch = identifyObjOnFrame(inputFrame);
                Log.i("match","best match ends::"+bestMatch);
                if (bestMatch != null) {


                    updateText(templateText, bestMatch.getDesc());
                    MatOfPoint contour = mrk.getContourOfBestMatch();

                    result = mrk.drawContourOnIdentifiedImg(contour);
                    prevBestMatch = bestMatch;
                    return result;
                } else {
                    setVisibility(templateText, View.INVISIBLE);
                    prevBestMatch = null;

                }
            }
        }

        return inputFrame.rgba();
    }

    private void storeData(String fileName,String msg){
        if(null == map){
            map = new HashMap<String,Template>();

        }
        Template t = new Template(fileName,msg);
        map.put(fileName, t);
        db.addImgData(t);
        //@TODO: need to add logic for differential inserts into database

    }

}
