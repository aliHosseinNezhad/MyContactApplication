package ir.maktabkhune.mycontacts.MyInformation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.maktabkhune.mycontacts.ContactsRecyclerView.ContactAdapter;
import ir.maktabkhune.mycontacts.DataCallBack;
import ir.maktabkhune.mycontacts.MyPreferenceManger;
import ir.maktabkhune.mycontacts.R;
import ir.maktabkhune.mycontacts.Utils;

public class EditMyInformation extends Fragment {
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private TextView birthDate;
    private CircleImageView image;
    private int imageId;


    private Button confirmBtn;
    private Button setBirthDateBtn;

    private boolean hasPhoneNo;
    private boolean hasBirthDate;
    private long birthDateMilliSec;


    MaterialDatePicker materialDatePicker;

    MyInfoData myInfoData;
    DataCallBack dataCallBack;

    public EditMyInformation(MyInfoData myInfoData, DataCallBack dataCallBack) {
        this.myInfoData = myInfoData;
        this.dataCallBack = dataCallBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_my_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureView(view);
        setViewsData();
        setViewsListeners();
    }

    private void setViewsListeners() {
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                hasBirthDate = true;
                EditMyInformation.this.birthDateMilliSec = (long) selection;
                birthDate.setText(Utils.getBirthDateString((long) selection));
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().length() > 0) {
                    Bundle bundle = new Bundle();

                    bundle.putString(Utils.firstName, firstName.getText().toString());
                    bundle.putString(Utils.lastName, lastName.getText().toString());
                    bundle.putBoolean(Utils.hasBirthDate, hasBirthDate);
                    bundle.putLong(Utils.birthDateMilliSec, birthDateMilliSec);
                    bundle.putInt(Utils.imageId, imageId);
                    if (phoneNumber.getText().length() > 0) {
                        hasPhoneNo = true;
                    }
                    bundle.putBoolean(Utils.hasPhoneNo, hasPhoneNo);
                    bundle.putString(Utils.phoneNumber, phoneNumber.getText().toString());

                    Utils.exitSoftKeyboard(getActivity());
                    getActivity().getSupportFragmentManager().popBackStack();
                    dataCallBack.callBack(bundle);
                } else {
                    Toast.makeText(getActivity(), "please set the first name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setBirthDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null)
                    materialDatePicker.show(getActivity().getSupportFragmentManager(), "hello");
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageCallBack imageCallBack = new ImageCallBack() {
                    @Override
                    public void onSelectImage(int index) {
                        imageId = Utils.getImageIdByIndex[index];
                        image.setImageResource(imageId);
                    }
                };
                MyImageDialog myImageDialog = new MyImageDialog(getActivity(), imageCallBack);
                myImageDialog.show();
            }
        });
    }

    private void setViewsData() {
        if (!MyPreferenceManger.getInstance(getActivity()).isMyInfoEmpty()) {
            firstName.setText(myInfoData.firstName);
            lastName.setText(myInfoData.lastName);
            if (myInfoData.hasPhoneNo)
                phoneNumber.setText(myInfoData.phoneNumber);
            imageId = myInfoData.imageId;
            image.setImageResource(myInfoData.imageId);

            if (myInfoData.hasBirthDate)
                birthDate.setText(Utils.getBirthDateString(myInfoData.birthDateMilliSec));

            hasBirthDate = myInfoData.hasBirthDate;
            birthDateMilliSec = myInfoData.birthDateMilliSec;
        } else {
            imageId=myInfoData.imageId;
        }
    }

    private void initMaterialDatePicker() {
        CalendarConstraints.Builder calendarBuilder = new CalendarConstraints.Builder();
        if (myInfoData.hasBirthDate)
            calendarBuilder.setOpenAt(myInfoData.birthDateMilliSec);


        MaterialDatePicker.Builder<Long> builder
                = MaterialDatePicker.Builder.datePicker();

        builder.setTitleText("select contact birth date");
        builder.setCalendarConstraints(calendarBuilder.build());
        if (myInfoData.hasBirthDate)
            builder.setSelection(myInfoData.birthDateMilliSec);


        materialDatePicker = builder.build();
    }

    private void configureView(View view) {
        initMaterialDatePicker();
        firstName = view.findViewById(R.id.my_info_first_name_edit);
        lastName = view.findViewById(R.id.my_info_last_name_edit);
        birthDate = view.findViewById(R.id.my_info_birth_date_edit);
        phoneNumber = view.findViewById(R.id.my_info_phone_number_edit);
        confirmBtn = view.findViewById(R.id.confirm_my_info_btn_edit);
        setBirthDateBtn = view.findViewById(R.id.date_picker_my_info);
        image = view.findViewById(R.id.my_info_image_edit);
    }
}
