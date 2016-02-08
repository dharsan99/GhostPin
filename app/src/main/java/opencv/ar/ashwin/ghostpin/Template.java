package opencv.ar.ashwin.ghostpin;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

/**
 * Created by ashwin on 23/01/16.
 */
public class Template {
    public Template(String templName, String desc) {
        this.templName = templName;
        this.desc = desc;
    }

    public String getTemplName() {
        return templName;
    }

    public void setTemplName(String templName) {
        this.templName = templName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    String templName;
    String desc;
    Mat templGray;

    public Mat getTemplGray() {
        return templGray;
    }

    public void setTemplGray(Mat templGray) {
        this.templGray = templGray;
    }

    public MatOfKeyPoint getKeyPointTempl() {
        return keyPointTempl;
    }

    public void setKeyPointTempl(MatOfKeyPoint keyPointTempl) {
        this.keyPointTempl = keyPointTempl;
    }

    public Mat getDescTempl() {
        return descTempl;
    }

    public void setDescTempl(Mat descTempl) {
        this.descTempl = descTempl;
    }

    MatOfKeyPoint keyPointTempl;
    Mat descTempl;


}
