package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

public class MyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到请求");
        //处理请求
        if(msg instanceof HttpRequest){
            DefaultHttpRequest request = (DefaultHttpRequest)msg;
            System.out.println("URI:"+request.uri());
            //System.err.println(msg);
        }

        //处理内容
        if (msg instanceof HttpContent){
            LastHttpContent  httpContent = (LastHttpContent) msg;
            ByteBuf bufData = httpContent.content();
            if(!(bufData instanceof EmptyByteBuf)){
                byte[] msgBytes = new byte[bufData.readableBytes()];
                bufData.readBytes(msgBytes);
                System.out.println(new String(msgBytes, Charset.forName("UTF-8")));
            }

        }
        String sendMsg = "服务端测试";

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(sendMsg.getBytes(Charset.forName("UTF-8"))));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        ctx.write(response);
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
