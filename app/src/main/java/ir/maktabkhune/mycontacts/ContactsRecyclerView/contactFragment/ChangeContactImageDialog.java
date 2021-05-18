package ir.maktabkhune.mycontacts.ContactsRecyclerView.contactFragment;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.maktabkhune.mycontacts.R;
import ir.maktabkhune.mycontacts.Utils;

public class ChangeContactImageDialog extends Dialog implements View.OnClickListener {
    List<CircleImageView> images;
    AddNewContactFragment.ImageCallBack imageCallBack;
    public ChangeContactImageDialog(@NonNull Context context, AddNewContactFragment.ImageCallBack imageCallBack) {
        super(context);
        setContentView(R.layout.contact_image_dialog);
        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        this.imageCallBack=imageCallBack;
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
    }

    @Override
    public void onClick(View view) {
        for(int i=0;i<images.size();i++){
            if(images.get(i)==(CircleImageView) view){
                imageCallBack.setImageId(Utils.getImageIdByIndex[i]);
                dismiss();
            }
        }
    }
}
