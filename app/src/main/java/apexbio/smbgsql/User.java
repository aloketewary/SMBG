package apexbio.smbgsql;

/**
 * Created by A1302 on 2015/7/27.
 */
public class User implements java.io.Serializable{

    private long uid;
    private String account, pwd, uname, ubirthday, email;
    private int gender, high, low;
    private double height, weight;

    public User(){
        uid = 0;
        account = "";
        pwd = "";
        uname = "";
        gender = 0;
        ubirthday = "";
        height = 0;
        weight = 0;
        email = "";
        high = 0;
        low = 0;
    }

    public User(long uid, String account, String pwd, String uname
            , int gender, String ubirthday, double height, double weight, String email, int high, int low){
        this.uid = uid;
        this.account = account;
        this.pwd = pwd;
        this.uname = uname;
        this.gender = gender;
        this.ubirthday = ubirthday;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.high = high;
        this.low = low;
    }

    public long getUid(){
        return uid;
    }

    public void setUid(long uid){
        this.uid = uid;
    }

    public String getAccount(){
        return account;
    }

    public void setAccount(String account){
        this.account = account;
    }

    public String getPwd(){
        return pwd;
    }

    public void setPwd(String pwd){
        this.pwd = pwd;
    }

    public String getUname(){
        return uname;
    }

    public void setUname(String uname){
        this.uname = uname;
    }

    public int getGender(){
        return gender;
    }

    public void setGender(int gender){
        this.gender = gender;
    }

    public String getUbirthday(){
        return ubirthday;
    }

    public void setUbirthday(String ubirthday){
        this.ubirthday = ubirthday;
    }

    public double getHeight(){
        return height;
    }

    public void setHeight(double height){
        this.height = height;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public int getHigh(){
        return high;
    }

    public void setHigh(int high){
        this.high = high;
    }

    public int getLow(){
        return low;
    }

    public void setLow(int low){
        this.low = low;
    }
}
