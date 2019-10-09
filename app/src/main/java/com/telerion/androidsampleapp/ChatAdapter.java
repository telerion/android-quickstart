package com.telerion.androidsampleapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.telerion.androidsdk.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    static final int VIEW_TYPE_MESSAGE_SENT = 1;
    static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    ChatFragment mFragment;
    List<ChatMessage> mMessageList = new ArrayList<>();

    public ChatAdapter(ChatFragment fragment) {
        mFragment = fragment;

        super.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (0 != positionStart) {
                    View view = mFragment.getView();
                    if (null != view) {
                        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_message_list);
                        if (null != recyclerView) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mMessageList.size());
                        }
                    }
                }
            }
        });
    }

    // Add history to the beggining of the list.
    public void onHistory(List<ChatMessage> messages) {
        for (ChatMessage message : messages) {
            this.mMessageList.add(0, message);
            super.notifyItemInserted(0);
        }
    }

    // New messages appended to the end
    public void onMessage(ChatMessage message) {
        this.mMessageList.add(message);
        super.notifyItemInserted(mMessageList.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = this.mMessageList.get(position);
        if (message.isIncoming())
            return VIEW_TYPE_MESSAGE_RECEIVED;
        else
            return VIEW_TYPE_MESSAGE_SENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (VIEW_TYPE_MESSAGE_SENT == viewType)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_chat_message, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_chat_message, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = this.mMessageList.get(position);
        ((MessageHolder) holder).bind(message);
    }

    // Message holder.
    private class MessageHolder extends RecyclerView.ViewHolder {
        TextView mMessageText;
        TextView mMessageDate;

        MessageHolder(View itemView) {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.text_message_body);
            mMessageDate = itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage message) {
            mMessageText.setText(message.getMessage());
            SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
            mMessageDate.setText(format.format(message.getDate()));
        }
    }
}
