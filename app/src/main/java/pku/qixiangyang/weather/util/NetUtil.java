package pku.qixiangyang.weather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 12541 on 2018/10/25.
 */

public class NetUtil {

    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;

    /*
    Context:
    它描述的是一个应用程序环境的信息，即上下文
    该类是一个抽象(abstract class)类，Android提供了该抽象类的具体实现类(后面我们会讲到是ContextIml类)
    通过它我们可以获取应用程序的资源和类，也包括一些应用级别操作，例如：启动一个Activity，发送广播，接受Intent信息等
     */
    //为什么getNetworkState函数是要static?
    public static int getNetworkState(Context context){
        //首先通过网络连接管理者获取管理对象
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取当前网络的额外信息
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //之后我们就可以进行更多操作或者是进行网络状态的判断
        if(networkInfo==null){
            return NETWORK_NONE;
        }
        //getType()获取网络类型
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_WIFI) {
            return NETWORK_WIFI;
        }else if(nType==ConnectivityManager.TYPE_MOBILE){
            return NETWORK_MOBILE;
        }
        return NETWORK_NONE;
    }
}
