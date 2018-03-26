package dingran.curltest.test;

public class JniTest {

	static{
		System.loadLibrary("jni_curl");
	}
	
	native public String curlInit(String host);
}
