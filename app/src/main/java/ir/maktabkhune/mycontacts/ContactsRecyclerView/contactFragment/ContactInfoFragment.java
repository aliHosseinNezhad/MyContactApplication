package ir.maktabkhune.mycontacts.ContactsRecyclerView.contactFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.maktabkhune.mycontacts.ContactsRecyclerView.ContactAdapter;
import ir.maktabkhune.mycontacts.ContactsRecyclerView.ContactData;
import ir.maktabkhune.mycontacts.R;
import ir.maktabkhune.mycontacts.Utils;

public class ContactInfoFragment extends Fragment {
    ContactAdapter contactAdapter;
    ContactData contactData;
    int position;

    TextView firstName;
    TextView lastName;
    TextView phoneNumber;
    TextView birthDate;
    CircleImageView Image;
    ImageButton callBtn;
    ImageButton editBtn;

    public ContactInfoFragment(ContactAdapter contactAdapter, ContactData contactData,int position) {
        this.contactAdapter = contactAdapter;
        this.contactData = contactData;
        this.position=position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureViews(view);
        setViewsData();
        setViewsListeners();
    }

    private void setViewsListeners() {
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactAdapter.OnStartCall(position);
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
                contactAdapter.editContact(position);
            }
        });
    }

    private void setViewsData() {
        firstName.setText(contactData.firstName);
        lastName.setText(contactData.lastName);
        if (contactData.phoneNumbers.size() > 0)
            phoneNumber.setText(contactData.phoneNumbers.get(0));
        if (contactData.hasBirthDate)
            birthDate.setText(Utils.getBirthDateString(contactData.birthDateMilliSec));
        Image.setImageResource(contactData.imageId);
    }

    private void configureViews(View view) {
        firstName = view.findViewById(R.id.first_name_tv);
        lastName = view.findViewById(R.id.last_name_tv);
        phoneNumber = view.findViewById(R.id.phone_number_tv);
        birthDate = view.findViewById(R.id.birth_date_tv);
        Image = view.findViewById(R.id.contact_info_image);
        callBtn = view.findViewById(R.id.contact_info_call);
        editBtn = view.findViewById(R.id.contact_info_edit);
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewsData();
    }
}
