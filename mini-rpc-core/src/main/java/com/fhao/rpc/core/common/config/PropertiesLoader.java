package com.fhao.rpc.core.common.config;

import com.fhao.rpc.core.common.utils.CommonUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * <p>author: FHao</p>
 * <p>create time: 2023-05-19 15:41</p>
 * <p>description:   </p>
 */
public class PropertiesLoader {
    private static Properties properties;

    private static Map<String, String> propertiesMap = new HashMap<>();

    private static String DEFAULT_PROPERTIES_FILE = Objects.requireNonNull(PropertiesLoader.class.getResource("/")).getPath() + "irpc.properties";
//            "E:\\IdeaProjects\\mini-rpc\\mini-rpc-core\\src\\main\\resources\\irpc.properties";

    //todo 如果这里直接使用static修饰是否可以？
    public static void loadConfiguration() throws IOException {
        if(properties!=null){
            return;
        }
        properties = new Properties();
        FileInputStream in = null;
        in = new FileInputStream(DEFAULT_PROPERTIES_FILE);
        properties.load(in);
    }

    /**
     * 根据键值获取配置属性
     *
     * @param key
     * @return
     */
    public static String getPropertiesStr(String key) {
        if (properties == null) {
            return null;
        }
        if (CommonUtils.isEmpty(key)) {
            return null;
        }
        if (!propertiesMap.containsKey(key)) {
            String value = properties.getProperty(key);
            propertiesMap.put(key, value);
        }
        return String.valueOf(propertiesMap.get(key));
    }

    /**
     * 根据键值获取配置属性
     *
     * @param key
     * @return
     */
    public static Integer getPropertiesInteger(String key) {
        if (properties == null) {
            return null;
        }
        if (CommonUtils.isEmpty(key)) {
            return null;
        }
        if (!propertiesMap.containsKey(key)) {
            String value = properties.getProperty(key);
            propertiesMap.put(key, value);
        }
        return Integer.valueOf(propertiesMap.get(key));
    }

    public static void main(String[] args) {
        String DEFAULT_PROPERTIES_FILE = PropertiesLoader.class.getResource("/").getPath() + "irpc.properties";
        System.out.println(DEFAULT_PROPERTIES_FILE);
    }
}
