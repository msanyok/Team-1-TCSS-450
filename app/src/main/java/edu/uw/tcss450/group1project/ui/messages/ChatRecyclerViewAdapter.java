/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** The size of chat bubble text */
    private static final int TEXT_SIZE = 16;

    /** The size of card corner radii */
    private static final int RADIUS = 15;

    /** The list of messages to show */
    private final List<ChatMessage> mMessages;

    /**
     * The unique identifier (currently nickname) that determines if
     * a chat messages should be displayed as a sent or received message.
     */
    private final String mPersonalIdentifier;

    /** The mapping of each text message's full width */
    private final Map<Integer, Integer> mTextWidthMap;

    /** The context used for creating text views */
    private final Context mContext;

    /**
     * Creates a new ChatRecyclerViewAdapter that shows the given messages for a particular chat.
     * The account's (email) thePersonalIdentifier is
     * used to determine how certain messages should display
     *
     * @param theMessages the list of messages to show
     * @param thePersonalIdentifier this user's email that helps identify which messages they sent
     * @param theContext the supplied fragment context
     */
    public ChatRecyclerViewAdapter(final List<ChatMessage> theMessages,
                                   final String thePersonalIdentifier,
                                   final Context theContext) {
        this.mMessages = theMessages;
        mPersonalIdentifier = thePersonalIdentifier;
        mTextWidthMap = new HashMap<>();
        mContext = theContext;
        constructTextWidthMapping();
    }

    /**
     * Constructs a mapping of each chat message to its corresponding text view width
     */
    public void constructTextWidthMapping() {
        for (int i = 0; i < mMessages.size(); i++) {
            TextView tView = new TextView(mContext);
            tView.setTextSize(TEXT_SIZE);
            if (mMessages.get(i).getSender().equals(mPersonalIdentifier)) {
                tView.setText(mMessages.get(i).getMessage());
            } else {
                tView.setText(String.format("%s: %s",
                        mMessages.get(i).getSender(), mMessages.get(i).getMessage()));
            }
            tView.measure(0, 0);
            mTextWidthMap.put(mMessages.get(i).getMessageId(), tView.getMeasuredWidth());
        }
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
        theHolder.setMessage(mMessages.get(thePosition), thePosition);
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

        /** The View Binding the the chat message card */
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
         *
         * @param theMessage the message to be displayed
         * @param thePosition the list position of the message
         */
        void setMessage(final ChatMessage theMessage, final int thePosition) {

            final Resources res = mView.getContext().getResources();
            final MaterialCardView card = mBinding.cardRoot;
            int[] attr = {
                    R.attr.cardColor,
                    R.attr.colorAccent,
                    R.attr.cardTextColor,
                    R.attr.buttonTextColor,
                    R.attr.background,
                    R.attr.adminTextColor
            };

            TypedArray typedArray = mView.getContext().obtainStyledAttributes(attr);
            int sendCardColor = typedArray.getResourceId(0, R.color.white);
            int receiveCardColor = typedArray.getResourceId(1, R.color.white);
            int sendTextColor = typedArray.getResourceId(2, R.color.black);
            int receiveTextColor = typedArray.getResourceId(3, R.color.black);
            int backgroundColor = typedArray.getResourceId(4, R.color.white);
            int adminTextColor = typedArray.getResourceId(5, R.color.white);
            typedArray.recycle();

            int standard = (int) res.getDimension(R.dimen.chat_margin);
            int extended = (int) res.getDimension(R.dimen.chat_margin_sided);

            ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    card.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] corners = { RADIUS, RADIUS, RADIUS, RADIUS };
                    if (thePosition >= 1 &&
                        theMessage.getSender().equals(mMessages.get(thePosition - 1).getSender())) {
                        int previousWidth =
                                mTextWidthMap.get(mMessages.get(thePosition - 1).getMessageId());
                        if (theMessage.getSender().equals(mPersonalIdentifier)) {
                            corners[1] = 0;
                            if (mBinding.textMessage.getWidth() <= previousWidth) {
                                corners[0] = 0;
                            }
                        } else {
                            corners[0] = 0;
                            if (mBinding.textMessage.getWidth() <= previousWidth) {
                                corners[1] = 0;
                            }
                        }
                    }
                    if (thePosition < mMessages.size() - 1 &&
                        theMessage.getSender().equals(mMessages.get(thePosition + 1).getSender())) {
                        int nextWidth =
                                mTextWidthMap.get(mMessages.get(thePosition + 1).getMessageId());
                        if (theMessage.getSender().equals(mPersonalIdentifier)) {
                            corners[3] = 0;
                            if (mBinding.textMessage.getWidth() <= nextWidth) {
                                corners[2] = 0;
                            }
                        } else {
                            corners[2] = 0;
                            if (mBinding.textMessage.getWidth() <= nextWidth) {
                                corners[3] = 0;
                            }
                        }
                    }
                    card.setShapeAppearanceModel(card.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, corners[0])
                    .setTopRightCorner(CornerFamily.ROUNDED, corners[1])
                    .setBottomLeftCorner(CornerFamily.ROUNDED, corners[2])
                    .setBottomRightCorner(CornerFamily.ROUNDED, corners[3])
                    .build());
                    return true;
                }
            };

            card.getViewTreeObserver().addOnPreDrawListener(listener);

            if (mPersonalIdentifier.equals(theMessage.getSender())) {
                // This message is from the user. Format it as such
                mBinding.textMessage.setText(theMessage.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                // Set the left margin
                layoutParams.setMargins(extended, standard, standard, standard);
                // Set this View to the right (end) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.END;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                                res.getColor(sendCardColor, null),255));
                mBinding.textMessage.setTextColor(res.getColor(sendTextColor, null));

                card.requestLayout();

            } else if (theMessage.getSender().equals("TalkBox Admin")) {

                // This message is from the admin. Format it as such
                mBinding.textMessage.setText(theMessage.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                // Set the right margin
                layoutParams.setMargins(standard, standard, standard, standard);
                // Set this View to the middle
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.CENTER;

                mBinding.textMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                                res.getColor(backgroundColor, null),255));

                mBinding.textMessage.setTextColor(
                        res.getColor(adminTextColor, null));

                card.requestLayout();
            } else {

                // This message is from another user. Format it as such
                mBinding.textMessage.setText(theMessage.getSender() +
                        ": " + theMessage.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                // Set the right margin
                layoutParams.setMargins(standard, standard, extended, standard);
                // Set this View to the left (start) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.START;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                                res.getColor(receiveCardColor, null),255));

                mBinding.textMessage.setTextColor(
                        res.getColor(receiveTextColor, null));

                card.requestLayout();
            }
        }
    }
}

