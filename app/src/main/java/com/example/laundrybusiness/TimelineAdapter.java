package com.example.laundrybusiness;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<TimelineStep> steps;
    private String currentStatus;

    public TimelineAdapter(List<TimelineStep> steps, String currentStatus) {
        this.steps = steps;
        this.currentStatus = currentStatus;
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tracking_step, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimelineViewHolder holder, int position) {
        TimelineStep step = steps.get(position);
        holder.statusTitle.setText(step.title);

        // Logic to determine if the step is complete, active, or pending
        boolean isComplete = isStepComplete(step.dataStatus);
        boolean isCurrent = step.dataStatus.equals(currentStatus);

        int color;
        if (isComplete) {
            // Completed steps use primary color and a check icon
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary);
            holder.statusDot.setImageResource(android.R.drawable.checkbox_on_background);
            holder.statusSubtitle.setText(step.completedTimestamp); // Show actual completion time
        } else if (isCurrent) {
            // Current step uses accent color/black and a solid dot
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent);
            holder.statusDot.setImageResource(android.R.drawable.presence_online);
            holder.statusSubtitle.setText("In Progress...");
        } else {
            // Pending steps use gray and an empty circle
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.gray);
            holder.statusDot.setImageResource(R.drawable.outline_circle_24); // Assuming you have this icon
            holder.statusSubtitle.setText("Awaiting Update");
        }

        holder.statusDot.setImageTintList(ColorStateList.valueOf(color));
        holder.timelineLine.setBackgroundColor(color);

        // Hide the connecting line below the last item
        if (position == steps.size() - 1) {
            holder.timelineLine.setVisibility(View.INVISIBLE);
        } else {
            holder.timelineLine.setVisibility(View.VISIBLE);
        }
    }

    // Logic to check if a specific step has passed the current status
    private boolean isStepComplete(String stepDataStatus) {
        // This logic is simplified; a real system would use indices or a proper server check.
        // For now, we assume steps are ordered correctly in the list.
        for (TimelineStep step : steps) {
            if (step.dataStatus.equals(currentStatus)) {
                return false; // Found the current step, everything after is not complete
            }
            if (step.dataStatus.equals(stepDataStatus)) {
                return true; // Found the step before the current one
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        ImageView statusDot;
        TextView statusTitle, statusSubtitle;
        View timelineLine;

        public TimelineViewHolder(View itemView) {
            super(itemView);
            statusDot = itemView.findViewById(R.id.statusDot);
            statusTitle = itemView.findViewById(R.id.statusTitle);
            statusSubtitle = itemView.findViewById(R.id.statusSubtitle);
            timelineLine = itemView.findViewById(R.id.timelineLine);
        }
    }

    // --- Data Model for the Timeline Steps ---
    public static class TimelineStep {
        public String title;
        public String dataStatus; // Matches the server status code (e.g., "WASHING")
        public String completedTimestamp; // Time when this status was completed

        public TimelineStep(String title, String dataStatus, String completedTimestamp) {
            this.title = title;
            this.dataStatus = dataStatus;
            this.completedTimestamp = completedTimestamp;
        }
    }
}