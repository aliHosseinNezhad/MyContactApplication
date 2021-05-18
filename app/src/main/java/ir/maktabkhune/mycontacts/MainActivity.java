package ir.maktabkhune.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import ir.maktabkhune.mycontacts.ContactsRecyclerView.ContactData;
import ir.maktabkhune.mycontacts.ContactsRecyclerView.ContactAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int READ_CONTACTS_REQUEST_CODE = 1983;
    public static final int CALL_REQUEST_CODE = 3432;
    public Toolbar toolbar;
    ContactAdapter contactsAdapter;
    private ArrayList<ContactData> contacts;

    public RecyclerView recyclerView;

    public FloatingActionButton addContactBtn;

    public CollapsingToolbarLayout collapsingToolbar;
    public AppBarLayout appBarLayout;
    public ImageView expandedImageView;
    public ImageView expandedImageView2;
    private boolean deleteOptionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewConfigure();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Contacts");
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        requestReadContactPermission();
    }

    private void setViewConfigure() {
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Contacts");
        collapsingToolbar.setExpandedTitleColor(getColor(R.color.dark_gray));
        collapsingToolbar.setCollapsedTitleTextColor(getColor(R.color.dark_gray));


        expandedImageView = findViewById(R.id.expandedImage);
        expandedImageView2 = findViewById(R.id.expandedImage2);

        recyclerView = findViewById(R.id.recycler_view);
        addContactBtn = findViewById(R.id.plus_imageButton);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);
    }



    public void showContactList() {
        contacts = MyPreferenceManger.getInstance(this).getData();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        contactsAdapter =
                new ContactAdapter(MainActivity.this, contacts, linearLayoutManager, recyclerView);
        recyclerView.setAdapter(contactsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (deleteOptionMenu) {
            menu.setGroupVisible(R.id.norma_option_group, false);
            menu.setGroupVisible(R.id.delete_option_group, true);
            deleteOptionMenu = false;
            if (contactsAdapter != null)
                contactsAdapter.onSetOptionMenuItem(menu, "delete");
        } else {
            menu.setGroupVisible(R.id.delete_option_group, false);
            menu.setGroupVisible(R.id.norma_option_group, true);
            if (contactsAdapter != null)
                contactsAdapter.onSetOptionMenuItem(menu, "normal");
        }
        return true;
    }

    public void deleteOptionMenu() {
        invalidateOptionsMenu();
        deleteOptionMenu = true;
    }

    public void normalOptionMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        contactsAdapter.onOptionMenuSelected(item);
        return true;
    }



    private void requestReadContactPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            showContactList();

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            showRationaleDialog();
        } else {
            showRationaleDialog();
        }
    }

    private void showRationaleDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setPositiveButton("go to permission or deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(MainActivity.this
                        , new String[]{Manifest.permission.READ_CONTACTS}
                        , READ_CONTACTS_REQUEST_CODE);
            }
        });
        builder.setCancelable(false);
        builder.setTitle("Request Contact Permission");
        builder.setMessage("If you want this app to add your contacts to your contacts list, please give this app permission.");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoneContacts();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showContactList();
            }
        } else if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactsAdapter.startCall();
            }
            else if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_DENIED){
                contactsAdapter.DialNumber();
            }
        }
    }



    private void getPhoneContacts() {
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                getPhoneContactsList();
                OnLoadingFragment onLoadingFragment = new OnLoadingFragment(MainActivity.this);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, onLoadingFragment)
                        .addToBackStack(null).commit();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                getSupportFragmentManager().popBackStack();
                showContactList();
            }
        };
        asyncTask.execute();
    }

    private void getPhoneContactsList() {
        contacts = MyPreferenceManger.getInstance(this).getData();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                ContactData contactData = new ContactData();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                contactData.firstName = name;
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactData.phoneNumbers.add(phoneNo);
                        Log.i("TAG", "Name: " + name);
                        Log.i("TAG", "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
                contactData.imageId = Utils.getImageIdByIndex[new Random().nextInt(Utils.getImageIdByIndex.length)];
                contacts.add(contactData);
            }
            MyPreferenceManger.getInstance(this).AddedContacts(contacts);
        }
        if (cur != null) {
            cur.close();
        }
    }


}
