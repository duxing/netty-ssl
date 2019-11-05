package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof SslHandshakeCompletionEvent) {
            LOG.info("method=\"useEventTriggered\" msg=\"ssl hand shake completed\"");
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            LOG.info("msg=\"HttpResponse\" status={}", response.status());
            if (!response.headers().isEmpty()) {
                for (CharSequence name: response.headers().names()) {
                    for (CharSequence value : response.headers().getAll(name)) {
                        // LOG.info("header=\"{}\" value=\"{}\"", name, value);
                    }
                }
            }
            return;
        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            LOG.info("msg=\"HttpContent\" content={}", content.content().toString(CharsetUtil.UTF_8));

            if (content instanceof LastHttpContent) {
                LOG.info("msg=\"LastHttpContent\"");
                ctx.close();
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("msg=\"exception\"", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
