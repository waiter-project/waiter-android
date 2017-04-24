package com.waiter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.waiter.models.Event;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private boolean mPermissionDenied = false;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private OnFragmentInteractionListener mListener;

    private List<Event> eventList;

    private BottomSheetBehavior mBottomSheetBehavior;

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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        /*
        ** Start Bottom Sheet
         */
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator);
        View bottomSheet = coordinatorLayout.findViewById(R.id.event_bottom_sheet);
        mEventTitle = (TextView) bottomSheet.findViewById(R.id.eventTitle);
        mEventPrice = (TextView) bottomSheet.findViewById(R.id.eventPrice);
        mEventDescription = (TextView) bottomSheet.findViewById(R.id.eventDescription);
        mEventAddress = (TextView) bottomSheet.findViewById(R.id.eventAddress);
        mEventDate = (TextView) bottomSheet.findViewById(R.id.eventDate);
        mEventWaitersAvailable = (TextView) bottomSheet.findViewById(R.id.eventWaitersAvailable);
        mFAB = (FloatingActionButton) bottomSheet.findViewById(R.id.fab);

        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked.", Toast.LENGTH_LONG).show();
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
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
//                Log.d("MapsFragment", "onStateChanged() | currentState = " + mBottomSheetBehavior.getState() + ", newState = " + newState);

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (showFAB) {
                            mFAB.startAnimation(mGrowAnimation);
                        } else {
                            mFAB.startAnimation(mShrinkAnimation);
                        }
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        showFAB = false;
//                        mFAB.setVisibility(View.VISIBLE);
//                        mFAB.startAnimation(mGrowAnimation);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        showFAB = true;
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                if (slideOffset > -1 && slideOffset <= 0) {
//                    Log.d("MapsFragment", "onSlide() | HIDDEN > COLLAPSED");
//                } else if (slideOffset > 0 && slideOffset <= 1) {
//                    Log.d("MapsFragment", "onSlide() | COLLAPSED > EXPANDED");
//                }
//                Log.d("MapsFragment", "onSlide() | slideOffset = " + slideOffset);
            }
        });
        // End BottomSheet

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        checkLocationPermission();
        mGoogleMap.setOnMyLocationButtonClickListener(this);

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        eventList = new ArrayList<>();
        List<String> listOfWaiters = new ArrayList<>();
        listOfWaiters.add("58fc51f131087c0011378ebe");
        eventList.add(new Event("58fc51e531087c0011378ebc",
                "Eiffel Tower",
                "A big piece of iron",
                "5 Avenue Anatole Paris France",
                48.8584,
                2.2945,
                "Everyday",
                1,
                listOfWaiters));

        LatLng latLng = new LatLng(48.8151239, 2.3631254); // Epitech Paris location
        for (int i = 0; i < eventList.size(); i++) {
            latLng = new LatLng(eventList.get(i).getLong(), eventList.get(i).getLat());
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(i)));
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mFAB.getVisibility() == View.GONE) {
            mFAB.setVisibility(View.VISIBLE);
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        int eventID = Integer.parseInt(marker.getTitle());

        mEventTitle.setText(eventList.get(eventID).getName());
//        mEventPrice.setText();
        mEventDescription.setText(eventList.get(eventID).getDescription());
        mEventAddress.setText(eventList.get(eventID).getAddress());
        mEventDate.setText(eventList.get(eventID).getDate());
        mEventWaitersAvailable.setText(getString(R.string.waiters_available, eventList.get(eventID).getListOfWaiters().size()));
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
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
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mFAB.startAnimation(mShrinkAnimation);
        } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mFAB.startAnimation(mGrowAnimation);
        } else {
            return false;
        }
        return true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionMaps(uri);
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
        // TODO: Update argument type and name
        void onFragmentInteractionMaps(Uri uri);
    }
}
