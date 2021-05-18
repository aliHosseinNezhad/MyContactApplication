package ir.maktabkhune.mycontacts.ContactsRecyclerView;

import android.view.View;

import java.util.ArrayList;

public class ContactData {
    //contact data
    public String firstName;
    public String lastName;
    public long birthDateMilliSec;
    public boolean hasBirthDate;
    public ArrayList<String> phoneNumbers;
    public int imageId;


    //contact template view data
//visibility

    public int DetailVisibility = View.GONE;
    public boolean isChecked;
    public boolean DetailIsOpened;
    public int CheckBoxVisibility;
    //

    public ContactData() {
        phoneNumbers = new ArrayList<>();
        isChecked = false;
        hasBirthDate=false;
    }
}
