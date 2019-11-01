package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        new Application().run();
    }

    void run() throws Exception {
        URI uri = new URI(System.getenv("URL"));
        // "https://auth-integ-service-dot-cognac-prod.appspot.com/.well-known/jwks.json"

        boolean cert = true;
        if ("false".equalsIgnoreCase(System.getenv("CERT"))) {
            cert = false;
        }


        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        boolean ssl = "https".equalsIgnoreCase(scheme);

        EventLoopGroup group= new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer(ssl, cert));

            LOG.info("method=\"run\" msg=\"\" host={} port={}", host, port);
            Channel ch = b.connect(host, port).sync().channel();

            HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

            ch.writeAndFlush(request);
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
