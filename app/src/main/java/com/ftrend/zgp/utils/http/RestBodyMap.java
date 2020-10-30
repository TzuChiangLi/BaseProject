package com.ftrend.zgp.utils.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RestBodyMap
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin
 * @since 2019/11/18
 */
public class RestBodyMap extends HashMap<String, Object> {

    public RestBodyMap() {
    }

    public RestBodyMap(Map<String, Object> src) {
        putAll(src);
    }

    public String getString(final String key, final String def) {
        if (containsKey(key)) {
            Object value = get(key);
            return value == null ? def : String.valueOf(value);
        } else {
            return def;
        }
    }

    public String getString(final String key) {
        return getString(key, "");
    }

    public int getInt(final String key, final int def) {
        try {
            return Integer.parseInt(getString(key));
        } catch (Exception e) {
            return def;
        }
    }

    public int getInt(final String key) {
        return getInt(key, 0);
    }

    public double getDouble(final String key, final double def) {
        try {
            return Double.parseDouble(getString(key));
        } catch (Exception e) {
            return def;
        }
    }

    public double getDouble(final String key) {
        return getDouble(key, 0.0);
    }

    public boolean getBool(final String key, final boolean def) {
        try {
            return Boolean.parseBoolean(getString(key));
        } catch (Exception e) {
            return def;
        }
    }

    public boolean getBool(final String key) {
        return getBool(key, false);
    }

    public RestBodyMap getMap(final String key) {
        try {
            return containsKey(key) ? new RestBodyMap((Map<String, Object>) get(key)) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<RestBodyMap> getMapList(final String key) {
        try {
            List<Map<String, Object>> src = (List<Map<String, Object>>) get(key);
            if (src == null) {
                return null;
            }
            List<RestBodyMap> list = new ArrayList<>();
            for (Map<String, Object> map : src) {
                list.add(new RestBodyMap(map));
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getStringList(final String key) {
        try {
            return containsKey(key) ? (List<String>) get(key) : null;
        } catch (Exception e) {
            return null;
        }
    }

}
