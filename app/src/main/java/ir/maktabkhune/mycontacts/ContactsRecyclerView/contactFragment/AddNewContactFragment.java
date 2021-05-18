package ir.maktabkhune.mycontacts.ContactsRecyclerView.contactFragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.maktabkhune.mycontacts.DataCallBack;
import ir.maktabkhune.mycontacts.R;
import ir.maktabkhune.mycontacts.Utils;

public class AddNewContactFragment extends Fragment {
    public EditText firstName;
    public EditText lastName;

    public EditText phoneNumber;
    public TextView createContact;

    public TextView birthDate;
    public boolean hasBirthDate;

    public CircleImageView imageView;

    public ScrollView scrollView;
    public int imageId = -1;

    public long birthDateMilliSec;
    public ConstraintLayout constraintLayout;
    Button button;

    DataCallBack dataCallBack;

    public AddNewContactFragment(DataCallBack dataCallBack) {
        this.dataCallBack = dataCallBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_edit_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureView(view);
        setViewsListeners();
        createContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().length() > 0 && phoneNumber.length() > 0) {
                    Bundle bundle = new Bundle();

                    bundle.putString(Utils.firstName, firstName.getText().toString());
                    bundle.putString(Utils.lastName, lastName.getText().toString());

                    bundle.putString(Utils.phoneNumber, phoneNumber.getText().toString());

                    bundle.putBoolean(Utils.hasBirthDate,hasBirthDate);
                    bundle.putLong(Utils.birthDateMilliSec,birthDateMilliSec);
                    bundle.putInt(Utils.imageId, imageId);

                    dataCallBack.callBack(bundle);
                    exitSoftKeyBoard();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    exitSoftKeyBoard();
                    Toast.makeText(getActivity(), "please set the name and phoneNumber !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setViewsListeners() {
        constraintLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                final View focusedView = getActivity().getCurrentFocus();
                if (focusedView != null && focusedView.getBottom()!=0)
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, focusedView.getBottom());
                        }
                    });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalendarConstraints.Builder calendarBuilder = new CalendarConstraints.Builder();


                MaterialDatePicker.Builder<Long> builder
                        = MaterialDatePicker.Builder.datePicker();

                builder.setTitleText("select contact birth date");
                builder.setCalendarConstraints(calendarBuilder.build());


                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "hello");


                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Log.i("materialPicker", "onPositiveButtonClick: " + (long) selection);
                        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("iran"));
                        calendar1.setTimeInMillis((long) selection);
                        AddNewContactFragment.this.hasBirthDate=true;
                        AddNewContactFragment.this.birthDateMilliSec=(long)selection;

                        birthDate.setText("contact birth date is \n year :" + calendar1.get(Calendar.YEAR) + "/ month: " + (calendar1.get(Calendar.MONTH) + 1) + "/ day: " + calendar1.get(Calendar.DAY_OF_MONTH));

                    }
                });


            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeContactImageDialog dialog = new ChangeContactImageDialog(getActivity(), imageCallBack);
                dialog.show();
            }
        });


    }

    private void configureView(View view) {
        firstName = view.findViewById(R.id.first_name_editText);
        lastName = view.findViewById(R.id.last_name_editText);
        phoneNumber = view.findViewById(R.id.phone_number_editText);
        createContact = view.findViewById(R.id.create_contact_textView);
        birthDate = view.findViewById(R.id.birth_date_txt);
        imageView = view.findViewById(R.id.contact_add_image);
        button = view.findViewById(R.id.date_picker);
        scrollView = view.findViewById(R.id.scrollView2);
        constraintLayout = view.findViewById(R.id.contact_inf_container);
    }

    public void exitSoftKeyBoard() {
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    public interface ImageCallBack {
        void setImageId(int id);
    }

    ImageCallBack imageCallBack = new ImageCallBack() {
        @Override
        public void setImageId(int id) {
            imageView.setImageResource(id);
            imageId = id;
        }
    };
}
