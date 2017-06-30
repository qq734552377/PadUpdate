package ucast.com.padupdate.socket.Message;

/**
 * Created by Allen on 2017/1/5.
 */

public class FileEntityMessage extends MessageBase {
    public String fileName;
    public int sum_bao;
    public int number_bao;
    public String data;

    @Override
    public void Load(String[] Str) {
        this.Cmd=Str[0];
        this.fileName=Str[1];
        this.sum_bao=Integer.parseInt(Str[2]);
        this.number_bao=Integer.parseInt(Str[3]);
        this.data=Str[4];
    }
}
