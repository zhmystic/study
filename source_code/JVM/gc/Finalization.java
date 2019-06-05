package com.suncar.card.service.controller.gc;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * <P>
 * Copyright 2019 sun_car All Rights Reserved.
 * </p>
 *
 * @author zhmystic.
 * @date 2019-06-05 18:38
 * @description
 */
public class Finalization {

    public static Finalization finalization;

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalized");
        finalization = this;
    }

    public static void main(String[] args) {
        Finalization f = new Finalization();
        System.out.println("First print：" + f);
        f = null;
        System.gc();
        try {//休息一段时间，让上面的垃圾回收线程执行完成
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Second print：" + f);
        System.out.println(f.finalization);

        String s = new String("abc");   //强引用
        SoftReference<String> softRef = new SoftReference<String>(s);   //软引用
        WeakReference<String> weakRef = new WeakReference<String>(s);   //弱引用
        ReferenceQueue queue = new ReferenceQueue<>();
        PhantomReference phantomRef = new PhantomReference(s,queue);    //虚引用

    }
}
