package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.ObjectShowData;
import com.example.chirs.rxsimpledemo.entity.User;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.service.Service;
import com.google.gson.reflect.TypeToken;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetZipDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private TextView resultTv;
    private Subscription subscription;


    private ObjectShowData showObject = new ObjectShowData();
    private DataObject<User> dataObject = new DataObject<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_object);
        initView();
        initEvent();
    }

    public void initView() {
        searchBt = (Button)findViewById(R.id.goAct_bt);
        resultTv = (TextView)findViewById(R.id.goAct_result);
    }

    public void initEvent() {
        searchBt.setOnClickListener(this);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                NetworkRequest.cancel(subscription);
                //Toast.makeText(GetSeqDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goAct_bt:
                subscription = getObjectData();
                break;
        }
    }

    private Subscription getObjectData() {
        resultTv.setText("");
        showProgress("正在查询...");

        Observable<DataObject<User>> observable1 = NetworkRequest.create().setUrl("http://192.168.1.103:8080/webService/userInfo/getAllUserInfoLayer.action")
                .setMethod(Service.GET_TYPE)
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .request()
                .getObservable();

        Observable<DataObject<User>> observable2 = NetworkRequest.create().setUrl("http://192.168.1.103:8080/webService/userInfo/getAllUserInfo.action")
                .setMethod(Service.GET_TYPE)
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .request()
                .getObservable();

        Observable<DataObject<User>> observable3 = NetworkRequest.create().setUrl("http://192.168.1.103:8080/webService/userInfo/getAllUserInfo.action")
                .setMethod(Service.GET_TYPE)
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .request()
                .getObservable();
        return Observable.zip(observable1, observable2, observable3, new Func3<DataObject<User>, DataObject<User>, DataObject<User>, Object>() {
            @Override
            public Object call(DataObject<User> userDataObject, DataObject<User> userDataObject2, DataObject<User> userDataObject3) {
                Log.i("WebService", "第一个请求："+ userDataObject.data.rows.get(0).toString());
                Log.i("WebService", "第二个请求："+ userDataObject2.data.rows.get(0).toString());
                Log.i("WebService", "第三个请求："+ userDataObject3.data.rows.get(0).toString());
                return null;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                Log.i("WebService", "请求结束");
                hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                Log.i("WebService", "请求错误："+ e.getMessage());
                hideProgress();
            }

            @Override
            public void onNext(Object o) {
                Log.i("WebService", "请求成功");
            }
        });
    }
}
