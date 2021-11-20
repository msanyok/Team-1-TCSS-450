/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
//import com.google.android.material.shape.CornerFamily;

import java.util.List;


import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatMessageBinding;

/**
 * A ViewAdapter used for showing a chat thread.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ChatRecyclerViewAdapter extends
        RecyclerView.Adapter<ChatRecyclerViewAdapter.MessageViewHolder> {

    /** The list of messages to show */
    private final List<ChatMessage> mMessages;

    /** T
     * he unique identifier (currently email) that determines if
     * a chat messages should be displayed as a sent or recieved message.
     */
    private final String mPersonalIdentifier;

    /**
     * Creates a new ChatRecyclerViewAdapter that shows the given messages for a particular chat
     * The account's (email) thePersonalIdentifier is used to determine how certain messages should display
     *
     * @param theMessages the list of messages to show
     * @param thePersonalIdentifier this user's email that helps identify which messages they sent
     */
    public ChatRecyclerViewAdapter(final List<ChatMessage> theMessages, final String thePersonalIdentifier) {
        this.mMessages = theMessages;
        mPersonalIdentifier = thePersonalIdentifier;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                final int theViewType) {
        return new MessageViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_chat_message, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder theHolder,
                                 final int thePosition) {
        theHolder.setMessage(mMessages.get(thePosition));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * Inner class that defines how each message should look
     *
     * @author Charles Bryan
     * @version Fall 2021
     */
    class MessageViewHolder extends RecyclerView.ViewHolder {

        /** The current view */
        private final View mView;

        /** */
        private FragmentChatMessageBinding mBinding;

        /**
         * Creates a new View Holder
         *
         * @param theView the current view
         */
        public MessageViewHolder(@NonNull final View theView) {
            super(theView);
            mView = theView;
            mBinding = FragmentChatMessageBinding.bind(theView);
        }

        /**
         * Sets up a message UI based on its state.
         * @param theMessage
         */
        void setMessage(final ChatMessage theMessage) {

            final Resources res = mView.getContext().getResources();
            final MaterialCardView card = mBinding.cardRoot;

            int standard = (int) res.getDimension(R.dimen.chat_margin);
            int extended = (int) res.getDimension(R.dimen.chat_margin_sided);

            if (mPersonalIdentifier.equals(theMessage.getSender())) {
                //This message is from the user. Format it as such
                mBinding.textMessage.setText(theMessage.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                //Set the left margin
                layoutParams.setMargins(extended, standard, standard, standard);
                // Set this View to the right (end) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.END;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                                res.getColor(R.color.cardview_light_background, null), // todo: set color properly
                                16));
                mBinding.textMessage.setTextColor(
                        res.getColor(R.color.cardview_dark_background, null)); // todo: set color properly

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(ColorUtils.setAlphaComponent(
                        res.getColor(R.color.cardview_dark_background, null), // todo: set color properly
                        200));

                card.requestLayout();

            } else {

                //This message is from another user. Format it as such
                mBinding.textMessage.setText(theMessage.getSender() +
                        ": " + theMessage.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                //Set the right margin
                layoutParams.setMargins(standard, standard, extended, standard);
                // Set this View to the left (start) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.START;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                                res.getColor(R.color.slate_blue, null), // todo: set color properly
                                16));

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(ColorUtils.setAlphaComponent(
                        res.getColor(R.color.gray, null), // todo: set color properly
                        200));

                mBinding.textMessage.setTextColor(
                        res.getColor(R.color.cream, null)); // todo: set color properly

                card.requestLayout();
            }
        }
    }
}

