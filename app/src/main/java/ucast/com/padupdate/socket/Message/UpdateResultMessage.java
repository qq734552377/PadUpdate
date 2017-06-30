package ucast.com.padupdate.socket.Message;

/**
 * Created by pj on 2016/11/22.
 */
public class UpdateResultMessage extends MessageBase{

   public  boolean isSucess;


    @Override
    public void Load(String[] str) {
        this.Cmd=str[0];
        if (str[1].equals("1")){
            this.isSucess=true;
        }else{
            this.isSucess=false;
        }
    }
}
