package opencv.ar.ashwin.opcv;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.widget.ImageView;

import org.opencv.android.FpsMeter;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    static{ System.loadLibrary("opencv_java3");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgView = (ImageView) findViewById(R.id.imageView);
        identifyObjOnImage(R.drawable.stop_img4,R.drawable.stop_template,imgView);



    }
    private void identifyObjOnImage(int imageResourceId, int templateResourceId, ImageView imgView){
        Mat img;
        Mat template;
        try {

            img = Utils.loadResource(MainActivity.this,imageResourceId);
            template = Utils.loadResource(MainActivity.this,templateResourceId);
            MarkerTracker mrk = new MarkerTracker(img, template);

            mrk.performMatch();
            //MatOfPoint contour = mrk.getContourOfBestMatch();
            MatOfPoint contour = mrk.getContourOfBestMatch();
            Mat result = mrk.drawContourOnIdentifiedImg(contour);
            Bitmap bm = Bitmap.createBitmap(result.cols(), result.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, bm);
            imgView.setImageBitmap(bm);


        }catch (IOException e){
            Log.e("EXCEPTION","caught exception in identifyObjOnImage",e);
        }
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
}
