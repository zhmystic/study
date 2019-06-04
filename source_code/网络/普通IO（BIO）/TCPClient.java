package com.suncar.card.service.controller.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-05-31 11:09
 * @description
 */
public class TCPClient {

    public static void main(String[] args) throws IOException {

        String s = "hello world";
        //创建socket，并指定连接的是本机IP，65000端口
        Socket socket = new Socket("127.0.0.1",65000);
        //创建输出流和输入流
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        //将要传递的字符串通过输出流写入
        outputStream.write(s.getBytes());

        int ch = 0;
        byte[] buff = new byte[1024];
        //buff用来读取输入的内容，存成byte数组。ch用来获取读取数组的长度。
        ch = inputStream.read(buff);
        //将接收的流的byte数组转化成字符串，这是从服务端回传回来的字符串长度。
        String content = new String(buff,0,ch);
        System.out.println("服务器回传回来的字符串的长度为：" + content);

        //关流
        outputStream.close();
        inputStream.close();
        socket.close();



    }
}
