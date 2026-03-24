package my.edu.utar.passwordmanagerindividualassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

    private List<PasswordEntry> passwordList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PasswordEntry entry);
    }

    public PasswordAdapter(List<PasswordEntry> passwordList, OnItemClickListener listener) {
        this.passwordList = passwordList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_password, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PasswordEntry entry = passwordList.get(position);
        holder.siteName.setText(entry.getSiteName());
        holder.username.setText(entry.getUsername());

        String firstLetter = entry.getSiteName().isEmpty() ? "?"
                : String.valueOf(entry.getSiteName().charAt(0)).toUpperCase();
        holder.siteIcon.setText(firstLetter);
        holder.siteIcon.setBackgroundTintList(
                ColorStateList.valueOf(getColor(entry.getSiteName())));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(entry));
    }

    private int getColor(String name) {
        int[] colors = {
                0xFF1976D2, 0xFF388E3C, 0xFFD32F2F, 0xFF7B1FA2,
                0xFFF57C00, 0xFF0097A7, 0xFF5D4037, 0xFF455A64
        };
        return colors[Math.abs(name.hashCode()) % colors.length];
    }

    @Override
    public int getItemCount() { return passwordList.size(); }

    public void filter(String query, List<PasswordEntry> fullList) {
        passwordList.clear();
        if (query.isEmpty()) {
            passwordList.addAll(fullList);
        } else {
            String lower = query.toLowerCase();
            for (PasswordEntry e : fullList) {
                if (e.getSiteName().toLowerCase().contains(lower)) {
                    passwordList.add(e);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteName, username, siteIcon;

        ViewHolder(View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.siteName);
            username = itemView.findViewById(R.id.username);
            siteIcon = itemView.findViewById(R.id.siteIcon);
        }
    }
}