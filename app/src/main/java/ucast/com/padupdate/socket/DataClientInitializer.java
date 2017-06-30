package ucast.com.padupdate.socket;


import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import ucast.com.padupdate.socket.MessageCallback.CallbackHandle;
import ucast.com.padupdate.socket.MessageCallback.IMsgCallback;
import ucast.com.padupdate.socket.MessageProtocol.StationPackage;

/**
 * Created by Administrator on 2016/2/4.
 */
public class DataClientInitializer extends ChannelInitializer {

    public IMsgCallback callback;

    public DataClientInitializer() {
        callback = new CallbackHandle();
    }

    public void initChannel(Channel channel) {
        StationPackage stationPackage = new StationPackage(channel);
        stationPackage.callback = callback;
        TcpClientHandle handle = new TcpClientHandle(stationPackage);
        channel.pipeline().addLast("idleStateHandler", new IdleStateHandler(300000, 0,0, TimeUnit.MILLISECONDS));
        channel.pipeline().addLast("handler", handle);
    }

}
