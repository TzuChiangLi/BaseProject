package com.ftrend.zgp.utils.pop;

/**
 * @author liziqiang@ftrend.cn
 */

public interface ServerUrlInputCallback {
    /**
     * 用户输入完毕
     */
    void onOk(String value);

    /**
     * 用户取消输入
     */
    void onCancel();

    /**
     * 校验输入信息是否有效
     *
     * @param value
     * @return 错误提示信息，为空时表明信息校验通过
     */
    String validate(String value);
}
