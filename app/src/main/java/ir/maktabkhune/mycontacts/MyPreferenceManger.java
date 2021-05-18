package ir.maktabkhune.mycontacts;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ir.maktabkhune.mycontacts.ContactsRecyclerView.ContactData;
import ir.maktabkhune.mycontacts.MyInformation.MyInfoData;

public class MyPreferenceManger {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static MyPreferenceManger myPreferenceManger;

    private String ContactKey = "contact";
    private String MyInfoKey = "myInfo";
    private String RationalDialogKey="rational_dialog";

    public static MyPreferenceManger getInstance(Context context) {
        if (myPreferenceManger == null) {
            myPreferenceManger = new MyPreferenceManger(context);
            return myPreferenceManger;
        }
        return myPreferenceManger;
    }

    private MyPreferenceManger(Context context) {
        sharedPreferences = context.getSharedPreferences("preference_manager", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public ArrayList<ContactData> getData() {
        Type type = new TypeToken<ArrayList<ContactData>>() {
        }.getType();
        Gson gson = new Gson();
        if (isContactDataEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(sharedPreferences.getString(ContactKey, null), type);
        }
    }

    public void setMyInfo(MyInfoData myData) {
        Gson gson = new Gson();
        editor.putString(MyInfoKey, gson.toJson(myData));
        editor.apply();
    }

    public MyInfoData getMyInfo() {
        if (isMyInfoEmpty()) {
            return new MyInfoData();
        } else {
            MyInfoData myInfoData;
            Gson gson = new Gson();
            Type type = new TypeToken<MyInfoData>() {
            }.getType();
            myInfoData = gson.fromJson(sharedPreferences.getString(MyInfoKey, null), type);
            return myInfoData;
        }
    }

    public boolean isMyInfoEmpty() {
        if (sharedPreferences.getString(MyInfoKey, null) == null) {
            return true;
        } else {
            return false;
        }
    }

    public void setData(ArrayList<ContactData> contacts) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ContactDataModel>>() {
        }.getType();
        editor.putString(ContactKey, gson.toJson(contacts, type));
        editor.apply();
    }

    public boolean isContactDataEmpty() {
        if (sharedPreferences.getString(ContactKey, null) == null) {
            return true;
        } else {
            return false;
        }
    }

    public void AddedContacts(ArrayList<ContactData> contacts) {
        Comparator<ContactData> comparator = new Comparator<ContactData>() {
            @Override
            public int compare(ContactData one, ContactData two) {
                String nameOne, nameTwo;
                nameOne = one.firstName + " " + one.lastName;
                nameTwo = two.firstName + " " + two.lastName;
                int result = nameOne.compareTo(nameTwo);
                if (result < 0) {
                    return -1;
                } else if (result == 0) {
                    return 0;
                } else {
                    return +1;
                }
            }
        };
        Collections.sort(contacts, comparator);
        setData(contacts);
    }

    public void AddedContacts(ArrayList<ContactData> contacts, ContactData contactData) {
        contacts.add(contactData);
        AddedContacts(contacts);
    }

    public void EditContact(ArrayList<ContactData> contacts, ContactData contactData, int lastPosition) {
        contacts.remove(lastPosition);
        AddedContacts(contacts, contactData);
    }

}
