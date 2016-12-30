package test.identify.cb.com.testapplication;

import android.app.Activity;
import android.content.Context;
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

public class Register extends Activity {
    private String accountstr;
    private String pwdstr;
    private EditText account;
    private EditText pwd;
    private Context context;
    private static Activity activity;
    private static FinalHttp fh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        this.context = this;
        activity = this;
        account = (EditText) findViewById(R.id.accountEt);
        pwd = (EditText) findViewById(R.id.pwdEt);
        Button subbtn = (Button) findViewById(R.id.subBtn);

        subbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                accountstr = account.getText().toString();
                pwdstr = pwd.getText().toString();
                if(accountstr.length() == 0 || pwdstr.length() == 0){
                    Toast.makeText(context, "数据不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                clientRegister(accountstr,pwdstr);
            }

        });

    }
    @SuppressWarnings("unchecked")
    public void clientRegister(final String accountname,final String pwd){
        if(fh==null){
            fh = new FinalHttp();
        }
        AjaxParams params = new AjaxParams();
        params.put("method", "userRegister");
        params.put("userID", accountname);
        params.put("password", pwd);
        fh.post("https://www.erastone.com.cn:7780/api",params, new AjaxCallBack(){
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String code ;
                String message ;
                JSONObject object ;
                try {
                    object = new JSONObject(t.toString());
                    code = object.getString("code");
                    message = object.getString("message");
                    if (code.equals("0")) {
                        Log.e("debug", "注册成功");
                        Login.clientLogin(accountname, pwd);
                    } else {
                        Log.e("debug", code+message);
                        Toast.makeText(context,code+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

        });
    }
    public static void finishRegistLayou(){
        activity.finish();
    }
}
