package no.bouvet.p2pcommunication.adapter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.bouvet.p2pcommunication.R;

public class ChatListAdapterViewHolder {

    @Bind(R.id.message_received_layout) RelativeLayout messageReceivedLayout;
    @Bind(R.id.message_received_text_view) TextView messageReceivedTextView;
    @Bind(R.id.message_sent_layout) RelativeLayout messageSentLayout;
    @Bind(R.id.message_sent_text_view) TextView messageSentTextView;

    public ChatListAdapterViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
