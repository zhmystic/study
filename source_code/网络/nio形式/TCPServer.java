package com.suncar.card.service.controller.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-05-31 14:47
 * @description NIO实现 服务器端
 */
public class TCPServer {

    public static void main(String[] args) {

        try {
            //打开一个通道,并绑定监听65000端口
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(65000));
            //创建无限循环，模拟一直监听65000端口
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                new LengthCalculator(socketChannel).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
