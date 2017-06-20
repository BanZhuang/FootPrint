package com.Activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Entity.SetTripNavigationTrackListEntity;
import Entity.SetTripParticipantListEntity;
import Entity.SetTripWalkTrackListEntity;
import Entity.TripNavigationTrackEntity;
import Entity.TripParticipantEntity;
import Entity.TripWalkTrackEntity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BaiDumap.DrivingRouteOverlay;
import com.BaiDumap.WalkingRouteOverlay;
import com.Bll.MainBindService;
import com.Bll.MinaSocket;
import com.Lru.DiskLruCache;
import com.Lru.LruCacheUtils;
import com.Tool.ActionItem;
import com.Tool.IconManger;
import com.Tool.JSONtoDataSet;
import com.Tool.MyOrientationListener;
import com.Tool.TitlePopup;
import com.Tool.TitlePopup.OnItemOnClickListener;
import com.Tool.ToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class ActivityShowNavigationTripPlanMap extends Activity {

	private RelativeLayout focus_info, tripwalk_info;
	private LinearLayout myplace, alltrack, walkway, driveway, back, chatroom,
			writetrack, plandetail, sos, jujidian, participant;
	private ImageView imageview;
	private String imageurl;

	// �Զ�������
	private int tripplanid;
	private int tripid, Participantsid;
	private TitlePopup titlePopup;
	private List<ActionItem> actionlist = new ArrayList<ActionItem>();
	private List<TripParticipantEntity> palist = new ArrayList<TripParticipantEntity>();

	// ���ݸ��»�ȡ
	public static Handler mhander;
	private LruCacheUtils lruCache;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private SetTripNavigationTrackListEntity navigationtracklist;
	private SetTripWalkTrackListEntity walktracklist;

	// ��λ���
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private double mLatitude;
	private double mLongtitude;
	private boolean forstloc = true;
	private int three=0;

	// �Զ���ͼ��
	// private BitmapDescriptor mlogo;
	private MyOrientationListener myOrientationListener;
	private float mcurrentX;
	private LocationMode mlocationmode;

	// ���������
	private BitmapDescriptor mmarker;
	private List<Walklist> walklist = new ArrayList<Walklist>();
	private List<IconManger> iconlist = new ArrayList<IconManger>();
	private List<Marker> nmarklist = new ArrayList<Marker>();
	private List<Marker> wmarklist = new ArrayList<Marker>();
	

	// ·�߹滮
	private RoutePlanSearch mSearch;
	private DrivingRouteOverlay Doverlay = null;
	private WalkingRouteOverlay Woverlay = null;
	private double Latitude;
	private double Longtitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_shownavigationtripplan_map);
		
		lruCache = LruCacheUtils.getInstance();
		iconlist.add(new IconManger(true, R.drawable.icon_redmarker));
		iconlist.add(new IconManger(true, R.drawable.icon_bluemarker));
		iconlist.add(new IconManger(true, R.drawable.icon_greenmarker));
		iconlist.add(new IconManger(true, R.drawable.icon_purplemarker));
		iconlist.add(new IconManger(true, R.drawable.icon_yellowmarker));
		iconlist.add(new IconManger(true, R.drawable.icon_brownmarker));
		iconlist.add(new IconManger(true, R.drawable.icon_cyanmarker));
		// iconlist.add(new IconManger(true, R.drawable.icon_graymarker));
		iconlist.add(new IconManger(true, R.drawable.icon_orangemarker));
		iconlist.add(new IconManger(true, R.drawable.icon_pinkmarker));

		Intent intent = getIntent();
		tripplanid = intent.getIntExtra("tripplanid", -1);
		tripid = intent.getIntExtra("tripid", -1);
		Participantsid = intent.getIntExtra("Participantsid", -1);
		palist = ((SetTripParticipantListEntity) MainBindService.sParticipantsList
				.getData()).getTripParticipant();
		Toaction(palist);

		try {
			MinaSocket.SendMessage(new TripNavigationTrackEntity().ToJSON(16,
					tripplanid));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateUI();

		initView();
		setlisten();
		initLocation();

		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(listener);

	}

	private void Toaction(List<TripParticipantEntity> list) {
		for (int i = 0; i < list.size(); i++) {
			int x = 0;
			for (IconManger icon : iconlist) {
				x++;
				if (icon.isFlag()) {
					Walkmarker marker = new Walkmarker(icon.getImageid());
					icon.setFlag(false);
					List<TripWalkTrackEntity> l = new ArrayList<TripWalkTrackEntity>();
					walklist.add(new Walklist(list.get(i)
							.getTripParticipantId(), marker, l));
					actionlist.add(new ActionItem(this, list.get(i)
							.getUserNickname(), marker.getImageid(), list
							.get(i).getTripParticipantId()));
					break;

				} else if (iconlist.size() == x) {
					Walkmarker marker = new Walkmarker();
					List<TripWalkTrackEntity> l = new ArrayList<TripWalkTrackEntity>();
					walklist.add(new Walklist(list.get(i)
							.getTripParticipantId(), marker, l));
					actionlist.add(new ActionItem(this, list.get(i)
							.getUserNickname(), -1, list.get(i)
							.getTripParticipantId()));
				}
			}
		}
	}

	private void updateUI() {
		mhander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 0) {// ���λ�ȡ����
					if (MainBindService.sNavigationWalkTrack.isFlag()) {  //�����켣����Ϣ
						navigationtracklist = (SetTripNavigationTrackListEntity) MainBindService.sNavigationWalkTrack
								.getData();
						walktracklist = (SetTripWalkTrackListEntity) MainBindService.sNavigationWalkTrack
								.getData1();
						initNavigationMarker();

						// ���߹켣������
						for (TripWalkTrackEntity info : walktracklist
								.getTripWalkTrackList()) {
							int z = 0;
							for (Walklist item : walklist) {
								z++;
								if (item.getTripParticipantId() == info
										.getTripParticipantId()) {
									item.list.add(info);
									if (item.marker.getImageid() != -1) {
										item.getMarker()
												.initWalkMarker(
														info,
														ActivityShowNavigationTripPlanMap.this);
									} else {
										int x = 0;
										for (IconManger icon : iconlist) {
											x++;
											if (icon.isFlag()) {
												item.marker.setImageid(icon
														.getImageid());
												icon.setFlag(false);
												item.getMarker()
														.initWalkMarker(
																info,
																ActivityShowNavigationTripPlanMap.this);
												break;
											} else if (iconlist.size() == x) {
												ToastUtil
														.show(ActivityShowNavigationTripPlanMap.this,
																"��ʾ��Ŀ�����");
											}
										}
									}
									break;
								} else if (walklist.size() == z) {// ��ǰwalklist��û�иò�����
									addwalklistitem(info);
								}
							}
						}

					} else {
						ToastUtil.show(ActivityShowNavigationTripPlanMap.this,
								"���ݻ�ȡ�쳣");
					}

				} else if (msg.what == 1) {
					JSONtoDataSet data = (JSONtoDataSet) msg.obj;
					if (data.isFlag()) {
						TripWalkTrackEntity walktrack = (TripWalkTrackEntity) data
								.getData();

						int y = 0;
						for (Walklist item : walklist) {
							y++;
							if (item.getTripParticipantId() == walktrack
									.getTripParticipantId()) {
								item.list.add(walktrack);
								if (item.marker.getImageid() != -1) {
									item.getMarker()
											.initWalkMarker(
													walktrack,
													ActivityShowNavigationTripPlanMap.this);
								} else {
									int x = 0;
									for (IconManger icon : iconlist) {
										x++;
										if (icon.isFlag()) {
											item.marker.setImageid(icon
													.getImageid());
											icon.setFlag(false);
											item.getMarker()
													.initWalkMarker(
															walktrack,
															ActivityShowNavigationTripPlanMap.this);
											break;
										} else if (iconlist.size() == x) {
											ToastUtil
													.show(ActivityShowNavigationTripPlanMap.this,
															"��ʾ��Ŀ�����");
										}
									}
								}
								break;
							} else if (walklist.size() == y) {// ��ǰwalklist��û�иò�����
								addwalklistitem(walktrack);
							}
						}
					} else {
						ToastUtil.show(ActivityShowNavigationTripPlanMap.this,
								"���ݻ�ȡ�쳣");
					}
				}else if(msg.what == 2){
					Bitmap bitmap=(Bitmap) msg.obj;
					
					String url=imageurl;
					String key = lruCache.hashKeyForDisk(url);
	                DiskLruCache.Editor editor = null;
	               imageview.setImageBitmap(bitmap);
					lruCache.addBitmapToCache(url,bitmap);
					try {
						editor = lruCache.diskLruCache.edit(key);
						//λͼѹ���������������ѹ����ʽ������(100��ʾ��ѹ����30��ʾѹ��70%)���������
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, editor.newOutputStream(0));
                        editor.commit();//�ύ
					} catch (IOException e) {
						 try {
		                        editor.abort();//����д��
		                    } catch (IOException e1) {
		                        e1.printStackTrace();
		                    }
					}
				}
			}
		};
	}

	private void addwalklistitem(TripWalkTrackEntity info) {
		int x = 0;
		for (IconManger icon : iconlist) {
			x++;
			if (icon.isFlag()) {
				Walkmarker marker = new Walkmarker(icon.getImageid());
				icon.setFlag(false);
				List<TripWalkTrackEntity> list = new ArrayList<TripWalkTrackEntity>();
				list.add(info);
				marker.initWalkMarker(info,
						ActivityShowNavigationTripPlanMap.this);
				walklist.add(new Walklist(info.getTripParticipantId(), marker,
						list));
				break;
			} else if (iconlist.size() == x) {
				ToastUtil.show(ActivityShowNavigationTripPlanMap.this,
						"��ʾ��Ŀ�����");
				List<TripWalkTrackEntity> list = new ArrayList<TripWalkTrackEntity>();
				list.add(info);
				walklist.add(new Walklist(info.getTripParticipantId(),
						new Walkmarker(), list));
			}
		}
	}

	private void setlisten() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		chatroom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						ActivityShowNavigationTripPlanMap.this,
						ActivityChatroom.class);
				intent.putExtra("tripplanid", tripplanid);
				intent.putExtra("tripid", tripid);
				startActivity(intent);
			}
		});
		writetrack.setOnClickListener(new OnClickListener() { 

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						ActivityShowNavigationTripPlanMap.this,
						ActivityWriteWalkTrack.class);
				intent.putExtra("tripplanid", tripplanid);
				intent.putExtra("tripid", tripid);
				intent.putExtra("lon", mLongtitude);
				intent.putExtra("lat", mLatitude);
				intent.putExtra("Participantsid", Participantsid);
				startActivityForResult(intent, 1);
			}
		});
		participant.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				titlePopup.show(participant);
			}
		});
		plandetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						ActivityShowNavigationTripPlanMap.this,
						ActivityNavigationTripPlanDetail.class);
				intent.putExtra("tripplanid", tripplanid);
				intent.putExtra("tripid", tripid);
				intent.putExtra("Participantsid", Participantsid);
				startActivity(intent);
			}
		});

		myplace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LatLng latlng = new LatLng(mLatitude, mLongtitude);
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
				MapStatusUpdate msu1 = MapStatusUpdateFactory.zoomTo(15.0f);
				mBaiduMap.setMapStatus(msu1);
				mBaiduMap.animateMapStatus(msu);
			}
		});
		alltrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (Marker overlay : nmarklist) {
					builder.include((overlay).getPosition());
				}
				for (Marker overlay : wmarklist) {
					builder.include((overlay).getPosition());
				}
				mBaiduMap.setMapStatus(MapStatusUpdateFactory
						.newLatLngBounds(builder.build()));

			}
		});
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker maiker) {
				Bundle extrainfo = maiker.getExtraInfo();
				if (!extrainfo.isEmpty()) {
					if (extrainfo.getBoolean("flag")) {
						final TripNavigationTrackEntity info = (TripNavigationTrackEntity) extrainfo
								.getSerializable("info");
						TextView foucstitle, foucsbrief, foucscity, foucscreateman;
						
						foucstitle = (TextView) focus_info
								.findViewById(R.id.focus_title);
						foucsbrief = (TextView) focus_info
								.findViewById(R.id.focus_abstract);
						foucscity = (TextView) focus_info
								.findViewById(R.id.focus_address);
						foucscreateman = (TextView) focus_info
								.findViewById(R.id.focus_username);
						imageview=(ImageView) focus_info
								.findViewById(R.id.focus_image);
						imageurl=info.getFocusImage();
						if(!imageurl.equals("")){
							lruCache.loadBitmap(imageview,info.getFocusImage(),13);
						}
						foucstitle.setText(info.getFocusName());
						foucsbrief.setText(info.getFocusSummsry());
						foucscity.setText(info.getFocusCity());
						foucscreateman.setText(info.getFocusCreateMan());

						tripwalk_info.setVisibility(View.GONE);
						focus_info.setVisibility(View.VISIBLE);
						walkway.setVisibility(View.VISIBLE);
						driveway.setVisibility(View.VISIBLE);
						Latitude = info.getTripNavigationTracklat();
						Longtitude = info.getTripNavigationTracklon();
						focus_info.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(
										ActivityShowNavigationTripPlanMap.this,
										ActivityFoucsDetail.class);
								intent.putExtra("foucsid", info.getFocusId());
								startActivity(intent);
							}
						});

						ToastUtil.show(ActivityShowNavigationTripPlanMap.this,
								info.getFocusId() + "");
					} else {
						final TripWalkTrackEntity info = (TripWalkTrackEntity) extrainfo
								.getSerializable("info");
						// TextView title=(TextView)
						// tripwalk_info.findViewById(R.id.tripwalk_info_title);
						TextView abstracts = (TextView) tripwalk_info
								.findViewById(R.id.tripwalk_info_abstract);
						TextView time = (TextView) tripwalk_info
								.findViewById(R.id.tripwalk_info_time);
						TextView user = (TextView) tripwalk_info
								.findViewById(R.id.tripwalk_info_username);
						// title.setText(info.getTripWalkTrackName());
						abstracts.setText(info.getTripWalkTrackContent());
						time.setText(info.getTripWalkTrackTime());
						user.setText(info.getUsername());
						focus_info.setVisibility(View.GONE);
						tripwalk_info.setVisibility(View.VISIBLE);
						walkway.setVisibility(View.GONE);
						driveway.setVisibility(View.GONE);
						tripwalk_info.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(
										ActivityShowNavigationTripPlanMap.this,
										ActivityWalkTrackDetail.class);
								intent.putExtra("detail", info);
								startActivity(intent);

							}
						});

					}
				}
				return true;
			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				focus_info.setVisibility(View.GONE);
				tripwalk_info.setVisibility(View.GONE);
				walkway.setVisibility(View.GONE);
				driveway.setVisibility(View.GONE);
			}
		});

		driveway.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Woverlay != null) {
					Woverlay.removeFromMap();
				}
				PlanNode stNode = PlanNode.withLocation(new LatLng(mLatitude,
						mLongtitude));
				PlanNode enNode = PlanNode.withLocation(new LatLng(Latitude,
						Longtitude));
				mSearch.drivingSearch((new DrivingRoutePlanOption()).from(
						stNode).to(enNode));
			}
		});

		walkway.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Doverlay != null) {
					Doverlay.removeFromMap();
				}
				PlanNode stNode = PlanNode.withLocation(new LatLng(mLatitude,
						mLongtitude));
				PlanNode enNode = PlanNode.withLocation(new LatLng(Latitude,
						Longtitude));
				mSearch.walkingSearch((new WalkingRoutePlanOption()).from(
						stNode).to(enNode));
			}
		});

		titlePopup.setItemOnClickListener(new OnItemOnClickListener() {

			@Override
			public void onItemClick(ActionItem item, int position) {

				int imageid = titlePopup.getAction(position).mDrawable;
				
				int Participantsid = titlePopup.getAction(position)
						.getParticipantsid();
				if (imageid == R.drawable.icon_graymarker) {
					// ����ͼƬ
					for (IconManger icon : iconlist) {
						if (icon.isFlag()) {
							titlePopup.getAction(position).mDrawable = icon
									.getImageid();
							titlePopup.changemIsDirty();
							icon.setFlag(false);
							break;
						}
					}
					for (Walklist entity : walklist) {
						if (Participantsid == entity.getTripParticipantId()) {
							List<TripWalkTrackEntity> list = entity.getList();
							entity.getMarker().setI(0);
							for (TripWalkTrackEntity walktrack : list) {
								entity.getMarker().initWalkMarker(walktrack,
										ActivityShowNavigationTripPlanMap.this);
							}
							break;
						}
					}
				} else {
					// ����ͼƬ
					for (IconManger icon : iconlist) {
						if (imageid == icon.getImageid()) {
							icon.setFlag(true);
							titlePopup.getAction(position).mDrawable = R.drawable.icon_graymarker;
							titlePopup.changemIsDirty();
							break;
						}
					}
					// ɾ�����Ӧ���߸�����
					for (Marker marker : wmarklist) {
						TripWalkTrackEntity info = (TripWalkTrackEntity) marker
								.getExtraInfo().getSerializable("info");
						if (Participantsid == info.getTripParticipantId()) {
							marker.remove();
						}
					}
				}

			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		focus_info = (RelativeLayout) findViewById(R.id.focus_info);
		tripwalk_info = (RelativeLayout) findViewById(R.id.tripwalk_info);
		
		myplace = (LinearLayout) findViewById(R.id.myplace);//�ҵ�λ��
		alltrack = (LinearLayout) findViewById(R.id.alltrack);//���й켣
		walkway = (LinearLayout) findViewById(R.id.walktohere);//����
		driveway = (LinearLayout) findViewById(R.id.drivetohere);//�Լ�
		back = (LinearLayout) findViewById(R.id.back_image);
		chatroom = (LinearLayout) findViewById(R.id.chatroom); //������
		writetrack = (LinearLayout) findViewById(R.id.writetrack); //���
		plandetail = (LinearLayout) findViewById(R.id.plandetail);//��������
		sos = (LinearLayout) findViewById(R.id.sos); //sos
		jujidian = (LinearLayout) findViewById(R.id.jujidian);//�ۼ���
		participant = (LinearLayout) findViewById(R.id.participant);//�л��û���ͼ��
		// function=(TextView) findViewById(R.id.function_text);

		// ʵ��������������
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		// �������������������
		titlePopup.addActionList(actionlist);

		mMapView = (MapView) findViewById(R.id.mapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
	}

	private void initLocation() {

		mlocationmode = LocationMode.NORMAL;
		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setIsNeedAddress(true);
		option.setOpenGps(true);// ��GPS
		option.setScanSpan(1000);// ���÷���λ����ļ��ʱ��Ϊ1000ms
		option.setPriority(LocationClientOption.GpsFirst);// gps��λ����
		option.disableCache(false);// ��ֹ���û��涨λ
		mLocationClient.setLocOption(option);
		// //��ʼ��ͼ��
		// LayoutInflater inflate=getLayoutInflater();
		// View a=inflate.inflate(R.layout.location_logo, null);
		// mlogo=BitmapDescriptorFactory.fromView(a);

		myOrientationListener = new MyOrientationListener(this);
		myOrientationListener
				.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

					@Override
					public void onOrientationChanged(float x) {
						// TODO Auto-generated method stub
						mcurrentX = x;
					}
				});
	}

	private void initNavigationMarker() {
		//mmarker = BitmapDescriptorFactory.fromResource(R.drawable.rednn);
		LayoutInflater flater = LayoutInflater.from(ActivityShowNavigationTripPlanMap.this);
		View view = flater.inflate(R.layout.marker_nloge, null);
		TextView num = (TextView) view.findViewById(R.id.number);
		ImageView image = (ImageView) view.findViewById(R.id.image);
		// mBaiduMap.clear();
		LatLng latlng = null;
		Marker marker = null;
		OverlayOptions opinions;
		int i=0;
//		image.setImageResource(R.drawable.rednn);
//		mmarker = BitmapDescriptorFactory.fromView(view);
		
		for (TripNavigationTrackEntity info : navigationtracklist
				.getTripNavigationTrackList()) {
			i++;
			num.setText(i+ "");
			mmarker = BitmapDescriptorFactory.fromView(view);
			// ��γ��
			latlng = new LatLng(info.getTripNavigationTracklat(),
					info.getTripNavigationTracklon());
			// ͼ��
			opinions = new MarkerOptions().position(latlng).icon(mmarker)
					.zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(opinions);

			Bundle arg0 = new Bundle();
			arg0.putBoolean("flag", true);
			arg0.putSerializable("info", info);
			marker.setExtraInfo(arg0);
			nmarklist.add(marker);
		}
		// MapStatusUpdate mus=MapStatusUpdateFactory.newLatLng(latlng);
		// MapStatusUpdate msu1=MapStatusUpdateFactory.zoomTo(15.0f);
		// mBaiduMap.setMapStatus(msu1);
		// mBaiduMap.setMapStatus(mus);
	}

	class Walkmarker {
		private int i = 0;
		private int imageid = -1;

		public Walkmarker() {
		}

		public Walkmarker(int imageid) {
			this.imageid = imageid;
		}

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}

		public int getImageid() {
			return imageid;
		}

		public void setImageid(int imageid) {
			this.imageid = imageid;
		}

		/**
		 * @param info
		 * @param context
		 */
		private void initWalkMarker(TripWalkTrackEntity info, Context context) {
			// mmarker=BitmapDescriptorFactory.fromResource(R.drawable.icon_redmarker);
			LayoutInflater flater = LayoutInflater.from(context);
			View view = flater.inflate(R.layout.marker_logo, null);
			TextView num = (TextView) view.findViewById(R.id.number);
			ImageView image = (ImageView) view.findViewById(R.id.image);
			// mBaiduMap.clear();
			LatLng latlng = null;
			Marker marker = null;
			OverlayOptions opinions;

			// for(TripWalkTrackEntity
			// info:walktracklist.getTripWalkTrackList()){
			i++;
			num.setText(i + "");
			image.setImageResource(imageid);
			mmarker = BitmapDescriptorFactory.fromView(view);
			// ��γ��
			latlng = new LatLng(info.getTripWalkTrackLat(),
					info.getTripWalkTrackLon());
			// ͼ��
			opinions = new MarkerOptions().position(latlng).icon(mmarker)
					.zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(opinions);

			Bundle arg0 = new Bundle();
			arg0.putBoolean("flag", false);
			arg0.putSerializable("info", info);
			marker.setExtraInfo(arg0);
			wmarklist.add(marker);
			// }
			// MapStatusUpdate mus=MapStatusUpdateFactory.newLatLng(latlng);
			// mBaiduMap.setMapStatus(mus);
		}

	}

	public class Walklist {

		private int tripParticipantId;
		private Walkmarker marker;
		private List<TripWalkTrackEntity> list = new ArrayList<TripWalkTrackEntity>();

		public Walklist(int tripParticipantId, Walkmarker marker,
				List<TripWalkTrackEntity> list) {
			this.tripParticipantId = tripParticipantId;
			this.marker = marker;
			this.list = list;
		}

		public int getTripParticipantId() {
			return tripParticipantId;
		}

		public void setTripParticipantId(int userid) {
			this.tripParticipantId = userid;
		}

		public Walkmarker getMarker() {
			return marker;
		}

		public void setMarker(Walkmarker marker) {
			this.marker = marker;
		}

		public List<TripWalkTrackEntity> getList() {
			return list;
		}

		public void setList(List<TripWalkTrackEntity> list) {
			this.list = list;
		}

	}

	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			three++;

			MyLocationData data = new MyLocationData.Builder()
					.direction(mcurrentX).accuracy(location.getRadius())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();

			mBaiduMap.setMyLocationData(data);
			// �����Զ���ͼ��
			MyLocationConfiguration config = new MyLocationConfiguration(
					mlocationmode, true, null);
			mBaiduMap.setMyLocationConfigeration(config);

			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();
			if (forstloc) {
				LatLng latlng = new LatLng(mLatitude, mLongtitude);
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
				MapStatusUpdate msu1 = MapStatusUpdateFactory.zoomTo(15.0f);
				mBaiduMap.setMapStatus(msu1);
				mBaiduMap.animateMapStatus(msu);
				forstloc = false;
			}
			if(three>2){
			three=0;
			int i=0;
			for(Marker marker:nmarklist){
				TripNavigationTrackEntity info=(TripNavigationTrackEntity) marker.getExtraInfo().getSerializable("info");
				double Long=info.getTripNavigationTracklon();
				double Lat=info.getTripNavigationTracklat();
				double dis=Distance(mLongtitude, mLatitude, Long, Lat);
				i++;
				if(dis<100){
					JSONObject json=new JSONObject();
//					try {
//						json.put("tag", 46);
//						json.put("foucsId", 46);
//						json.put("userId", user);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					LayoutInflater flater = LayoutInflater.from(ActivityShowNavigationTripPlanMap.this);
					View view = flater.inflate(R.layout.marker_nloge, null);
					TextView num = (TextView) view.findViewById(R.id.number);
					ImageView image = (ImageView) view.findViewById(R.id.image);
					
					num.setText(i + "");
					image.setImageResource(R.drawable.greennn);
					mmarker = BitmapDescriptorFactory.fromView(view);
					marker.setIcon(mmarker);
				}
			}
			}

		}

	}
	
	public  double Distance(double long1, double lat1, double long2,  
	        double lat2) {  
	    double a, b, R;  
	    R = 6378137; // ����뾶  
	    lat1 = lat1 * Math.PI / 180.0;  
	    lat2 = lat2 * Math.PI / 180.0;  
	    a = lat1 - lat2;  
	    b = (long1 - long2) * Math.PI / 180.0;  
	    double d;  
	    double sa2, sb2;  
	    sa2 = Math.sin(a / 2.0);  
	    sb2 = Math.sin(b / 2.0);  
	    d = 2  
	            * R  
	            * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)  
	                    * Math.cos(lat2) * sb2 * sb2));  
	    return d;  
	}

	OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

		@Override
		public void onGetBikingRouteResult(BikingRouteResult result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			// TODO Auto-generated method stub
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(ActivityShowNavigationTripPlanMap.this,
						"��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();

			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				Doverlay = new DrivingRouteOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(Doverlay);
				Doverlay.setData(result.getRouteLines().get(0));
				Doverlay.addToMap();
				Doverlay.zoomToSpan();
			}
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			// TODO Auto-generated method stub
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(ActivityShowNavigationTripPlanMap.this,
						"��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();

			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				Woverlay = new WalkingRouteOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(Woverlay);
				Woverlay.setData(result.getRouteLines().get(0));
				Woverlay.addToMap();
				Woverlay.zoomToSpan();
			}
		}

		@Override
		public void onGetIndoorRouteResult(IndoorRouteResult arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetMassTransitRouteResult(MassTransitRouteResult arg0) {
			// TODO Auto-generated method stub
			
		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				TripWalkTrackEntity walktrackentity = (TripWalkTrackEntity) data
						.getSerializableExtra("return");
				int y = 0;
				for (Walklist item : walklist) {
					y++;
					if (item.getTripParticipantId() == walktrackentity
							.getTripParticipantId()) {
						item.marker.initWalkMarker(walktrackentity,
								ActivityShowNavigationTripPlanMap.this);
						break;
					} else if (walklist.size() == y) {
						int x = 0;
						for (IconManger icon : iconlist) {
							x++;
							if (icon.isFlag()) {
								Walkmarker marker = new Walkmarker(
										icon.getImageid());
								icon.setFlag(false);
								List<TripWalkTrackEntity> list = new ArrayList<TripWalkTrackEntity>();
								list.add(walktrackentity);
								marker.initWalkMarker(walktrackentity,
										ActivityShowNavigationTripPlanMap.this);
								walklist.add(new Walklist(walktrackentity
										.getTripParticipantId(), marker, list));
								break;
							} else if (iconlist.size() == x) {
								ToastUtil.show(
										ActivityShowNavigationTripPlanMap.this,
										"��ʾ��Ŀ�����");
								List<TripWalkTrackEntity> list = new ArrayList<TripWalkTrackEntity>();
								list.add(walktrackentity);
								walklist.add(new Walklist(walktrackentity
										.getTripParticipantId(),
										new Walkmarker(), list));
							}
						}

					}
				}
			}
			break;

		default:
			break;
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// ·�߹滮�ر�
		mSearch.destroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// ������λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// ��������
		myOrientationListener.start();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// �رն�λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// ֹͣ���򴫸���
		myOrientationListener.stop();

	}
}