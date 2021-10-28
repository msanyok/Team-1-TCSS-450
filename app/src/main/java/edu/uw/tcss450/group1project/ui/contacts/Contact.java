package edu.uw.tcss450.group1project.ui.contacts;

public class Contact {

    private String myFirst;
    private String myLast;
    private String myNickname;

    public Contact(final String theFirst, final String theLast, final String theNickname) {
        myFirst = theFirst;
        myLast = theLast;
        myNickname = theNickname;
    }

    @Override
    public String toString() {
        return myFirst + myLast + String.format("(%s)", myNickname);
    }
}
