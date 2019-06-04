package com.suncar.card.service.controller.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-05-31 11:16
 * @description
 */
public class LengthCalculator extends Thread {
    //SocketChannel为成员变量
    private Socket socket;
    public LengthCalculator(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            //获取socket的输入流和输出流
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            int ch = 0;
            //buff主要用来读取输入的内容，存入byte数组。ch主要用来获取读取数组的长度
            byte[] buff = new byte[1024];
            ch = inputStream.read(buff);
            String content = new String(buff,0,ch);
            System.out.println("客户端发送的字符串为：" + content);

            //往输出流中写入获得的字符串长度，回发给客户端
            outputStream.write(String.valueOf(content.length()).getBytes());

            //关流，关socket
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
