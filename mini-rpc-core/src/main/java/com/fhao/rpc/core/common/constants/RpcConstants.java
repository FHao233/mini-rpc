package com.fhao.rpc.core.common.constants;

/**
 * author: FHao
 * create time: 2023-04-26 15:31
 * description:
 */
public class RpcConstants {
    public static final short MAGIC_NUMBER=19812;
    public static final String JDK_PROXY_TYPE = "jdk";

    public static final String JAVASSIST_PROXY_TYPE = "javassist";

    public static final String RANDOM_ROUTER_TYPE = "random";

    public static final String ROTATE_ROUTER_TYPE = "rotate";

    public static final String JDK_SERIALIZE_TYPE = "jdk";

    public static final String FAST_JSON_SERIALIZE_TYPE = "fastJson";

    public static final Integer DEFAULT_TIMEOUT = 3000;

    public static final String DEFAULT_DECODE_CHAR = "$_i0#Xsop1_$";





    public static final int SERVER_DEFAULT_MSG_LENGTH = 1024 * 10;

    public static final int CLIENT_DEFAULT_MSG_LENGTH = 1024 * 10;


    public static final Integer DEFAULT_THREAD_NUMS = 256;

    public static final Integer DEFAULT_QUEUE_SIZE = 512;

    public static final Integer DEFAULT_MAX_CONNECTION_NUMS = DEFAULT_THREAD_NUMS + DEFAULT_QUEUE_SIZE;
}
