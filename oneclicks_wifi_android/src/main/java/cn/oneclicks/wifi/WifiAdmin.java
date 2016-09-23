package cn.oneclicks.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import java.util.List;
 
public class WifiAdmin {
 
    private static final String TAG = "[WifiAdmin]";
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList = null;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiLock mWifiLock;
    private DhcpInfo dhcpInfo;
 
    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }



    public boolean openWifi() {//打开wifi
        if (!mWifiManager.isWifiEnabled()) {
            Log.i(TAG, "setWifiEnabled.....");
            mWifiManager.setWifiEnabled(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.i(TAG, "setWifiEnabled.....end");
        }
        return mWifiManager.isWifiEnabled();
    }
 
    public void closeWifi() { if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
 
    public int checkState() {
        return mWifiManager.getWifiState();
    }
 
    public void acquireWifiLock() {//锁定wifiLock
        mWifiLock.acquire();
    }
 
    public void releaseWifiLock() {//解锁wifiLock
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }
 
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }
 
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }
 
    public void connectConfiguration(int index) {//指定配置好的网络进行连接
        if (index > mWifiConfiguration.size()) {
            return;
        }
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(TAG, "enableNetwork.....end");
    }
 
    public void startScan() {//wifi扫描
        boolean scan = mWifiManager.startScan();
        Log.i(TAG, "startScan result:" + scan);
        mWifiList = mWifiManager.getScanResults();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
 
        if (mWifiList != null) {
            Log.i(TAG, "startScan result:" + mWifiList.size());
            for (int i = 0; i < mWifiList.size(); i++) {
                ScanResult result = mWifiList.get(i);
                Log.i(TAG, "startScan result[" + i + "]" + result.SSID + "," + result.BSSID);
            }
            Log.i(TAG, "startScan result end.");
        } else {
            Log.i(TAG, "startScan result is null.");
        }
 
    }
 
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }
 
    public StringBuilder lookUpScan() {// 查看扫描结果   
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }
 
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }
 
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
 
    public DhcpInfo getDhcpInfo() {
        return dhcpInfo = mWifiManager.getDhcpInfo();
    }
 
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
 
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
 
    public WifiInfo getWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo;
    }
 
    public void addNetwork(WifiConfiguration wcg) { // 添加一个网络配置并连接  
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        System.out.println("addNetwork--" + wcgID);
        System.out.println("enableNetwork--" + b);
    }
 
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
 
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        Log.i(TAG, "SSID:" + SSID + ",password:" + Password);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
 
        WifiConfiguration tempConfig = this.IsExsits(SSID);
 
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        } else {
        	//mWifiManager.addNetwork(tempConfig);
            Log.i(TAG, "IsExsits is null.");
        }
 
        if (Type == 1) // WIFICIPHER_NOPASS
        {
            Log.i(TAG, "Type =1.");
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            Log.i(TAG, "Type =2.");
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
 
            Log.i(TAG, "Type =3.");
            config.preSharedKey = "\"" + Password + "\"";
 
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
 
    private WifiConfiguration IsExsits(String SSID) { // 查看以前是否已经配置过该SSID  
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }
    
    public void connect(String SSID)
    {
    	if(IsExsits(SSID)!=null)
    	{
    		WifiConfiguration result;
    		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
    		int i = 0;
    			if (existingConfigs != null) {           
	                for ( i = 0; i < existingConfigs.size(); i++) {
	                     result = existingConfigs.get(i);
	                     if(result.SSID.equalsIgnoreCase("\"" + SSID + "\""))
	                     {
	                    	 break;
	                     }
	                }
                this.connectConfiguration(i);
    			}
    	}
    	else
    	{
    		this.addNetwork(this.CreateWifiInfo(SSID, "", 1));
    	}
    		
    }
}
