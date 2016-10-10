package com.example.chirs.rxsimpledemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.User;
import com.example.webserviceutil.WebService;
import com.example.webserviceutil.callBack.ProgressCollectionCallBack;
import com.example.webserviceutil.entity.FileObject;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.service.Service;

import java.io.File;
import java.util.List;

import rx.Subscription;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class PostCollectionDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private Subscription subscription;
    private TextView resultTv;

    private final static String TAG = "PostCollectionData";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_collection);
        initView();
        initEvent();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    public void initView() {
        searchBt = (Button)findViewById(R.id.gcAct_bt);
        resultTv = (TextView)findViewById(R.id.gcAct_result);
    }

    public void initEvent() {
        searchBt.setOnClickListener(this);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                WebService.cancel(subscription);
                Toast.makeText(PostCollectionDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gcAct_bt:
                subscription = getCollectionData();
                break;
        }
    }

    private Subscription getCollectionData() {
        showProgress("正在查询...");
        resultTv.setText("");

        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava/";
        File file = new File(basePath);
        String[] fileNameArray = file.list();


        WebServiceParam param = new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_uploadList.action", Service.POST_TYPE, User.class);
        param.addParam("user.name", "Anima18");
        param.addParam("user.password", "123456");
        for(String fileName : fileNameArray) {
            Log.d(TAG, fileName);
            param.addParam(fileName, new FileObject(basePath + fileName));
        }
        return WebService.uploadFile(PostCollectionDataActivity.this, param, new ProgressCollectionCallBack<User>() {
            @Override
            public void onProgress(String fileName, int progress) {
                updataProgress(fileName, progress);
            }

            @Override
            public void onSuccess(List<User> data) {
                resultTv.setText(data.toString());
            }

            @Override
            public void onFailure(int code, String message) {
                resultTv.setText("code："+ code +", message:"+message);
            }

            @Override
            public void onCompleted() {
                hideProgress();
            }
        });
    }
}
