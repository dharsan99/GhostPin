package opencv.ar.ashwin.opcv;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ashwin on 15/01/16.
 */
public class MarkerTracker {

    private Mat imgGray;
    private Mat templGray;
    MatOfKeyPoint keyPointImg;
    MatOfKeyPoint keyPointTempl;
    Mat descImg;
    Mat descTempl;
    MatOfDMatch matches;
    Mat image;

    public Mat getTemplate() {
        return template;
    }

    public void setTemplate(Mat template) {
        this.template = template;
        templGray = new Mat(template.size(), image.type());
        Imgproc.cvtColor(template, templGray, Imgproc.COLOR_BGRA2GRAY);
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
        imgGray = new Mat(image.size(), image.type());
        Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_BGRA2GRAY);
    }

    Mat template;

    public MarkerTracker(Mat image, Mat template){
        this.image = image;
        this.template = template;
        Log.i("Marker-Tracker", "image is null?::" + (null == image));

        imgGray = new Mat(image.size(), image.type());
        templGray = new Mat(template.size(), image.type());
        //Convert them to grayscale
        Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_BGRA2GRAY);
        //  Core.normalize(imgGray, imgGray, 0, 255, Core.NORM_MINMAX);

        // Mat	grayImage02 = new Mat(image02.rows(), image02.cols(), image02.type());
        Imgproc.cvtColor(template, templGray, Imgproc.COLOR_BGRA2GRAY);
//        Core.normalize(templGray, templGray, 0, 255, Core.NORM_MINMAX);


    }


    public void performMatch(){



        //create feature detectors and feature extractors
        FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        //set the keypoints
        keyPointImg = new MatOfKeyPoint();
        orbDetector.detect(imgGray, keyPointImg);

        keyPointTempl = new MatOfKeyPoint();
        orbDetector.detect(templGray, keyPointTempl);

        //get the descriptions
        descImg = new Mat(image.size(), image.type());
        orbExtractor.compute(imgGray, keyPointImg, descImg);

        descTempl = new Mat(template.size(), template.type());
        orbExtractor.compute(templGray, keyPointTempl, descTempl);

        //perform matching
        matches = new MatOfDMatch();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matcher.match(descImg, descTempl, matches);

        Log.i("perform match result", matches.size().toString());


    }

    public MatOfPoint getContourOfBestMatch(){



        Mat threshold = new Mat(imgGray.size(),imgGray.type());
        Imgproc.threshold(imgGray, threshold, 70, 255, Imgproc.THRESH_TOZERO);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(threshold, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);
        //HashMap<Integer,MatOfPoint> coordinates = computeCoord(contours,)

        if(contours.size()==0 )
            return null;
        List<DMatch> matchList = matches.toList();
        List<KeyPoint> keyPointList = keyPointImg.toList();


    HashMap<Integer, Double> contourDensityMap = new HashMap<Integer,Double>();

        Log.i("getContourBestMatch::", "contour size::" + contours.size());


        for(int idx = 0;idx<contours.size();idx++){
            MatOfPoint2f  ctr2f = new MatOfPoint2f(contours.get(idx).toArray());
            //double contourarea = Imgproc.contourArea(contours.get(idx));
            double contourarea = contours.get(idx).rows();
            Log.i("contour area","contour area is::"+contourarea);
            for(DMatch match:matchList)
            {
                if(Imgproc.pointPolygonTest(ctr2f,keyPointList.get(match.queryIdx).pt,true)>0){
                    if(null ==contourDensityMap.get(idx))
                        contourDensityMap.put(idx,1.0);

                    else{
                        contourDensityMap.put(idx,((Double)contourDensityMap.get(idx))+1);
                    }

                }

            }
            if(contourDensityMap.containsKey(idx)) {
                Log.i("contourPoint","idx::"+idx+"count::"+contourDensityMap.get(idx));
                contourDensityMap.put(idx, contourDensityMap.get(idx) / contourarea);
            }
        }

        Log.i("MarkerTracker","contour density size::"+contourDensityMap.size());

        Map.Entry<Integer, Double> maxEntry = null;

        for (Map.Entry<Integer, Double> entry : contourDensityMap.entrySet())
        {
            Log.i("contourDensityMap","Entry value::"+entry.getValue());
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }

        //return contours;
        return contours.get(maxEntry!=null?maxEntry.getKey():0);
    }

    public Mat drawContourOnIdentifiedImg(MatOfPoint contour){

            if(null == contour)
                return null;

        Scalar color = new Scalar(0,255,0);
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        contourList.add(contour);
        Imgproc.drawContours(image,contourList,-1,color,3);
        return image;
    }

    public Mat drawContourOnIdentifiedImg(List<MatOfPoint> contourList){


        Scalar color = new Scalar(0,255,0);
      //  List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
       // contourList.add(contour);
        Imgproc.drawContours(image,contourList,-1,color,3);
        return image;
    }



}
