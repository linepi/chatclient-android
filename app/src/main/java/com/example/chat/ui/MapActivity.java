//package com.example.chat.ui;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.FrameLayout;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.baidu.mapapi.CoordType;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.SDKInitializer;
//
//import com.example.chat.R;
//
//public class MapActivity extends AppCompatActivity {
//
//    private MapView mapView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // 初始化百度地图 SDK
//        // 在使用百度地图 SDK 各组件之前初始化 context 信息，传入 ApplicationContext
//        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
//        SDKInitializer.initialize(getApplicationContext());
//        SDKInitializer.setCoordType(CoordType.BD09LL);
//        setContentView(R.layout.activity_map);
//        Log.e("tag","!!!!!!!!!!!!!!!");
//        Log.e("tag","!!!!!!!!!!!!!!!");
//        Log.e("tag","!!!!!!!!!!!!!!!");
//        // 在代码中创建 MapView
//        mapView = new MapView(this);
//        FrameLayout container = findViewById(R.id.map_container);
//        container.addView(mapView);
//
//        // 配置地图初始化参数
//        mapView.getMap().setMapType(BaiduMap.MAP_TYPE_NORMAL);
//        initLocationAndMark();
//    }
//
//    private void initLocationAndMark() {
//        // 位置服务和地图标记的逻辑
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//}