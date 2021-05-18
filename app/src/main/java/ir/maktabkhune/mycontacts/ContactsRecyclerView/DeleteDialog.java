package ir.maktabkhune.mycontacts.ContactsRecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import ir.maktabkhune.mycontacts.DataCallBack;
import ir.maktabkhune.mycontacts.R;

public class DeleteDialog extends Dialog {
    Button delete,cancel;
    DataCallBack dataCallBack;
    public DeleteDialog(@NonNull Context context, DataCallBack dataCallBack) {
        super(context);
        this.dataCallBack=dataCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warning_layout);
        delete=findViewById(R.id.delete_btn);
        cancel=findViewById(R.id.cancel_btn);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putBoolean("confirm",true);
                dataCallBack.callBack(bundle);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putBoolean("confirm",false);
                dataCallBack.callBack(bundle);
                dismiss();
            }
        });
    }
}
