package com.telerion.androidsampleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.telerion.androidsdk.ChatMessage;
import com.telerion.androidsdk.CompletionHandler;
import com.telerion.androidsdk.Session;
import com.telerion.androidsdk.TelerionError;

import java.util.List;

public class ChatFragment extends Fragment implements View.OnClickListener {

    // Members.
    Session mSession;
    ChatAdapter mChatAdapter = new ChatAdapter(this);

    // Constructor.
    public ChatFragment(Session session) {
        mSession = session;
    }

    public void addMessage(ChatMessage message) {
        mChatAdapter.onMessage(message);
    }

    // Lifecycle overrides.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show chat messages
        RecyclerView messageRecycler = getView().findViewById(R.id.recyclerview_message_list);
        messageRecycler.setHasFixedSize(true);
        messageRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageRecycler.setAdapter(mChatAdapter);

        // Refresher
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.chat_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mSession.getMessageHistory(10, new CompletionHandler<List<ChatMessage>>() {
                @Override
                public void onSuccess(List<ChatMessage> data) {
                    Log.w("ChatFragment", "History retrieved");
                    ChatFragment.this.mChatAdapter.onHistory(data);

                    // scroll if something received
                    if (data.size() > 0)
                        messageRecycler.scrollBy(0, -50);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(TelerionError error) {
                    Log.e("ChatFragment", "History error: " + error.getReason());
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        });

        // Send button
        getView().findViewById(R.id.chat_send).setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_action_hangup:
                mSession.disconnect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_send:
                mSession.sendMessage(((EditText) getView().findViewById(R.id.chat_input)).getText().toString(), new CompletionHandler<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Log.w("ChatFragment", "Message sent");
                    }

                    @Override
                    public void onError(TelerionError error) {
                        Log.e("ChatFragment", "Unable to send message: " + error.getReason());
                    }
                });
                ((EditText) getView().findViewById(R.id.chat_input)).setText("");
                break;
        }
    }
}
