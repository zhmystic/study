package com.suncar.card.service.controller.nio;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-05-31 14:49
 * @description
 */
public class LengthCalculator extends Thread{

    private SocketChannel socketChannel;

    public LengthCalculator(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            //从通道中读取数据
            //先分配一块儿缓存，因为NIO是通道面向缓存的，指定大小为1024
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(byteBuffer);
            //读取完之后一定要加上这句话，刷新缓冲区
            byteBuffer.flip();
            //转换为字节数组,然后转为字符串，打印数据
            byte[] array = byteBuffer.array();
            String content = new String(array).trim();
            System.out.println("客户端传来的数据为：" + content);

            //同时需要将客户端传来的字符串的长度回传会给客户端
            ByteBuffer writeBuffer = ByteBuffer.wrap(String.valueOf(content.length()).getBytes());
            socketChannel.write(writeBuffer);
            //关闭通道
            socketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
