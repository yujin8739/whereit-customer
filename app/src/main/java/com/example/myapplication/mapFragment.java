package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.naver.maps.map.overlay.InfoWindow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.List;

public class mapFragment extends Fragment implements OnMapReadyCallback
{
    //지도 객체 변수
    private MapView mapView;
    private boardadapter mAdapter;
    private static NaverMap naverMap;
    private List<Board> mDatas;
    private String comment_p,board_t,board_num,comment_board;//
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String Latitude_A, Longitude_B;
    private double Latitude_c, Longitude_d;
    private List<Content> mcontent;
    private Marker marker = new Marker();
    private InfoWindow infoWindow1 = new InfoWindow();
    int com_pos = 0;
    private Object adapter;

    public mapFragment() { }

    public static mapFragment newInstance()
    {
        mapFragment fragment = new mapFragment();


        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map,
                container, false);

        
        Intent intent = getActivity().getIntent();
        Latitude_A=intent.getStringExtra("latitude");
        Longitude_B=intent.getStringExtra("longitude");
        Latitude_c = Double.parseDouble(Latitude_A);
        Longitude_d = Double.parseDouble(Longitude_B);
        mapView = (MapView) rootView.findViewById(R.id.navermap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }
    private void setMarker(Marker marker,  double lat, double lng, int resourceID, int zIndex)
    {
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);
        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(resourceID));
        //마커의 투명도
        marker.setAlpha(0.8f);
        //마커 위치
        marker.setPosition(new LatLng(lat, lng));
        //마커 우선순위
        marker.setZIndex(zIndex);
        //마커 표시
        marker.setMap(naverMap);
    }
    private void setInfoWindow(InfoWindow infoWindow,  double lat, double lng, int resourceID, int zIndex)
    {
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        //배경 지도 선택
        Intent intent = getActivity().getIntent();
        naverMap.setMapType(NaverMap.MapType.Navi);

        setMarker(marker, Latitude_c, Longitude_d, R.drawable.ic_baseline_pin_drop_24, 10);
        marker.setMap(naverMap);
        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                ViewGroup rootView = (ViewGroup)getView().findViewById(R.id.navermap);
                pointAdapter adapter = new pointAdapter(getActivity(), rootView);
                infoWindow1.setAdapter((InfoWindow.Adapter) adapter);

                //인포창의 우선순위
                infoWindow1.setZIndex(10);
                //투명도 조정
                infoWindow1.setAlpha(0.9f);
                //인포창 표시
                infoWindow1.open(marker);

                return false;
            }
        });
        //건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);

        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(Latitude_c, Longitude_d),   // 위치 지정
                17// 줌 레벨
                // 방향
        );
        naverMap.setCameraPosition(cameraPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart()
    {
        String addr;

        super.onStart();
        Log.d("확인","검색어:"+Latitude_A);
        Log.d("확인","검색어:"+Longitude_B);
        Log.d("확인","검색어:"+board_t);

/*
        mDatas = new ArrayList<>();//
        mStore.collection("Board")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .whereEqualTo("title",board_t)//게시판 제목중에 검색어와 똑같으면 검색
                .orderBy(Database.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {

                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mcontent.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        Map<String, Object> shot = snap.getData();
                                        Intent intent = getActivity().getIntent();
                                        String documentId = String.valueOf(shot.get(Database.documentId));
                                        String comment = String.valueOf(shot.get(Database.comment));
                                        String c_name = String.valueOf(shot.get(Database.name));
                                        String c_photo = String.valueOf(shot.get(Database.c_photo));
                                        String num_comment=String.valueOf(shot.get(Database.comment_board));
                                        String Latitude = String.valueOf(shot.get(Database.latitude));
                                        String Longitude = String.valueOf(shot.get(Database.longitude));
                                        Content data = new Content(documentId, c_name, comment,  Integer.toString(com_pos),board_t,c_photo,num_comment,Latitude, Longitude);
                                        mcontent.add(data);//여기까지가 게시글에 해당하는 데이터 적용
                                    }
                                }
                            }
                        });*/


    }
    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}