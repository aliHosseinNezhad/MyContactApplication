package ir.maktabkhune.mycontacts.MyInformation;

import ir.maktabkhune.mycontacts.R;
import ir.maktabkhune.mycontacts.Utils;

public class MyInfoData {
    public String firstName;
    public String lastName;

    public String phoneNumber;
    public boolean hasPhoneNo;

    public long birthDateMilliSec;
    public boolean hasBirthDate;

    public int imageId;

    public MyInfoData(){

        firstName= Utils.EmptyText;
        lastName=Utils.EmptyText;

        phoneNumber=Utils.EmptyText;
        hasPhoneNo=false;

        birthDateMilliSec=0;
        hasBirthDate=false;

        imageId= R.drawable.avatar1;
    }
}
