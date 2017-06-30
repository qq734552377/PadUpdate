package ucast.com.padupdate.socket.Message;

/**
 * Created by Administrator on 2016/6/6.
 */
public class DizuoMacMessage extends MessageBase {
    public String mac;

    public void Load(String[] str) {
        super.Load(str);
        Cmd = str[0];
        mac = str[1];
    }
}
