package com.ftrend.zgp.utils.http;

import java.util.HashMap;
import java.util.List;

/**
 * RestBodyMap
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin
 * @since 2019/11/18
 */
public class RestBodyMap extends HashMap<String, Object> {

    public String getString(final String key, final String def) {
        if (containsKey(key)) {
            return String.valueOf(get(key));
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
            return (RestBodyMap) get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public List<RestBodyMap> getMapList(final String key) {
        try {
            return (List<RestBodyMap>) get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getStringList(final String key) {
        try {
            return (List<String>) get(key);
        } catch (Exception e) {
            return null;
        }
    }

}
