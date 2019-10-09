package com.telerion.androidsampleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.telerion.androidsdk.CompletionHandler;
import com.telerion.androidsdk.Session;
import com.telerion.androidsdk.TelerionError;

public class CallFragment extends Fragment implements View.OnClickListener {

    // Members.
    private Session mSession;
    private Boolean mAudioEnabled = true;
    private Boolean mSpeakerphoneEnabled = false;

    // Constructor.
    public CallFragment(Session session) {
        this.mSession = session;
    }


    // Lifecycle overrides.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.call_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.mSession.setLocalVideoView(getView().findViewById(R.id.local_renderer), new CompletionHandler<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onError(TelerionError e) {
                Log.e("CallFragment", "Unable to set local video view: " + e.toString());
            }
        });
        this.mSession.setRemoteVideoView(getView().findViewById(R.id.remote_renderer), new CompletionHandler<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onError(TelerionError e) {
                Log.e("CallFragment", "Unable to set remote video view: " + e.toString());
            }
        });

        getView().findViewById(R.id.call_switch_button).setOnClickListener(this);
        getView().findViewById(R.id.call_mute_button).setOnClickListener(this);
        getView().findViewById(R.id.call_speakerphone_button).setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.call_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call_action_hangup:
                mSession.disconnect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_switch_button:
                mSession.switchCamera(new CompletionHandler<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }

                    @Override
                    public void onError(TelerionError e) {
                        Log.e("CallFragment", "Unable to switch camera: " + e.toString());
                    }
                });
                break;
            case R.id.call_mute_button:
                mAudioEnabled = !mAudioEnabled;
                mSession.mute(this.mAudioEnabled, new CompletionHandler<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getView().findViewById(R.id.call_mute_button).setBackgroundResource(CallFragment.this.mAudioEnabled ? R.drawable.ic_mute_icon : R.drawable.ic_unmute_icon);
                    }

                    @Override
                    public void onError(TelerionError e) {
                        Log.e("CallFragment", "Unable to mute: " + e.toString());
                    }
                });
                break;
            case R.id.call_speakerphone_button:
                mSpeakerphoneEnabled = !mSpeakerphoneEnabled;
                mSession.setSpeakerphone(mSpeakerphoneEnabled, new CompletionHandler<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getView().findViewById(R.id.call_speakerphone_button).setBackgroundResource(CallFragment.this.mSpeakerphoneEnabled ? R.drawable.ic_speakerphone_off : R.drawable.ic_speakerphone_on);
                    }

                    @Override
                    public void onError(TelerionError e) {
                        Log.e("CallFragment", "Unable to set speakerphone: " + e.toString());
                    }
                });
                break;
        }
    }
}
