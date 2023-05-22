package com.fhao.rpc.core.common.config;

import com.fhao.rpc.core.common.utils.CommonUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static String DEFAULT_PROPERTIES_FILE = "irpc.properties";


    public static void loadConfiguration() throws IOException {
        if (properties != null) {
            return;
        }
        properties = new Properties();
        InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE);
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
            if(value == null || value.equals("")){
                return null;
            }
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
            if(value == null || value.equals("")){
                return null;
            }
            propertiesMap.put(key, value);
        }
        return Integer.valueOf(propertiesMap.get(key));
    }

    public static String getPropertiesStrDefault(String key, String defaultVal) {
        if (properties == null) {
            return defaultVal;
        }
        if (CommonUtils.isEmpty(key)) {
            return defaultVal;
        }

        String val = getPropertiesStr(key);
        if (val == null || val.equals("") ) {
            propertiesMap.put(key, String.valueOf(defaultVal));
            return defaultVal;
        }
        if (!propertiesMap.containsKey(key)) {
            String value = properties.getProperty(key);
            propertiesMap.put(key, value);
        }
        return  String.valueOf(propertiesMap.get(key));
    }

    public static Integer getPropertiesIntegerDefault(String key, Integer defaultVal) {
        if (properties == null) {
            return defaultVal;
        }
        if (CommonUtils.isEmpty(key)) {
            return defaultVal;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            propertiesMap.put(key, String.valueOf(defaultVal));
            return defaultVal;
        }
        if (!propertiesMap.containsKey(key)) {
            propertiesMap.put(key, value);
        }
        return Integer.valueOf(propertiesMap.get(key));
    }

    public static String getPropertiesNotBlank(String key) {
        String val = getPropertiesStr(key);
        if (val == null || val.equals("")) {
            throw new IllegalArgumentException(key + " 配置为空异常");
        }
        return val;
    }

    public static void main(String[] args) {
        String DEFAULT_PROPERTIES_FILE = PropertiesLoader.class.getResource("/").getPath() + "irpc.properties";
        System.out.println(DEFAULT_PROPERTIES_FILE);
    }
}
