package com.telerion.androidsampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.telerion.androidsdk.Core;
import com.telerion.androidsdk.CoreListener;
import com.telerion.androidsdk.ChatMessage;
import com.telerion.androidsdk.CompletionHandler;
import com.telerion.androidsdk.Session;
import com.telerion.androidsdk.SessionListener;
import com.telerion.androidsdk.SessionType;
import com.telerion.androidsdk.TelerionError;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SessionListener, CoreListener {

    // Members.
    private Core mCore;
    private HashMap<Session, ChatFragment> mChats = new HashMap<>();

    // Lifecycle overrides.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show login fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
    }

    // Login.
    public void login(String server, String id) {
        this.mCore = Core.create(getApplicationContext(), server);
        this.mCore.setListener(this);
        this.mCore.login(id);
    }

    // Create and start session.
    public void makeSession(String remoteNumber, String ownNumber, Boolean videoEnabled, SessionType type) {
        this.mCore.createSession(type, new CompletionHandler<Session>() {
            @Override
            public void onSuccess(Session session) {
                session.setVideoEnabled(videoEnabled);
                if (null != ownNumber)
                    session.setOwnNumber(ownNumber);
                session.setListener(MainActivity.this);
                session.connect(remoteNumber);
                if (SessionType.CALL == type)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CallFragment(session)).commit();
                else {
                    ChatFragment fragment = new ChatFragment(session);
                    MainActivity.this.mChats.put(session, fragment);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }

            @Override
            public void onError(TelerionError e) {
                Log.e("MainActivity", "Unable to create session: " + e);
            }
        });
    }

    // Logout
    public void logout() {
        this.mCore.logout();
    }

    // AppListener overrides
    @Override
    public void onLogin(Core core) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PhoneFragment()).commit();
    }

    @Override
    public void onLogout(Core core, TelerionError error) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        Toast.makeText(getApplicationContext(), "Core instance logged out with reason: " + error.getReason(),Toast.LENGTH_LONG).show();
    }

    // SessionListener overrides.
    @Override
    public void onMessage(Session session, ChatMessage message) {
        if (this.mChats.containsKey(session))
            this.mChats.get(session).addMessage(message);
    }

    @Override
    public void onConnect(Session session) {
    }

    @Override
    public void onDisconnect(Session session, TelerionError error) {
        this.mChats.remove(session);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PhoneFragment()).commit();
        Toast.makeText(getApplicationContext(), "Session ended with reason: " + error.getReason(),Toast.LENGTH_LONG).show();
    }
}
