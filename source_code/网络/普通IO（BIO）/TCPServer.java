package com.suncar.card.service.controller.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-05-31 11:09
 * @description     服务器端
 */
public class TCPServer {

    public static void main(String[] args) throws IOException {
        //创建socket，并将socket绑定到65000端口
        ServerSocket serverSocket = new ServerSocket(65000);
        //写死循环，模拟socket一直等待并处理客户端发送过来的请求
        while (true) {
            //监听65000端口，直到客户端返回连接accept信息后才返回
            Socket socket = serverSocket.accept();
            //获取客户端的请求信息后，执行相关业务逻辑
            new LengthCalculator(socket).start();
        }


    }
}



