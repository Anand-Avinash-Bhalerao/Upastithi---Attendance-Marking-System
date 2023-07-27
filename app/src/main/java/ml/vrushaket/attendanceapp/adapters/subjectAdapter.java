package ml.vrushaket.attendanceapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.models.classInfo;

public class subjectAdapter extends RecyclerView.Adapter<subjectAdapter.Viewholder> {

    private Context context;
    private ArrayList<classInfo> classInfoArrayList;

    // Constructor
    public subjectAdapter(Context context, ArrayList<classInfo> classInfoArrayList) {
        this.context = context;
        this.classInfoArrayList = classInfoArrayList;
    }

    @NonNull
    @Override
    public subjectAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull subjectAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        classInfo model = classInfoArrayList.get(position);
        holder.courseNameTV.setText(model.getClassCourse());
        holder.courseClassNameTV.setText(model.getClassName());
        holder.courseDescTV.setText(model.getClassDesc());
        holder.courseIDTV.setText(model.getClassID());
        if(model.getTotalPresent() == null){
            holder.tvRatio.setText("0/"+model.getTotalSessions());
        }else{
            holder.tvRatio.setText(model.getTotalPresent()+"/"+model.getTotalSessions());
        }


        //holder.courseIV.setImageResource(model.getCourse_image());

        if(model.getTotalPresent()!= null && model.getTotalSessions() !=null){
            float percent = (Float.parseFloat(model.getTotalPresent()) / Float.parseFloat(model.getTotalSessions())) * 100;
            holder.tvPercent.setText((int) percent+"%");
            // Set Progress
            holder.circularProgressBar.setProgress(Float.parseFloat(model.getTotalPresent()));
            // or with animation
            holder.circularProgressBar.setProgressWithAnimation(percent, Long.valueOf(2000)); // =1s

            // Set Progress Max
            holder.circularProgressBar.setProgressMax(100f);

            if(percent < 75){
                holder.circularProgressBar.setProgressBarColorStart(Color.parseColor("#ff4d4d"));
                holder.circularProgressBar.setProgressBarColorEnd(Color.parseColor("#ff4d4d"));

            } else{
                holder.circularProgressBar.setProgressBarColorStart(Color.DKGRAY);
                holder.circularProgressBar.setProgressBarColorEnd(Color.DKGRAY);
            }

            holder.circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
            holder.circularProgressBar.setProgressBarWidth(5f); // in DP
            holder.circularProgressBar.setBackgroundProgressBarWidth(2f); // in DP
            holder.circularProgressBar.setRoundBorder(false);
            holder.circularProgressBar.setStartAngle(0f);
            holder.circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
        }

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return classInfoArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView courseIV;
        private TextView courseNameTV, courseClassNameTV,courseDescTV, courseIDTV, tvRatio, tvPercent;
        CircularProgressBar circularProgressBar;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            courseIV = itemView.findViewById(R.id.idIVCourseImage);
            courseNameTV = itemView.findViewById(R.id.idTVCourseName);
            courseClassNameTV = itemView.findViewById(R.id.idTVCourseClassName);
            courseDescTV = itemView.findViewById(R.id.idTVCourseDesc);
            courseIDTV = itemView.findViewById(R.id.idTVCourseID);
            tvRatio = itemView.findViewById(R.id.tvRatio);
            tvPercent = itemView.findViewById(R.id.tvPercent);
            circularProgressBar = itemView.findViewById(R.id.circularProgressBar);
        }
    }
}