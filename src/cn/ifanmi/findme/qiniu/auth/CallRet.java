package cn.ifanmi.findme.qiniu.auth;

import cn.ifanmi.findme.qiniu.utils.IOnProcess;
import cn.ifanmi.findme.qiniu.utils.QiniuException;

public abstract class CallRet implements IOnProcess {
	public void onInit(int flag){}
	public abstract void onSuccess(byte[] body);
	public abstract void onFailure(QiniuException ex);
	public void onProcess(long current, long total){}
	public void onPause(Object tag){}
}
