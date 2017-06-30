package ucast.com.padupdate.socket.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pj on 2016/11/22.
 */
public class DizuoLogMessage extends MessageBase {

    public List<String> logs=new ArrayList<>();


    @Override
    public void Load(String[] str) {
        this.Cmd = str[0];
        for (int i = 1; i < str.length; i++) {
            this.logs.add(str[i]);
        }
    }
}
