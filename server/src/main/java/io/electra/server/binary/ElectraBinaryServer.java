package io.electra.server.binary;

import io.electra.common.server.ElectraChannelInitializer;
import io.electra.server.Database;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraBinaryServer {

    private static ElectraBinaryServer instance;

    private Database database;

    public ElectraBinaryServer(Database database) {
        ElectraBinaryServer.instance = this;

        this.database = database;
    }

    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup group = new NioEventLoopGroup(8);

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss, group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress("localhost", 9999))
                    .childHandler(new ElectraChannelInitializer(new ElectraServerHandler()));
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Database getDatabase() {
        return database;
    }

    public static ElectraBinaryServer getInstance() {
        return instance;
    }
}
