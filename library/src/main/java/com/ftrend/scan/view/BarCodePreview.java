package com.ftrend.scan.view;

import android.content.Context;
import android.util.AttributeSet;

import com.ftrend.scan.core.BarCodeScanConfig;
import com.ftrend.scan.inter.OnBarCodeScanResultListener;

/***
 * @author kdp
 * @date 2019/1/15 14:27
 * @description
 */
public class BarCodePreview extends AbBarCodeSurfaceView {
    private BarCodeScanConfig barCodeScanConfig;

    public BarCodePreview(Context context) {
        this(context, null);
    }

    public BarCodePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setBarCodeScanConfig(BarCodeScanConfig barCodeScanConfig) {
        this.barCodeScanConfig = barCodeScanConfig;
    }

    public BarCodeScanConfig getBarCodeScanConfig() {
        return barCodeScanConfig;
    }

    public void setOnBarCodeScanResultListener(OnBarCodeScanResultListener listener) {
        this.listener = listener;
    }

    /**
     * 打开摄像头
     */
    @Override
    public void openCamera() {
        cameraManager.closeCamera();
        if (isSurfaceCreate) {
            openCamera(getHolder());
        } else {
            getHolder().addCallback(this);
        }
    }

    /**
     * 关闭摄像头
     */
    @Override
    public void closeCamera() {
        stopRecognize();
        cameraManager.closeCamera();
        if (!isSurfaceCreate) {
            getHolder().removeCallback(this);
        }
    }

    /**
     * 开始预览
     */
    @Override
    public void startPreview() {
        cameraManager.startPreview();
    }

    /**
     * 停止预览
     */
    @Override
    public void stopPreview() {
        cameraManager.stopPreview();
    }


    /**
     * 开始识别
     */
    @Override
    public void startRecognize() {
        barCodeProcessor.startDecode();
        requestPreviewFrame();
    }


    /**
     * 停止识别
     */
    @Override
    public void stopRecognize() {
        barCodeProcessor.stopDecode();
    }

    @Override
    public void turnOnFlashLight() {
        cameraManager.turnOnFlashLight();
    }

    @Override
    public void turnOffFlashLight() {
        cameraManager.turnOffFlashLight();
    }

}
