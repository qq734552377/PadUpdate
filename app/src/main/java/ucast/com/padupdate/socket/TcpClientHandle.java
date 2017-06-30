package ucast.com.padupdate.socket;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import ucast.com.padupdate.socket.MessageProtocol.Package;

/**
 * Created by Administrator on 2016/2/4.
 */
public class TcpClientHandle extends ChannelInboundHandlerAdapter {

    public Package packageMessage;

    public TcpClientHandle(Package _packageMessage) {
        packageMessage = _packageMessage;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接来了:" + ctx.channel().id());
        String str = "@s001,1$";
        Common.SendData(str.getBytes());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (packageMessage == null)
            return;
        packageMessage.Dispose();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try {
            ByteBuf buff = (ByteBuf) msg;
            int len = buff.readableBytes();
            byte[] buffer = new byte[len];
            buff.readBytes(buffer);
            if (packageMessage == null)
                return;
            packageMessage.Import(buffer, 0, len);
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent event = (IdleStateEvent) evt;
        if(event.state() == IdleState.READER_IDLE) {
            ctx.close();
        }
    }
}
