package io.electra.common.server;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class PipelineUtils {

    private static boolean epoll;

    static {
        epoll = Epoll.isAvailable();
    }

    public static EventLoopGroup newEventLoopGroup(int threads, ThreadFactory threadFactory) {
        return epoll ? new EpollEventLoopGroup(threads, threadFactory) : new NioEventLoopGroup(threads, threadFactory);
    }

    public static Class<? extends ServerChannel> getServerChannel() {
        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel() {
        return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends Channel> getDatagramChannel() {
        return epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
    }

    public static boolean isEpoll() {
        return epoll;
    }
}
