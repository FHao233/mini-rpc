package com.fhao.rpc.core.common.cache;

import com.fhao.rpc.core.registy.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-04-26 15:52</p>
 * <p>description:   </p>
 */
public class CommonServerCache {
    //用于存储提供者类的名称和实例
    public static final Map<String,Object> PROVIDER_CLASS_MAP = new HashMap<>();
    //用于存储提供者的URL地址
    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();
}
