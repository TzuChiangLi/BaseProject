package com.ftrend.zgp.model;


import java.util.Date;

/**
 * 用户操作记录实体类
 *
 * @author liziqiang@ftrend.cn
 */
public class UserLog {
    private int id;
    private String module;
    private String function;
    private Date occurTime;
    private String content;
    private String userCode;
    private String depCode;

    public UserLog() {
    }

    public UserLog(String module, String function,String content, String userCode, String depCode) {
        this.module = module;
        this.function = function;
        this.content = content;
        this.userCode = userCode;
        this.depCode = depCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }
}
