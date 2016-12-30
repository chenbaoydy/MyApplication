package test.identify.cb.com.testapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import identify.cb.com.mylibrary.CallbackInfo;
import identify.cb.com.mylibrary.ErastonAuth;

public class Login extends Activity implements View.OnClickListener {
    private String accountstr;
    private String pwdstr;
    private EditText account;
    private EditText pwd;
    private static Context context;
    private static Activity activity;
    private static boolean isRegist;   //是否注册界面跳转过来登录标记
    private static FinalHttp fh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = this;
        this.activity = this;
        isRegist = false;
        ErastonAuth.init(activity, context, "sd0001");
        account = (EditText) findViewById(R.id.accountEt);
        pwd = (EditText) findViewById(R.id.pwdEt);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        Button regsterBtn = (Button) findViewById(R.id.regsterBtn);
        loginBtn.setOnClickListener(this);
        regsterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                accountstr = account.getText().toString();
                pwdstr = pwd.getText().toString();
                if (accountstr.length() == 0 || pwdstr.length() == 0) {
                    Toast.makeText(context, "数据不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                clientLogin(accountstr, pwdstr);
                break;
            case R.id.regsterBtn:
                isRegist = true;
                Intent intent = new Intent(Login.this, Register.class);
                Login.this.startActivity(intent);
                break;
            default:
                break;
        }
    }

    public static void clientLogin(final String accountname, final String pwd) {
        if (fh == null) {
            fh = new FinalHttp();
        }
        AjaxParams params = new AjaxParams();
        params.put("method", "userLogin");
        params.put("userID", accountname);
        params.put("password", pwd);
        final CallbackInfo callbackinfo = new CallbackInfo();
        fh.post("https://www.erastone.com.cn:7780/api", params, new AjaxCallBack() {
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String code = null;
                String message = null;
                JSONObject object = null;
                try {
                    object = new JSONObject(t.toString());
                    code = object.getString("code");
                    message = object.getString("message");
                    if (code.equals("0")) {
                        Log.e("debug", "登录成功");
                        Intent intentMain = new Intent(context, MainActivity.class);
                        intentMain.putExtra("username", accountname);
                        Login.context.startActivity(intentMain);
                        if(isRegist)
                            Register.finishRegistLayou();
                    } else {
                        isRegist = false;
                        Log.e("debug", code+message);
                        Toast.makeText(context,code+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    isRegist = false;
                    // TODO: handle exception
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                isRegist = false;
                Log.e("debug", "aarr"+strMsg);
            }
        });
    }


}
