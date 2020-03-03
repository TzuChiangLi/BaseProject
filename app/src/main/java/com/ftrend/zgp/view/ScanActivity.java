package com.ftrend.zgp.view;

import android.content.Intent;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.ftrend.scan.core.BarCodeScanConfig;
import com.ftrend.scan.core.BarCodeType;
import com.ftrend.scan.inter.OnBarCodeScanResultListener;
import com.ftrend.scan.view.BarCodePreview;
import com.ftrend.zgp.R;
import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.common.ScanViewUtil;
import com.ftrend.zgp.utils.log.LogUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liziqiang@ftrend.cn
 */
public class ScanActivity extends BaseActivity implements OnTitleBarListener, OnBarCodeScanResultListener {
    @BindView(R.id.barcodepreview)
    BarCodePreview mPreView;
    @BindView(R.id.scanview)
    ScanViewUtil mScanUtil;
    @BindView(R.id.scan_titlebar)
    TitleBar mTitleBar;
    @BindView(R.id.btn_turn_on_flash)
    ImageButton mOnBtn;
    @BindView(R.id.btn_turn_off_flash)
    ImageButton mOffBtn;
    public final static int SCAN_OK = 666666, SCAN_FAILURE = 666667;

    @Override
    public void onNetWorkChange(boolean isOnline) {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.scan_barcode_activity;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        //推迟界面初始化，减轻onCreate的压力
        setScanConfig();
    }

    @Override
    protected void initTitleBar() {
        ImmersionBar.with(this).titleBarMarginTop(mTitleBar).transparentBar().init();
        mTitleBar.setOnTitleBarListener(this);
    }

    @Override
    protected void onResume() {
        mPreView.openCamera();
        mPreView.startRecognize();
        mPreView.setOnBarCodeScanResultListener(this);
        super.onResume();
    }


    /**
     * 扫描配置
     */
    private void setScanConfig() {
        WindowManager windowManager = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int scanWidth = screenWidth / 6 * 4;
        int scanHeight = screenHeight / 3;
        int left = (screenWidth - scanWidth) / 2;
        int top = (screenHeight - scanHeight) / 2;
        int right = scanWidth + left;
        int bottom = scanHeight + top;

        mScanUtil.setBorder(new int[]{left, top, right, bottom});

        //识别区域
        Rect rect = new Rect(left, top, right, bottom);
        BarCodeScanConfig barCodeScanConfig = new BarCodeScanConfig.Builder()
                .setROI(rect)//识别区域
                .setAutofocus(true)//自动对焦，默认为true
                .setDisableContinuous(true)//使用连续对焦，必须在Autofocus为true的前提下，该参数才有效;默认为true
                .setBarCodeType(BarCodeType.ALL)//识别所有的条形码
//                .setBarCodeType(BarCodeType.values()[0])//识别所有的条形码
//                .setBarCodeType(BarCodeType.ONE_D_CODE)//仅识别所有的一维条形码
//                .setBarCodeType(BarCodeType.TWO_D_CODE)//仅识别所有的二维条形码
//                .setBarCodeType(BarCodeType.QR_CODE)//仅识别二维码
//                .setBarCodeType(BarCodeType.CODE_128)//仅识别CODE 128码
//                .setBarCodeType(BarCodeType.CUSTOME)//自定义条码类型，必须指定自定义识别的条形码格式
//                .setBarcodeFormats(EnumSet.of(BarcodeFormat.QR_CODE,BarcodeFormat.CODE_128))//定义识别的条形码格式
                .setSupportAutoZoom(true)//当二维码图片较小时自动放大镜头(仅支持QR_CODE)
                .build();
        mPreView.setBarCodeScanConfig(barCodeScanConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mPreView.openCamera();
            mPreView.startRecognize();
            //开启扫描动画
            mScanUtil.startScan();
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            try {
                setResult(SCAN_FAILURE, new Intent() {{
                    putExtra("scanResult", "");
                }});
            } finally {
                try {
                    mPreView.stopRecognize();
                } catch (Exception E) {
                    LogUtil.e(E.getMessage());
                }
                try {
                    mPreView.closeCamera();
                } catch (Exception E) {
                    LogUtil.e(E.getMessage());
                }
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPreView.stopRecognize();
        mPreView.closeCamera();
        mPreView.turnOffFlashLight();
        mScanUtil.stopScan();
    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }

    @Override
    public void onTitleClick(View v) {
    }

    @Override
    public void onRightClick(View v) {
    }

    @Override
    public void onSuccess(final String result) {
        try {
            setResult(SCAN_OK, new Intent() {{
                putExtra("scanResult", result);
            }});
        } finally {
            try {
                mPreView.stopRecognize();
                mPreView.closeCamera();
            } catch (Exception e) {
                LogUtil.d(e.getMessage());
            }
            finish();
        }
    }

    @Override
    public void onFailure() {
        setResult(SCAN_FAILURE);
    }

    @OnClick(R.id.btn_turn_on_flash)
    public void turnOn() {
        mPreView.turnOnFlashLight();
        mOffBtn.setVisibility(View.VISIBLE);
        mOnBtn.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_turn_off_flash)
    public void turnOff() {
        mPreView.turnOffFlashLight();
        mOffBtn.setVisibility(View.GONE);
        mOnBtn.setVisibility(View.VISIBLE);
    }
}
