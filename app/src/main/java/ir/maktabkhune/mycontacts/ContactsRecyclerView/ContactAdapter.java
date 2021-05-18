package ir.maktabkhune.mycontacts.ContactsRecyclerView;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.maktabkhune.mycontacts.ContactsRecyclerView.contactFragment.AddNewContactFragment;
import ir.maktabkhune.mycontacts.ContactsRecyclerView.contactFragment.ContactInfoFragment;
import ir.maktabkhune.mycontacts.ContactsRecyclerView.contactFragment.EditContactFragment;
import ir.maktabkhune.mycontacts.DataCallBack;
import ir.maktabkhune.mycontacts.MainActivity;
import ir.maktabkhune.mycontacts.MyInformation.MyInformationFragment;
import ir.maktabkhune.mycontacts.MyPreferenceManger;
import ir.maktabkhune.mycontacts.R;
import ir.maktabkhune.mycontacts.Utils;

import static ir.maktabkhune.mycontacts.MainActivity.CALL_REQUEST_CODE;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
    MainActivity mainActivity;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    public ArrayList<ContactData> contacts;
    Menu mMenu;

    OnBackPressedCallback SelectionDesignBackPressCallBack;

    int LastPosition = -1;

    private boolean onSelection = false;

    private String WaitingForCallPhoneNumber;
    private MenuItem selectAllItem;


    public ContactAdapter(MainActivity mainActivity, ArrayList<ContactData> contacts,
                          LinearLayoutManager linearLayoutManager,
                          RecyclerView recyclerView) {
        super();
        this.linearLayoutManager = linearLayoutManager;
        this.mainActivity = mainActivity;
        this.recyclerView = recyclerView;
        this.contacts = contacts;

        setMainActivityListeners();

        readyDataForNormalDesign();


        SelectionDesignBackPressCallBack = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                exitSelectionDesign();
            }
        };
        this.mainActivity.getOnBackPressedDispatcher().addCallback(this.mainActivity, SelectionDesignBackPressCallBack);

    }

    public class ContactHolder extends RecyclerView.ViewHolder {

        TextView firstName;

        TextView lastName;
        TextView phoneNumber;
        CircleImageView image;
        ImageButton callBtn;

        ImageButton editBtn;
        ImageButton infoBtn;
        CheckBox contactCheckBox;

        LinearLayout contactDetail;

        LinearLayout contactNameLayout;
        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            configureView(itemView);
            setViewsListeners();
        }

        private void setViewsListeners() {
            setContactLayoutListeners();
            setContactCheckBoxListeners();
            setContactDetailBtn();
        }

        private void setContactDetailBtn() {
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnStartCall(getAdapterPosition());
                }
            });
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editContact(getAdapterPosition());
                }
            });
            infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContactInfo(getAdapterPosition());
                }
            });
        }

        private void setContactCheckBoxListeners() {
            contactCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contacts.get(getAdapterPosition()).isChecked = !contacts.get(getAdapterPosition()).isChecked;
                    if (allContactsSelected()) {
                        selectAllItem.setIcon(R.drawable.select_all_blue);
                        selectAllItem.setChecked(true);
                    } else {
                        selectAllItem.setIcon(R.drawable.select_all_dark);
                        selectAllItem.setChecked(false);
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

        private void setContactLayoutListeners() {
            contactNameLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!onSelection) {
                        startSelectionDesign(getAdapterPosition());

                    } else {
                        exitSelectionDesign();
                    }
                    return true;
                }

            });


            contactNameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onSelection) {
                        contactCheckBox.callOnClick();
                    } else {
                        if (contacts.get(getAdapterPosition()).DetailVisibility == View.GONE) {
                            if (LastPosition != -1) {
                                contacts.get(LastPosition).DetailVisibility = View.GONE;
                                notifyItemChanged(LastPosition);
                                LastPosition = -1;
                            }
                            contacts.get(getAdapterPosition()).DetailVisibility = View.VISIBLE;
                            LastPosition = getAdapterPosition();
                        } else {
                            contacts.get(getAdapterPosition()).DetailVisibility = View.GONE;
                            LastPosition = -1;
                        }
                        mainActivity.appBarLayout.setExpanded(false);
                        recyclerView.smoothScrollToPosition(getLayoutPosition());
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

        private void configureView(View v) {
            contactNameLayout = v.findViewById(R.id.contact_name_layout);
            contactDetail = v.findViewById(R.id.contact_detail);
            contactCheckBox = v.findViewById(R.id.check_box);

            firstName = v.findViewById(R.id.contact_name);
            phoneNumber = v.findViewById(R.id.phone_number);
            image = v.findViewById(R.id.contact_image);

            callBtn = v.findViewById(R.id.call_btn);
            editBtn = v.findViewById(R.id.edit_contact_btn);
            infoBtn = v.findViewById(R.id.contact_info_btn);


        }

    }

    public void readyDataForNormalDesign() {
        if (contacts != null)
            for (int i = 0; i < contacts.size(); i++) {
                contacts.get(i).CheckBoxVisibility = View.GONE;
                contacts.get(i).isChecked = false;
            }
    }

    private void setMainActivityListeners() {
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!onSelection)
                    if (dy > 0 && ContactAdapter.this.mainActivity.addContactBtn.getVisibility() == View.VISIBLE) {
                        ContactAdapter.this.mainActivity.addContactBtn.hide();
                    } else if (dy < 0 && ContactAdapter.this.mainActivity.addContactBtn.getVisibility() != View.VISIBLE) {
                        ContactAdapter.this.mainActivity.addContactBtn.show();
                    }
            }
        });


        this.mainActivity.addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactAdapter.this.mainActivity.addContactBtn.setRotation(0f);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ContactAdapter.this.mainActivity.addContactBtn, "rotation", 90f);
                objectAnimator.setDuration(200);
                objectAnimator.start();
                addNewContact();

            }
        });
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_template, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        setContactData(holder, position);
    }

    private void setContactData(ContactHolder holder, int position) {
        holder.image.setImageResource(contacts.get(position).imageId);
        holder.firstName.setText(contacts.get(position).firstName);
        if (contacts.get(position).phoneNumbers.size() > 0)
            holder.phoneNumber.setText(contacts.get(position).phoneNumbers.get(0));
        else
            holder.phoneNumber.setText(Utils.dontPhoneNumber);
        holder.contactCheckBox.setVisibility(contacts.get(position).CheckBoxVisibility);
        holder.contactDetail.setVisibility(contacts.get(position).DetailVisibility);
        holder.contactCheckBox.setChecked(contacts.get(position).isChecked);
    }


    public void onOptionMenuSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact_menu) {
            removeSelectedContacts();

        } else if (item.getItemId() == R.id.add_contact_menu) {
            addNewContact();
        } else if (item.getItemId() == R.id.selection_menu) {
            startSelectionDesign();
        } else if (item.getItemId() == R.id.select_all_menu) {
            item.setChecked(!item.isChecked());
            if (item.isChecked()) {
                selectAll();
                item.setIcon(R.drawable.select_all_blue);
            } else {
                deSelectAll();
                item.setIcon(R.drawable.select_all_dark);
            }
        } else if (item.getItemId() == R.id.my_info_menu) {
            openMyInfo();
        }
    }

    private void openMyInfo(){
        MyInformationFragment myInformationFragment = new MyInformationFragment();
        mainActivity.getSupportFragmentManager().beginTransaction()
                .addToBackStack(null).add(R.id.fragment_container, myInformationFragment)
                .commit();
    }

    public void onSetOptionMenuItem(Menu menu, String i) {
        mMenu = menu;
        if (i == "delete") {
            selectAllItem = mMenu.findItem(R.id.select_all_menu);
        }
    }


    public ContactData CreateContactData(Bundle bundle) {
        ContactData contactData = new ContactData();
        contactData.firstName = bundle.getString(Utils.firstName);
        contactData.lastName = bundle.getString(Utils.lastName);
        contactData.phoneNumbers.add(bundle.getString(Utils.phoneNumber));

        contactData.hasBirthDate = bundle.getBoolean(Utils.hasBirthDate);
        contactData.birthDateMilliSec = bundle.getLong(Utils.birthDateMilliSec);

        contactData.CheckBoxVisibility = View.GONE;
        contactData.DetailVisibility = View.GONE;
        contactData.isChecked = false;
        contactData.DetailIsOpened = false;


        if (bundle.getInt(Utils.imageId) == -1) {
            contactData.imageId = Utils.getImageIdByIndex[7];
        } else {
            contactData.imageId = bundle.getInt(Utils.imageId);
        }

        return contactData;
    }


    @Override
    public int getItemCount() {
        if (contacts != null && contacts.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            return contacts.size();
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            return 0;
        }
    }


    public void editContact(final int position) {
        DataCallBack dataCallBack = new DataCallBack() {
            @Override
            public void callBack(Bundle bundle) {
                ContactData contactData = CreateContactData(bundle);
                MyPreferenceManger
                        .getInstance(mainActivity).EditContact(contacts, contactData, position);
                contacts = MyPreferenceManger.getInstance(mainActivity).getData();

                notifyDataSetChanged();
            }
        };
        EditContactFragment editContactFragment =
                new EditContactFragment(contacts.get(position), dataCallBack);
        mainActivity.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, editContactFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showContactInfo(int position) {
        ContactInfoFragment contactInfoFragment = new ContactInfoFragment(this, contacts.get(position), position);
        mainActivity.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, contactInfoFragment)
                .addToBackStack(null).commit();
    }

    public void addNewContact() {
        DataCallBack dataCallBack = new DataCallBack() {
            @Override
            public void callBack(Bundle bundle) {
                ContactData contactData = CreateContactData(bundle);

                contactData.CheckBoxVisibility = View.GONE;
                contactData.DetailVisibility = View.GONE;
                contactData.isChecked = false;
                contactData.DetailIsOpened = false;

                MyPreferenceManger
                        .getInstance(mainActivity).AddedContacts(contacts, contactData);
                contacts = MyPreferenceManger.getInstance(mainActivity).getData();
                notifyDataSetChanged();
            }
        };
        AddNewContactFragment addNewContactFragment = new AddNewContactFragment(dataCallBack);
        mainActivity.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, addNewContactFragment)
                .addToBackStack(null)
                .commit();
    }


    public void selectAll() {
        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).isChecked = true;
        }
        notifyDataSetChanged();
    }

    public void deSelectAll() {
        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).isChecked = false;
        }
        notifyDataSetChanged();
    }

    public boolean allContactsSelected() {
        for (int i = 0; i < contacts.size(); i++) {
            if (!contacts.get(i).isChecked)
                return false;
        }
        return true;
    }

    public void removeSelectedContacts() {
        DataCallBack dataCallBack = new DataCallBack() {
            @Override
            public void callBack(Bundle bundle) {
                if (bundle.getBoolean("confirm")) {
                    for (int i = contacts.size() - 1; i >= 0; i--)
                        if (contacts.get(i).isChecked) {
                            contacts.remove(i);
                            notifyItemRemoved(i);
                        }
                    MyPreferenceManger.getInstance(mainActivity).setData(contacts);
                    notifyItemRangeChanged(0, getItemCount());
                    if (contacts.size() == 0) {
                        exitSelectionDesign();
                    }
                    Log.i("TAG-remove", "removeSelectedContacts: " + contacts.size());
                }
            }
        };
        if (IsAnyContactsSelected()) {
            DeleteDialog dialog = new DeleteDialog(mainActivity, dataCallBack);
            dialog.show();
        } else {
            Toast.makeText(mainActivity, "first select one or some contact and click this to delete them", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean IsAnyContactsSelected() {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isChecked)
                return true;
        }
        return false;
    }


    public void startSelectionDesign() {
        onSelection = true;
        mainActivity.appBarLayout.setExpanded(false);
        mainActivity.expandedImageView.setVisibility(View.GONE);
        mainActivity.expandedImageView2.setVisibility(View.VISIBLE);
        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).DetailVisibility = View.GONE;
            contacts.get(i).CheckBoxVisibility = View.VISIBLE;
            contacts.get(i).isChecked = false;
        }

        mainActivity.deleteOptionMenu();

        mainActivity.addContactBtn.hide();

        SelectionDesignBackPressCallBack.setEnabled(onSelection);

        notifyDataSetChanged();
    }

    private void startSelectionDesign(int position) {
        startSelectionDesign();
        contacts.get(position).isChecked = true;
        notifyDataSetChanged();
    }

    public void exitSelectionDesign() {
        onSelection = false;

        mainActivity.appBarLayout.setExpanded(true);
        mainActivity.expandedImageView.setVisibility(View.VISIBLE);
        mainActivity.expandedImageView2.setVisibility(View.GONE);

        mainActivity.addContactBtn.show();
        SelectionDesignBackPressCallBack.setEnabled(false);
        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).CheckBoxVisibility = View.GONE;
            contacts.get(i).isChecked = false;
        }
        mainActivity.normalOptionMenu();
        readyDataForNormalDesign();
        notifyDataSetChanged();
    }




    public void OnStartCall(int position) {
        if (contacts.get(position).phoneNumbers.size() > 0) {
            if (ActivityCompat.checkSelfPermission(mainActivity,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startCall(contacts.get(position).phoneNumbers.get(0));
            }
            else {
                WaitingForCallPhoneNumber = contacts.get(position).phoneNumbers.get(0);
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_CODE);
            }
        } else {
            Toast.makeText(mainActivity, "dot't have any phoneNumber", Toast.LENGTH_SHORT).show();
        }
    }

    public void startCall() {
        startCall(WaitingForCallPhoneNumber);
        WaitingForCallPhoneNumber = "";
    }

    public void startCall(String phoneNumber) {
        startVibrator(60);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        mainActivity.startActivity(callIntent);
    }

    public void DialNumber() {
        String TELEPHONE_SCHEMA = "tel:";
        Uri uri = Uri.parse(TELEPHONE_SCHEMA + WaitingForCallPhoneNumber);
        Intent phoneCallIntent=new Intent(Intent.ACTION_DIAL);
        phoneCallIntent.setData(uri);
        mainActivity.startActivity(phoneCallIntent);
    }



    public void startVibrator(int milliSeconds) {
        Vibrator v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milliSeconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(milliSeconds);
        }
    }

}
