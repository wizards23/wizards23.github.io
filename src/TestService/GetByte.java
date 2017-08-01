package TestService;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class GetByte {
	public  void getbyte(byte[] b) throws Exception {
		
		String methodName = "getBytes";

		WebParameter[] parameter = { new WebParameter<byte[]>("arg0",
				b) 
		};
		SoapObject soapObject = WebService.getSoapObject(methodName, parameter);
		Log.i("123byte", "123byte");
	}
}
