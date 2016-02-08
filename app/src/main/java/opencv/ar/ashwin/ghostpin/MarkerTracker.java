package opencv.ar.ashwin.ghostpin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ashwin on 15/01/16.
 */
public class MarkerTracker {

    private Mat imgGray;
    private Mat templGray;
    MatOfKeyPoint keyPointImg;
    Mat descImg;
    MatOfDMatch matches;
    Mat image;

    public Mat getTemplate() {
        return template;
    }

    public void setTemplate(Mat template) {
        this.template = template;
        templGray = new Mat(template.size(), template.type());
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
        templGray = new Mat(template.size(), template.type());
        //Convert them to grayscale
        Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_BGRA2GRAY);
        //  Core.normalize(imgGray, imgGray, 0, 255, Core.NORM_MINMAX);

        // Mat	grayImage02 = new Mat(image02.rows(), image02.cols(), image02.type());
        Imgproc.cvtColor(template, templGray, Imgproc.COLOR_BGRA2GRAY);
//        Core.normalize(templGray, templGray, 0, 255, Core.NORM_MINMAX);


    }
    public MarkerTracker(Mat image){
        this.image = image;

        imgGray = new Mat(image.size(), image.type());
        //Convert them to grayscale
        Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_BGRA2GRAY);
        //  Core.normalize(imgGray, imgGray, 0, 255, Core.NORM_MINMAX);


    }


    public void performMatch(){



        //create feature detectors and feature extractors
        FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);


        //set the keypoints
        keyPointImg = new MatOfKeyPoint();
        orbDetector.detect(imgGray, keyPointImg);

        MatOfKeyPoint keyPointTempl = new MatOfKeyPoint();
        orbDetector.detect(templGray, keyPointTempl);

        //get the descriptions
        descImg = new Mat(image.size(), image.type());
        orbExtractor.compute(imgGray, keyPointImg, descImg);

        Mat descTempl = new Mat(template.size(), template.type());
        orbExtractor.compute(templGray, keyPointTempl, descTempl);

        //perform matching
        matches = new MatOfDMatch();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matcher.match(descImg, descTempl, matches);

        Log.i("perform match result", matches.size().toString());


    }

    public void performMatch(Template template){



        //create feature detectors and feature extractors
        FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);


        //set the keypoints
        keyPointImg = new MatOfKeyPoint();
        orbDetector.detect(imgGray, keyPointImg);
        //get the descriptions
        descImg = new Mat(image.size(), image.type());
        orbExtractor.compute(imgGray, keyPointImg, descImg);


        MatOfKeyPoint keyPointTempl = template.getKeyPointTempl();




        Mat descTempl = template.getDescTempl();

        //perform matching
        matches = new MatOfDMatch();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matcher.match(descImg, descTempl, matches);


    }



    public Template performMatches(Map<String,Template> templates){



        //create feature detectors and feature extractors
        FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);


        MatOfKeyPoint keyPointImgT;
        Mat descImgT;
        //set the keypoints
        keyPointImgT = new MatOfKeyPoint();
        orbDetector.detect(imgGray, keyPointImgT);

        descImgT = new Mat(image.size(), image.type());
        orbExtractor.compute(imgGray, keyPointImgT, descImgT);

        Template best = null;
        matches = null;
        Map.Entry<String, Template> maxEntry = null;
      //  MatOfDMatch matches = new MatOfDMatch();

        for (Map.Entry<String, Template> entry : templates.entrySet()) {

            MatOfKeyPoint keyPointTempl = null;
            Mat descTempl = null;
            Mat tGray = null;


            Template t = entry.getValue();
                if(null == t.getTemplGray() || null == t.getDescTempl() || null == t.getKeyPointTempl()){
                    //read image from stored data
                    Mat templ = readImgFromFile(t.getTemplName());

                    tGray = new Mat(templ.size(), templ.type());
                    Imgproc.cvtColor(templ, tGray, Imgproc.COLOR_BGRA2GRAY);

                    keyPointTempl = new MatOfKeyPoint();
                    orbDetector.detect(tGray, keyPointTempl);

                    descTempl = new Mat(templ.size(), templ.type());
                    orbExtractor.compute(tGray, keyPointTempl, descTempl);

                    t.setKeyPointTempl(keyPointTempl);
                    t.setDescTempl(descTempl);
                }
            else{
                    descTempl = t.getDescTempl();
                }

            MatOfDMatch matchWithT = new MatOfDMatch();
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            //matcher.radiusMatch(descImgT, descTempl, matchWithT,200);//
             matcher.match(descImgT, descTempl, matchWithT);
            List<DMatch> matchList = matchWithT.toList();
//            float min = Float.MAX_VALUE;
//            float max = Float.MIN_VALUE;
//            for(int i=0;i<matchList.size();i++){
//                min = matchList.get(i).distance<min?matchList.get(i).distance:min;
//                max = matchList.get(i).distance>max?matchList.get(i).distance:max;
//            }
//            Log.i("min distance","min distance is::"+min+"max distance::"+max+"size::"+matchList.size());

//            Collections.sort(matchList, new Comparator<DMatch>() {
//                @Override
//                public int compare(DMatch o1, DMatch o2) {
//                    if (o1.distance < o2.distance)
//                        return -1;
//                    if (o1.distance > o2.distance)
//                        return 1;
//                    return 0;
//                }
//            });


            float ratio = -1;
            if(matchList.size()>0)
                ratio = findMinTwoRatio(matchList);

            if(ratio>0.8 || ratio ==-1)
                continue;
            Log.i("match","ratio::"+ratio);


            //Todo:revisit logic
            if(matches==null ||(matchWithT.size().height> matches.size().height)){
                matches = matchWithT;
                keyPointImg = keyPointImgT;
                descImg = descImgT;
                best =t;
            }

        }


      //  Log.i("perform match result", matches.size().toString());

        return best;


    }

    private float findMinTwoRatio(List<DMatch> matchList){
        float min1 = Float.MAX_VALUE;
        float min2 = min1;

        for(int i=0;i<matchList.size();i++){
                if(matchList.get(i).distance<min1){
                    min2 = min1;
                    min1 = matchList.get(i).distance;
                }
            else if(matchList.get(i).distance<min2){
                    min2 = matchList.get(i).distance;
                }

        }

        return min1/min2;
    }



    private Mat readImgFromFile(String fileName){
        Bitmap myBitmap = BitmapFactory.decodeFile(fileName);
        Bitmap myBitmap32 = myBitmap.copy(Bitmap.Config.ARGB_8888, true);

        //TODO: check this piece of code in case of bugs
        Mat templ = new Mat();
        Utils.bitmapToMat(myBitmap,templ);
        return templ;
    }


    public MatOfPoint getContourOfBestMatch(){

        if(matches==null)
            return null;

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
            if(contourarea<50)
                continue;

            Rect r = Imgproc.boundingRect(contours.get(idx));

            double count = 0;
           // Log.i("contour area","contour area is::"+contourarea);
            for(DMatch match:matchList)
            {

                Point q = keyPointList.get(match.queryIdx).pt;
                if(q.x>=r.x && q.x<=(r.x+r.width) && q.y>=r.y && q.y<=(r.y+r.height))
                    count++;

//                if(Imgproc.pointPolygonTest(ctr2f,keyPointList.get(match.queryIdx).pt,true)>0){
//                    if(null ==contourDensityMap.get(idx))
//                        contourDensityMap.put(idx,1.0);
//
//                    else{
//                        contourDensityMap.put(idx,((Double)contourDensityMap.get(idx))+1);
//                    }
//
//                }

            }
//            if(contourDensityMap.containsKey(idx)) {
//                Log.i("contourPoint","idx::"+idx+"count::"+contourDensityMap.get(idx)+"contour area::"+contourarea);
//                contourDensityMap.put(idx, contourDensityMap.get(idx) / contourarea);
//            }
            if(count!=0){
                contourDensityMap.put(idx, count / contourarea);
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
        Log.i("maxEntry::",""+(maxEntry==null?null:maxEntry.getKey()));
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
