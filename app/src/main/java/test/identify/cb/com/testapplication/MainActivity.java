package test.identify.cb.com.testapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import identify.cb.com.mylibrary.CallbackInfo;
import identify.cb.com.mylibrary.ErastonAuth;
import identify.cb.com.mylibrary.HttpCallBack;


public class MainActivity extends Activity implements View.OnClickListener {
    private Button btn_bind;
    private Button btn_checkinfo;
    private Button btn_checkout;
    private static Activity activity;
    private static Context context;
    private static String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activity = this;
        this.context = this;
        Intent i = getIntent();
        userName = i.getStringExtra("username");
        btn_bind = (Button) findViewById(R.id.bind);
        btn_checkinfo = (Button) findViewById(R.id.unwrappingDevice);
        btn_checkout = (Button) findViewById(R.id.checkout);

        btn_bind.setOnClickListener(this);
        btn_checkinfo.setOnClickListener(this);
        btn_checkout.setOnClickListener(this);
    }
    static long time;
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bind:
                bind();
                break;
            case R.id.unwrappingDevice:
                unbind();
                break;
            case R.id.checkout:
                checkout();
                break;
            default:
                break;
        }
    }


    public void bind(){
        ErastonAuth.bindDevice(userName,new HttpCallBack() {
            @Override
            public void onSuccess(Object t) {
                // TODO Auto-generated method stub
                String code = null;
                String message = null;
                CallbackInfo info = (CallbackInfo) t;
                try {
                    code = info.getCode();
                    message = info.getMessage();
                    if (code.equals("0")) {
                        Toast.makeText(context,"绑定成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,message+info.getDataException(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            @Override
            public void onFailed(Throwable arg0, int arg1, String strMsg) {
                // TODO Auto-generated method stub
            }

        });
    }
    public void unbind(){
        ErastonAuth.unwrappingDevice(userName,new HttpCallBack() {

            @Override
            public void onSuccess(Object t) {
                // TODO Auto-generated method stub
                String code = null;
                String message = null;
                CallbackInfo info = (CallbackInfo) t;
                try {
                    code = info.getCode();
                    message = info.getMessage();
                    if (code.equals("0")) {
                        Toast.makeText(context,"解绑成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("debug", code+message);
                        Toast.makeText(context,code+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            @Override
            public void onFailed(Throwable arg0, int arg1, String strMsg) {
                // TODO Auto-generated method stub
                Log.e("debug", strMsg);
            }

        });
    }
    public void checkout(){
        ErastonAuth.checkoutDevice(userName,new HttpCallBack(){

            @Override
            public void onSuccess(Object t) {
                // TODO Auto-generated method stub
                String code = null;
                String message = null;
                String deviceID = null;
                boolean isSafe;
                boolean isVirtualDevice;
                CallbackInfo info = (CallbackInfo) t;
                try {
                    code = info.getCode();
                    message = info.getMessage();
                    isSafe = info.isSafe();
                    isVirtualDevice = info.isVirtualDevice();
                    deviceID = info.getDeviceID();
                    if (code.equals("0")) {
                        showDeviceFlagDialog(isSafe, deviceID, isVirtualDevice,message,0);
                    } else {
                        showDeviceFlagDialog(isSafe, deviceID, isVirtualDevice,message,1);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            @Override
            public void onFailed(Throwable arg0, int arg1, String strMsg) {
                // TODO Auto-generated method stub
                Log.e("debug", strMsg);
            }

        });
    }
    /**
     * @author cb
     * @注释: 弹出框校验设备
     */
    public static void showDeviceFlagDialog(boolean isSafe,String deviceID,boolean VirtualDevice,String message,int flag) {
        LayoutInflater layoutInflater = MainActivity.activity.getLayoutInflater();
        View customDialog = layoutInflater.inflate(R.layout.device_flag_view, null);
        TextView checkresult = (TextView) customDialog.findViewById(R.id.checkresult);
        TextView deviceid = (TextView) customDialog.findViewById(R.id.deviceid);
        TextView isVirtualDevice = (TextView) customDialog.findViewById(R.id.isVirtualDevice);
        TextView deviceIsSafe = (TextView) customDialog.findViewById(R.id.issafe);
        TextView hidemsg = (TextView) customDialog.findViewById(R.id.hide);
        TextView isvir = (TextView) customDialog.findViewById(R.id.isvir);
        if(flag == 0){
            hidemsg.setText("");
            checkresult.setText("校验成功");
            if(VirtualDevice){
                isVirtualDevice.setText("虚拟设备");
            }else{
                isVirtualDevice.setText("真机");
            }
            if(isSafe){
                deviceIsSafe.setText("安全");
            }else{
                deviceIsSafe.setText("不安全");
            }
        }else{
            checkresult.setText("校验失败");
            isvir.setVisibility(View.INVISIBLE);
            isVirtualDevice.setVisibility(View.INVISIBLE);
            if(isSafe){
                deviceIsSafe.setText("安全");
            }else{
                deviceIsSafe.setText("不安全");
            }
            hidemsg.setText(message);
        }

        deviceid.setText(deviceID);
        final Dialog dialog = new AlertDialog.Builder(MainActivity.activity)
                .create();
        dialog.show();
        dialog.getWindow().setContentView(customDialog);
        dialog.setCanceledOnTouchOutside(false);
        customDialog.findViewById(R.id.sureBtn).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                    }
                });

    }
}
