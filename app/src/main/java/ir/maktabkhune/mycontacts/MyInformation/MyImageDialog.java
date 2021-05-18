package ir.maktabkhune.mycontacts.MyInformation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.maktabkhune.mycontacts.R;

public class MyImageDialog extends Dialog implements View.OnClickListener {
    List<CircleImageView> images;
    ImageCallBack imageCallBack;
    TextView imageSelectionDialog;
    public MyImageDialog(@NonNull Context context,ImageCallBack imageCallBack) {
        super(context);
        this.imageCallBack=imageCallBack;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_image_dialog);
        imageSelectionDialog=findViewById(R.id.image_selection_dialog);
        imageSelectionDialog.setText("select your picture");


        images=new ArrayList<>();
        images.add((CircleImageView) findViewById(R.id.image_1));
        images.add((CircleImageView) findViewById(R.id.image_2));
        images.add((CircleImageView) findViewById(R.id.image_3));
        images.add((CircleImageView) findViewById(R.id.image_4));
        images.add((CircleImageView) findViewById(R.id.image_5));
        images.add((CircleImageView) findViewById(R.id.image_6));
        images.add((CircleImageView) findViewById(R.id.image_7));
        images.add((CircleImageView) findViewById(R.id.image_8));
        for(int i=0;i<images.size();i++){
            images.get(i).setOnClickListener(this);
        }


        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);

    }

    @Override
    public void onClick(View view) {
        for(int i=0;i<images.size();i++){
            if(images.get(i)== view){
                imageCallBack.onSelectImage(i);
                dismiss();
            }
        }
    }
}
