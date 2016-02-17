package com.waiter.waiterpoc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.waiter.waiterpoc.models.GenericResponseArray;
import com.waiter.waiterpoc.models.User;
import com.waiter.waiterpoc.network.ServiceGenerator;
import com.waiter.waiterpoc.network.WaiterService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DebuggingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DebuggingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebuggingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // UI references.
    private TextView mDebuggingText;
    private EditText mEmailText;
    private ProgressBar mProgressBar;

    public DebuggingFragment() {
        // Required empty public constructor
    }

    public static DebuggingFragment newInstance() {
        DebuggingFragment fragment = new DebuggingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_debugging, container, false);

        // Set up the debugging TextView
        mDebuggingText = (TextView) rootView.findViewById(R.id.debuggingText);

        mEmailText = (EditText) rootView.findViewById(R.id.emailText);
        mEmailText.setVisibility(View.INVISIBLE);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        Button queryButton = (Button) rootView.findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new RetrieveFeedTask().execute();

                String user = mEmailText.getText().toString();
                mProgressBar.setVisibility(View.VISIBLE);

                WaiterService service = ServiceGenerator.createService(WaiterService.class);

                Call<GenericResponseArray<User>> call = service.getUsersList();

                call.enqueue(new Callback<GenericResponseArray<User>>() {
                    @Override
                    public void onResponse(Call<GenericResponseArray<User>> call, Response<GenericResponseArray<User>> response) {
                        if (response.isSuccess()) {
                            mDebuggingText.setText("SUCCESS");
                            List<User> userList= response.body().getData();
                            mDebuggingText.append("\n\n");
                            for (User u : userList) {
                                mDebuggingText.append(u.getId() + "\n" + u.getFirstname() + " " + u.getLastname() + "\n" + u.getEmail() + " - Wallet: " + u.getWallet() + "\n\n");
                            }

                        } else {
                            mDebuggingText.setText("FAILURE");
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<GenericResponseArray<User>> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        mDebuggingText.setText("An unknown error happened.");
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionDebugging(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
            mListener.onFragmentInteractionDebugging(Uri.parse("doWhatYouWant"));
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteractionDebugging(Uri uri);
    }
}
