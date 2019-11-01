package netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientInitializer.class);

    private final boolean ssl;
    private final boolean cert;


    public HttpClientInitializer(boolean ssl, boolean cert) {
        this.ssl = ssl;
        this.cert = cert;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (ssl) {
            SslHandler handler = getSslHandler(ch.alloc());
            p.addLast(handler);
        }

        p.addLast(new HttpClientCodec());
        p.addLast(new HttpClientHandler());
    }

    private SslHandler getSslHandler(ByteBufAllocator alloc) {
        try {
            SslContextBuilder ctxBuilder = SslContextBuilder.forClient();
            if (!cert) {
                ctxBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
            }

            return ctxBuilder.build().newHandler(alloc);
        } catch(SSLException e) {
            LOG.error("method=\"nettySslHandler\" ssl={} cert={}", ssl, cert, e);
            throw new IllegalStateException(e);
        }
    }
}
