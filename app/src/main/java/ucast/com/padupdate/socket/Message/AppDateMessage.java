package ucast.com.padupdate.socket.Message;

/**
 * Created by pj on 2016/11/22.
 */
public class AppDateMessage  extends MessageBase{

   public String version;


    @Override
    public void Load(String[] str) {
        this.Cmd=str[0];
        this.version=str[1];
    }
}
