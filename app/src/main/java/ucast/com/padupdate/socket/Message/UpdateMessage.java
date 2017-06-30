package ucast.com.padupdate.socket.Message;

/**
 * Created by pj on 2016/11/22.
 */
public class UpdateMessage extends MessageBase{

   public  boolean isUpdate;


    @Override
    public void Load(String[] str) {
        this.Cmd=str[0];
        if (str[1].equals("1")){
            this.isUpdate=true;
        }else{
            this.isUpdate=false;
        }
    }
}
