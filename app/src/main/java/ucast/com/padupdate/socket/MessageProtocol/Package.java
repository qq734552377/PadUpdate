package ucast.com.padupdate.socket.MessageProtocol;


import io.netty.channel.Channel;
import ucast.com.padupdate.socket.Message.MessageBase;
import ucast.com.padupdate.socket.MessageCallback.IMsgCallback;

/**
 * Created by Administrator on 2016/2/3.
 */

public abstract  class Package {

    private Channel channel;

    public IMsgCallback callback;

    protected boolean mDispose;

    public Package(Channel _channel) {
        channel = _channel;
    }

    public abstract void Import(byte[] buffer, int offset, int count) throws Exception;

    public abstract MessageBase MessageRead(byte[] data);

    public abstract MessageBase MessageRead(String str) throws Exception;

    protected void OnMessageDataReader(String str) throws Exception {
        MessageBase mesaage = MessageRead(str);
        if (mesaage == null)
            return;
        MessageReceived(mesaage);
    }

    protected void MessageReceived(MessageBase messageBase) {
        synchronized (this) {
            if (callback == null)
                return;
            callback.Receive(channel, messageBase);
        }
    }

    public void Dispose() {
        synchronized (this) {
            if (mDispose)
                return;
            mDispose = true;
            callback = null;
        }
    }
}