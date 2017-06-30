package ucast.com.padupdate.socket.Message;

/**
 * Created by pj on 2016/11/22.
 */
public class UploadResultMessage extends MessageBase{

   public  boolean isUpload;


    @Override
    public void Load(String[] str) {
        this.Cmd=str[0];
        if (str[1].equals("1")){
            this.isUpload=true;
        }else{
            this.isUpload=false;
        }
    }
}
