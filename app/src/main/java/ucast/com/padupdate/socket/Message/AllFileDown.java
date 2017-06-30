package ucast.com.padupdate.socket.Message;

/**
 * Created by Allen on 2017/1/6.
 */

public class AllFileDown extends MessageBase {

    @Override
    public void Load(String[] Str) {
        this.Cmd=Str[0];
    }
}
