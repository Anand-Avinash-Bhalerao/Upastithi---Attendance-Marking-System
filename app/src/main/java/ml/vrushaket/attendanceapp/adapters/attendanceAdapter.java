package ml.vrushaket.attendanceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.models.attendanceInfo;

public class attendanceAdapter extends RecyclerView.Adapter<attendanceAdapter.Viewholder> {

    private Context context;
    private ArrayList<attendanceInfo> attendanceInfoArrayList;

    // Constructor
    public attendanceAdapter(Context context, ArrayList<attendanceInfo> attendanceInfoArrayList) {
        this.context = context;
        this.attendanceInfoArrayList = attendanceInfoArrayList;
    }

    @NonNull
    @Override
    public attendanceAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_card, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull attendanceAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        attendanceInfo model = attendanceInfoArrayList.get(position);
        holder.attendanceDateTV.setText(model.getAttendanceDate());
        holder.attendanceTimeTV.setText(model.getAttendanceTime().replace(" ",""));
        holder.attendanceNoteTV.setText(model.getAttendanceNote());

        if(model.getAttendanceStatus().equalsIgnoreCase("Present")){
            holder.attendanceStatus.setImageResource(R.drawable.checked);
        }else {
            holder.attendanceStatus.setImageResource(R.drawable.cancel);
        }
    }

    @Override
    public int getItemCount() {
        return attendanceInfoArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView attendanceTimeTV,attendanceDateTV,attendanceNoteTV;
        private ImageView attendanceStatus;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            attendanceTimeTV = itemView.findViewById(R.id.attendanceTimeTV);
            attendanceDateTV = itemView.findViewById(R.id.attendanceDateTV);
            attendanceNoteTV = itemView.findViewById(R.id.attendancNoteTV);
            attendanceStatus = itemView.findViewById(R.id.imgStatus);

        }
    }
}