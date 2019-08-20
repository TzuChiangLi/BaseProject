package com.ftrend.zgp.utils.permission;

import android.Manifest;

import com.ftrend.zgp.utils.log.LogUtil;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

/**
 * @author LZQ
 * @content 权限申请工具类
 */
public class PermissionUtil {
    /**
     * 申请读写外部存储、读取设备状态、摄像头权限
     */
    public static void checkAndRequestPermission() {
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {
                        String refusedPermission = "";
                        for (int i = 0; i < refusedPermissions.length; i++) {
                            refusedPermission += String.format("%s%s\n", "以下权限申请失败：", refusedPermissions[i]);
                        }
                        LogUtil.e(refusedPermission);
                        showRefusedDialog(refusedPermissions);
                    }
                });
    }

    /**
     * @param refusedPermissions 拒绝的权限组
     * @content 根据拒绝的内容显示弹窗提示用户
     */
    public static void showRefusedDialog(Permission[] refusedPermissions) {


    }
}
