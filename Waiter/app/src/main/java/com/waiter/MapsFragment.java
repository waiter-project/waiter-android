package com.waiter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.waiter.models.ErrorResponse;
import com.waiter.models.Event;
import com.waiter.models.ResponseEventsNearLocation;
import com.waiter.network.ServiceGenerator;
import com.waiter.network.WaiterClient;
import com.waiter.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener {

    private static final String TAG = "MapsFragment";

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 14;

    private boolean mPermissionDenied = false;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private OnFragmentInteractionListener mListener;

    private CoordinatorLayout mCoordinatorLayout;
    private View mBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;

    private View mRootView;
    private TextView mEventTitle;
    private TextView mEventPrice;
    private TextView mEventDescription;
    private TextView mEventAddress;
    private TextView mEventDate;
    private TextView mEventWaitersAvailable;
    private FloatingActionButton mFAB;
    private boolean showFAB = false;

    private Animation mGrowAnimation;
    private Animation mShrinkAnimation;

    private int eventPosition = -1;

    private WaiterClient waiterClient;
    private ErrorResponse errorResponse;

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRootView = inflater.inflate(R.layout.fragment_maps, container, false);

        waiterClient = ServiceGenerator.createService(WaiterClient.class);

        /*
        ** Start Init Maps
         */
        mMapView = (MapView) mRootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // End Init Maps


        /*
        ** Start Bottom Sheet
         */
        mCoordinatorLayout = (CoordinatorLayout) mRootView.findViewById(R.id.coordinator);
        initializeUI();
        // End BottomSheet

        return mRootView;
    }

    private void initializeUI() {
        mBottomSheet = mCoordinatorLayout.findViewById(R.id.event_bottom_sheet);
        mEventTitle = (TextView) mBottomSheet.findViewById(R.id.event_title);
        mEventPrice = (TextView) mBottomSheet.findViewById(R.id.event_price);
        mEventDescription = (TextView) mBottomSheet.findViewById(R.id.event_description);
        mEventAddress = (TextView) mBottomSheet.findViewById(R.id.event_address);
        mEventDate = (TextView) mBottomSheet.findViewById(R.id.event_date);
        mEventWaitersAvailable = (TextView) mBottomSheet.findViewById(R.id.event_waiters_available);
        mFAB = (FloatingActionButton) mCoordinatorLayout.findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMapsEventClicked(eventPosition);
            }
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mGrowAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.simple_grow);
        mShrinkAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.simple_shrink);

        mGrowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFAB.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mShrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFAB.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Not used.
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset >= -1 && slideOffset <= 0) {
                    mFAB.animate().scaleX(slideOffset + 1).scaleY(slideOffset + 1).setDuration(0).start();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        checkLocationPermission();

        mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.setOnCameraMoveStartedListener(this);
        mGoogleMap.setOnCameraMoveListener(this);
        mGoogleMap.setOnCameraMoveCanceledListener(this);

        if (Utils.isEmulator()) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
    }

    private void putMarkersOnMap() {
        mGoogleMap.clear();

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_resized);
//        LatLng latLng = new LatLng(48.8151239, 2.3631254); // Epitech Paris location
        for (int i = 0; i < MainActivity.mEventList.size(); i++) {
//            latLng = new LatLng(MainActivity.mEventList.get(i).getLong(), MainActivity.mEventList.get(i).getLat());
            LatLng latLng = new LatLng(MainActivity.mEventList.get(i).getLocation().get(1), MainActivity.mEventList.get(i).getLocation().get(0));
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(i)).icon(icon));
        }
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
//        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mFAB.getVisibility() == View.GONE) {
            mFAB.setScaleX(0);
            mFAB.setScaleY(0);
            mFAB.setVisibility(View.VISIBLE);
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        eventPosition = Integer.parseInt(marker.getTitle());

        mEventTitle.setText(MainActivity.mEventList.get(eventPosition).getName());
        //mEventPrice.setText();
        mEventDescription.setText(MainActivity.mEventList.get(eventPosition).getDescription());
        mEventAddress.setText(MainActivity.mEventList.get(eventPosition).getAddress());
        mEventDate.setText(MainActivity.mEventList.get(eventPosition).getDate());
        mEventWaitersAvailable.setText(getString(R.string.waiters_available, MainActivity.mEventList.get(eventPosition).getListOfWaiters().size()));

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        bottomSheetToPreviousState();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        } else {
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                } else {
                    Toast.makeText(getActivity(), "Location permission denied.", Toast.LENGTH_LONG).show();
                    mPermissionDenied = true;
                }
            }
        }
    }

    private boolean bottomSheetToPreviousState() {
        mBottomSheet.scrollTo(0, 0);
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mFAB.setVisibility(View.GONE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
//        if (mPermissionDenied) {
//            // Permission was not granted, display error dialog.
//            Toast.makeText(getActivity(), "Location permission denied.", Toast.LENGTH_LONG).show();
//            mPermissionDenied = false;
//        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    public boolean onBackPressed() {
        return bottomSheetToPreviousState();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteractionMaps();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setLastKnownLocation(boolean withAnimation) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        LatLng latLng = new LatLng(48.8151239, 2.3631254); // Epitech Paris location
        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            Toast.makeText(getContext(), "Get current location failed.", Toast.LENGTH_SHORT).show();
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(DEFAULT_ZOOM).build();
        if (withAnimation) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setLastKnownLocation(false);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Log.d(TAG, "onCameraMoveStarted: The user gestured on the map.");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            Log.d(TAG, "onCameraMoveStarted: The user tapped something on the map.");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            Log.d(TAG, "onCameraMoveStarted: The app moved the camera.");
        }
    }

    @Override
    public void onCameraMove() {
        Log.d(TAG, "onCameraMove: The camera is moving.");
    }

    @Override
    public void onCameraMoveCanceled() {
        Log.d(TAG, "onCameraMoveCanceled: Camera movement canceled.");
    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "onCameraIdle: The camera has stopped moving: " + mGoogleMap.getCameraPosition().toString());
