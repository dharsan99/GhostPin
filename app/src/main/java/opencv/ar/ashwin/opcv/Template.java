package opencv.ar.ashwin.opcv;

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

}
