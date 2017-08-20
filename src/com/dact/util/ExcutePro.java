package com.dact.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ExcutePro {

    public static Map<String, String> getProperties(String path) throws Exception {
        Resource resource = new ClassPathResource(path);
        Properties props = PropertiesLoaderUtils.loadProperties(resource);
        Map<String, String> param = new HashMap<String, String>((Map) props);
        return param;
    }
}
