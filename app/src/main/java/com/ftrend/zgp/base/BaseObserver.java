package com.ftrend.zgp.base;

import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.HttpManager;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * 被观察者的封装
 * 笔记：如果不需要泛型，Observer后面是实体类那实现类不需要跟泛型
 *
 * @author liziqiang@ftrend.cn
 */
public class BaseObserver<T> implements Observer<BaseResponse<T>> {
    private HttpCallBack<T> mCallBack;

    public BaseObserver(HttpCallBack<T> callBack) {//HttpCallBack<T> callBack
        mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCallBack.onStart();
    }

    @Override
    public void onNext(BaseResponse<T> tBaseResponse) {
        //如果服务返回正常但是数据有错误，在返回的数据头上应该会有错误代码
        //onErrorMessage()
        mCallBack.onSuccess(tBaseResponse.getBody(), tBaseResponse.getHead());
    }

    @Override
    public void onError(Throwable e) {
        //此处需要对异常处理进行封装分类，如果是服务出现错误此处e应该有回调值
        //onThrowableMessage()
        mCallBack.onError(HttpManager.onThrowableMessage(e));
//    200 OK：客户端请求成功。
//
//　　400 Bad Request：客户端请求有语法错误，不能被服务器所理解。
//
//　　401 Unauthorized：请求未经授权，这个状态代码必须和WWW-Authenticate报头域一起使用。
//
//　　403 Forbidden：服务器收到请求，但是拒绝提供服务。
//
//　　404 Not Found：请求资源不存在，举个例子：输入了错误的URL。
//
//　　500 Internal Server Error：服务器发生不可预期的错误。
//
//　　503 Server Unavailable：服务器当前不能处理客户端的请求，一段时间后可能恢复正常，举个例子：HTTP/1.1 200 OK(CRLF)。
    }

    @Override
    public void onComplete() {
        mCallBack.onFinish();

    }
}
