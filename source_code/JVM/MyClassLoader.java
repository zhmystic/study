package com.suncar.card.service.controller.reflect;

import java.io.*;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-04 15:55
 * @description     自定义加载器
 */
public class MyClassLoader extends ClassLoader {
    //路径
    private String path;
    //加载器名称
    private String classloaderName;

    public MyClassLoader(String path, String classloaderName) {
        this.path = path;
        this.classloaderName = classloaderName;
    }

    //用于寻找类文件
    @Override
    public Class findClass(String name){
        byte[] b = loadClassData(name);
        //返回整个class文件的字节流
        return defineClass(name,b,0,b.length);
    }

    //用于加载类文件
    private byte[] loadClassData(String name) {
        name = path + name + ".class";
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(new File(name));
            out = new ByteArrayOutputStream();
            int i = 0;
            while((i = in.read()) != -1) {
                out.write(i);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return out.toByteArray();
    }
}
