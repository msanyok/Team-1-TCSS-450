package edu.uw.tcss450.group1project.ui.contacts;

import android.widget.ImageView;

public class Contact {

    private String myFirst;
    private String myLast;
    private String myNickname;
    private ImageView myImage;

    public Contact(final String theFirst, final String theLast, final String theNickname) {
        myFirst = theFirst;
        myLast = theLast;
        myNickname = theNickname;
//        myImage = theImage;
    }

    public String getFirst() {
        return myFirst;
    }

    public String getLast() {
        return myLast;
    }

    public String getNickname() {
        return myNickname;
    }

    public void setNickname(final String theNickname) {
        myNickname = theNickname;
    }

    public void setImage(final ImageView theImage) {
        myImage = theImage;
    }
}
