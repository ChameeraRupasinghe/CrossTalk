package cse15.chameera.crosstalk;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mChatList;
    public static final int SENDER = 0;
    public static final int RECEIVER = 1;

    public int getItemViewType(int position) {
        if (mChatList.get(position).getSenderOrReciver() == SENDER) {
            return SENDER;
        } else {
            return RECEIVER;
        }

    }


    public MessageChatAdapter(ArrayList<ChatMessage> chatMessages) {
        mChatList = chatMessages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SENDER:
                View viewSender = inflater.inflate(R.layout.layout_sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSender);
                break;

            case RECEIVER:
                View viewReceiver = inflater.inflate(R.layout.layout_receiver_message, viewGroup, false);
                viewHolder = new ViewHolderReceiver(viewReceiver);
                break;

            default:
                View viewSenderDefault = inflater.inflate(R.layout.layout_sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSenderDefault);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case SENDER:
                ViewHolderSender viewHolderSender = (ViewHolderSender) holder;
                configureSenderView(viewHolderSender, position);
                break;

            case RECEIVER:
                ViewHolderReceiver viewHolderReceiver = (ViewHolderReceiver) holder;
                configureReceiverView(viewHolderReceiver, position);
                break;

        }

    }

    private void configureReceiverView(ViewHolderReceiver viewHolderReceiver, int position) {
        ChatMessage receiverMessage = mChatList.get(position);
        viewHolderReceiver.getmSenderMessageTextView().setText(receiverMessage.getMessage());
    }

    private void configureSenderView(ViewHolderSender viewHolderSender, int position) {
        ChatMessage senderMessage = mChatList.get(position);
        viewHolderSender.getmSenderMessageTextView().setText(senderMessage.getMessage());
    }

    @Override
    public int getItemCount() {
        if(mChatList != null)
            return mChatList.size();
        else
            return 0;
    }

    public void refillAdapter(ChatMessage newMessage) {

        mChatList.add(newMessage);

        notifyItemInserted(getItemCount() - 1);
    }

    public void cleanUp() {
        mChatList.clear();
    }


    //View Holder Inner Class for Sender
    private class ViewHolderSender extends RecyclerView.ViewHolder {

        private TextView mSenderMessageTextView;

        public ViewHolderSender(View viewSender) {
            super(viewSender);
            mSenderMessageTextView = (TextView) itemView.findViewById(R.id.text_view_sender_message);
        }

        public TextView getmSenderMessageTextView() {
            return mSenderMessageTextView;
        }
    }

    //View Holder Inner Class for Receiver
    private class ViewHolderReceiver extends RecyclerView.ViewHolder {

        private TextView mReceiverMessageTextView;

        public ViewHolderReceiver(View viewSender) {
            super(viewSender);
            mReceiverMessageTextView = (TextView) itemView.findViewById(R.id.text_view_recipient_message);
        }

        public TextView getmSenderMessageTextView() {
            return mReceiverMessageTextView;
        }
    }
}
