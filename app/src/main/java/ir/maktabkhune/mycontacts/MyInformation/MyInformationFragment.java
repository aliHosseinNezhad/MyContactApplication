package ir.maktabkhune.mycontacts.MyInformation;

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
import ir.maktabkhune.mycontacts.DataCallBack;
import ir.maktabkhune.mycontacts.MyPreferenceManger;
import ir.maktabkhune.mycontacts.R;
import ir.maktabkhune.mycontacts.Utils;

public class MyInformationFragment extends Fragment {
    private TextView firstName;
    private TextView lastName;
    private TextView phoneNumber;
    private TextView birthDate;
    private CircleImageView image;
    private ImageButton editInfoBtn;

    MyInfoData myInfoData;

    DataCallBack dataCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_iformation_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConfigureViews(view);
        setViewsData();
        setViewsListeners();
    }

    private void setViewsListeners() {
        editInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataCallBack = new DataCallBack() {
                    @Override
                    public void callBack(Bundle bundle) {
                        myInfoData.firstName = bundle.getString(Utils.firstName);
                        myInfoData.lastName = bundle.getString(Utils.lastName);
                        myInfoData.hasBirthDate = bundle.getBoolean(Utils.hasBirthDate);
                        myInfoData.hasPhoneNo = bundle.getBoolean(Utils.hasPhoneNo);
                        myInfoData.phoneNumber = bundle.getString(Utils.phoneNumber);
                        myInfoData.birthDateMilliSec = bundle.getLong(Utils.birthDateMilliSec);
                        myInfoData.imageId=bundle.getInt(Utils.imageId);
                        MyPreferenceManger.getInstance(getActivity()).setMyInfo(myInfoData);
                        setViewsData();
                    }
                };
                EditMyInformation editMyInformation = new EditMyInformation(myInfoData, dataCallBack);
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, editMyInformation)
                            .addToBackStack(null)
                            .commit();

            }
        });
    }

    private void ConfigureViews(View view) {
        firstName = view.findViewById(R.id.my_info_first_name_edit);
        lastName = view.findViewById(R.id.my_info_last_name_edit);
        phoneNumber = view.findViewById(R.id.my_info_phone_number_edit);
        birthDate = view.findViewById(R.id.my_info_birth_date_edit);
        image = view.findViewById(R.id.my_info_image_edit);
        editInfoBtn = view.findViewById(R.id.confirm_my_info_btn_edit);
    }

    private void setViewsData() {
        firstName.setText("?");
        lastName.setText("?");
        phoneNumber.setText("?");
        birthDate.setText("?");
        myInfoData = MyPreferenceManger.getInstance(getActivity()).getMyInfo();
        if (!MyPreferenceManger.getInstance(getActivity()).isMyInfoEmpty()) {
            if (firstName.getText().length() > 0)
                firstName.setText(myInfoData.firstName);

            if (myInfoData.lastName.length() > 0)
                lastName.setText(myInfoData.lastName);

            if (myInfoData.hasPhoneNo)
                phoneNumber.setText(myInfoData.phoneNumber);

            image.setImageResource(myInfoData.imageId);


            if (myInfoData.hasBirthDate)
                birthDate.setText(Utils.getBirthDateString(myInfoData.birthDateMilliSec));

        }
    }
}
