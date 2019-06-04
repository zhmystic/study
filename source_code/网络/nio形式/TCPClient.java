package com.suncar.card.service.controller.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-05-31 14:49
 * @description     NIO实现 客户端
 */
public class TCPClient {

    public static void main(String[] args) {
        try {
            //建立通道，并指定IP和端口
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1",65000));

            String data = "hello world";
            //将数据写入到通道中
            ByteBuffer byteBuffer = ByteBuffer.wrap(data.getBytes());
            socketChannel.write(byteBuffer);

            //读取到服务器端回传回来的字符串的长度
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(readBuffer);
            readBuffer.flip();
            byte[] array = readBuffer.array();
            String content = new String(array).trim();
            System.out.println("服务器端回传回来的字符串长度为：" + content);

            //关闭连接
            socketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
