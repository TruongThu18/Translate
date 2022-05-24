package com.example.translater.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.translater.R;
import com.example.translater.model.Translate;
import com.example.translater.repo.TranslateDataBase;

import java.util.ArrayList;
import java.util.List;

public class AdapterRv extends RecyclerView.Adapter<AdapterRv.ViewHoler> {
    List<Translate> list;
    Context context;

    public AdapterRv(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void addData(List<Translate> list){
        this.list =list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_row_rv, parent, false);
        return  new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        holder.outto.setText(list.get(position).getTo());
        holder.outfrom.setText(list.get(position).getFrom());
        holder.lang2.setText(list.get(position).getLang2());
        holder.lang1.setText(list.get(position).getLang1());
        holder.btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showconfirm(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list==null) return 0;
        return list.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder{
        TextView outto,outfrom,lang1, lang2;
        ImageView btndel;
        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            outfrom = itemView.findViewById(R.id.outFrom);
            outto = itemView.findViewById(R.id.outTo);
            btndel = itemView.findViewById(R.id.btndel);
            lang1 = itemView.findViewById(R.id.outlang1);
            lang2 = itemView.findViewById(R.id.outlang2);
        }
    }
    private  void showconfirm(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Note");
        builder.setMessage("Bạn có muốn xóa bản dịch này?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TranslateDataBase.getInstance(context.getApplicationContext()).transDao().del(list.get(pos));
                list.remove(pos);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
