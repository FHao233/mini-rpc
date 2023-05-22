package com.fhao;

//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
import com.fhao.rpc.core.serialize.fastjson.FastJsonSerializeFactory;

/**
* author: FHao
* create time: ${YEAR}-${MONTH}-${DAY} ${HOUR}:${MINUTE}
* description:
*/public class Main {
   public static class Test{
        private Throwable throwable;
        private int a;

       public int getA() {
           return a;
       }

       public void setA(int a) {
           this.a = a;
       }

       public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.setA(1);
        try {
            int a = 1/0;
        }catch (Exception e){
            test.setThrowable(e);
        }
        FastJsonSerializeFactory fastJsonSerializeFactory = new FastJsonSerializeFactory();
        byte[] s = fastJsonSerializeFactory.serialize(test);
        String s1 = new String(s);
        System.out.println(s1);
        Test test1 = fastJsonSerializeFactory.deserialize(s, Test.class);
        test1.getThrowable().printStackTrace();
    }
}