//        Toast.makeText(getContext(), "onCameraIdle: The camera has stopped moving: " + mGoogleMap.getCameraPosition().toString(), Toast.LENGTH_LONG).show();
        if (mGoogleMap.getCameraPosition().target.latitude != 0 && mGoogleMap.getCameraPosition().target.longitude != 0) {
//            Toast.makeText(getContext(), mGoogleMap.getCameraPosition().target.toString(), Toast.LENGTH_LONG).show();
            loadEvents();
        }
    }

    private void loadEvents() {
        Call<ResponseEventsNearLocation> call = waiterClient.getEventsNearLocation(
                mGoogleMap.getCameraPosition().target.longitude,
                mGoogleMap.getCameraPosition().target.latitude,
                mGoogleMap.getCameraPosition().zoom);

        call.enqueue(new Callback<ResponseEventsNearLocation>() {
            @Override
            public void onResponse(@NonNull Call<ResponseEventsNearLocation> call, @NonNull Response<ResponseEventsNearLocation> response) {
                if (response.isSuccessful()) {
                    ResponseEventsNearLocation body = response.body();
                    if (body != null) {
                        MainActivity.mEventList = body.getData().getEvents();
                        putMarkersOnMap();
                        mListener.onFragmentInteractionMaps();
//                        Snackbar.make(((MainActivity)getActivity()).getViewPager(), getString(R.string.update_success, "Events"), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(((MainActivity)getActivity()).getViewPager(), getString(R.string.response_body_null), Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    errorResponse = ErrorUtils.parseError(response);
                    if (errorResponse != null && errorResponse.getData() != null) {
                        if (errorResponse.getData().getCauses() == null || errorResponse.getData().getCauses().isEmpty()) {
                            Snackbar.make(((MainActivity)getActivity()).getViewPager(), errorResponse.getData().getMessage(), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(((MainActivity)getActivity()).getViewPager(), errorResponse.getData().getCauses().get(0), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(((MainActivity)getActivity()).getViewPager(), getString(R.string.internal_error), Snackbar.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseEventsNearLocation> call, @NonNull Throwable t) {
                Snackbar.make(((MainActivity)getActivity()).getViewPager(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteractionMaps();
        void onMapsEventClicked(int eventPosition);
    }
}
