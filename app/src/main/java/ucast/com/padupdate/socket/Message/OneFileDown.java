package ucast.com.padupdate.socket.Message;

/**
 * Created by Allen on 2017/1/6.
 */

public class OneFileDown extends MessageBase {
    public String fileName;

    @Override
    public void Load(String[] Str) {
        this.Cmd=Str[0];
        this.fileName=Str[1];
    }
}
