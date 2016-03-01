package apexbio.smbgsql;

/**
 * Created by A1302 on 2015/7/7.
 */

public class GData implements java.io.Serializable{
    private long id;
    private long Uid;
    private String Metername;
    private String GDataDate;
    private String GDataTime;
    private String GDataFlag;
    private int Gvalue;
    private String GNote;

    public GData() {
        Uid = 0;
        Metername = "";
        GDataDate = "";
        GDataTime = "";
        GDataFlag = "";
        Gvalue = 0;
        GNote = "";
    }

    public GData(long id, long Uid, String Metername, String GDataDate,
                 String GDataTime, String GDataFlag, int Gvalue, String GNote) {
        this.id = id;
        this.Uid = Uid;
        this.Metername = Metername;
        this.GDataDate = GDataDate;
        this.GDataTime = GDataTime;
        this.GDataFlag = GDataFlag;
        this.Gvalue = Gvalue;
        this.GNote = GNote;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return Uid;
    }

    public void setUid(long Uid) {
        this.Uid = Uid;
    }

    public String getMetername() {
        return Metername;
    }

    public void setMetername(String Metername) {
        this.Metername = Metername;
    }

    public String getGDataDate() {
        return GDataDate;
    }

    public void setGDataDate(String GDataDate) {
        this.GDataDate = GDataDate;
    }

    public String getGDataTime() {
        return GDataTime;
    }

    public void setGDataTime(String GDataTime) {
        this.GDataTime = GDataTime;
    }

    public String getGDataFlag() {
        return GDataFlag;
    }

    public void setGDataFlag(String GDataFlag) {
        this.GDataFlag = GDataFlag;
    }

    public int getGvalue() {
        return Gvalue;
    }

    public void setGvalue(int Gvalue) {
        this.Gvalue = Gvalue;
    }

    public String getGNote() {
        return GNote;
    }

    public void setGNote(String GNote) {
        this.GNote = GNote;
    }
}