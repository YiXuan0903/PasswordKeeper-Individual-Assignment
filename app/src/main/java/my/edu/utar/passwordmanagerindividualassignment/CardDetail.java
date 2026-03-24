package my.edu.utar.passwordmanagerindividualassignment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardDetail extends BottomSheetDialogFragment {

    private PasswordEntry entry;
    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean passwordVisible = false;
    private boolean pinVisible = false;

    public static CardDetail newInstance(PasswordEntry entry) {
        CardDetail sheet = new CardDetail();
        sheet.entry = entry;
        return sheet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_detail, container, false);
        db = AppDatabase.getInstance(requireContext());

        ((TextView) view.findViewById(R.id.bsSiteName)).setText(entry.getSiteName());
        ((TextView) view.findViewById(R.id.bsUsername)).setText(entry.getUsername());

        TextView bsPassword = view.findViewById(R.id.bsPassword);
        bsPassword.setText("••••••••");

        TextView bsExtraPin = view.findViewById(R.id.bsExtraPin);
        if (entry.getExtraPin() != null && !entry.getExtraPin().isEmpty()) {
            view.findViewById(R.id.pinRow).setVisibility(View.VISIBLE);
            bsExtraPin.setText("••••••••");

            view.findViewById(R.id.togglePin).setOnClickListener(v -> {
                pinVisible = !pinVisible;
                if (pinVisible) {
                    bsExtraPin.setText(EncryptionUtil.decrypt(entry.getExtraPin()));
                    ((ImageButton) v).setImageResource(R.drawable.outline_visibility_off_24);
                } else {
                    bsExtraPin.setText("••••••••");
                    ((ImageButton) v).setImageResource(R.drawable.outline_visibility_24);
                }
            });
        }

        if (entry.getChallengeQuestion() != null && !entry.getChallengeQuestion().isEmpty()) {
            view.findViewById(R.id.challengeRow).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.bsChallengeQA)).setText(
                    "Q: " + entry.getChallengeQuestion() + "\nA: " + entry.getChallengeAnswer());
        }

        view.findViewById(R.id.togglePassword).setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                bsPassword.setText(EncryptionUtil.decrypt(entry.getPassword()));
                ((ImageButton) v).setImageResource(R.drawable.outline_visibility_off_24);
            } else {
                bsPassword.setText("••••••••");
                ((ImageButton) v).setImageResource(R.drawable.outline_visibility_24);
            }
        });

        view.findViewById(R.id.copyUsername).setOnClickListener(v ->
                copyToClipboard("Username", entry.getUsername()));
        view.findViewById(R.id.copyPassword).setOnClickListener(v ->
                copyToClipboard("Password", EncryptionUtil.decrypt(entry.getPassword())));
        view.findViewById(R.id.copyPin).setOnClickListener(v ->
                copyToClipboard("PIN", EncryptionUtil.decrypt(entry.getExtraPin())));

        view.findViewById(R.id.bsDeleteBtn).setOnClickListener(v -> {
            executor.execute(() -> {
                db.passwordDao().delete(entry);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            });
        });

        view.findViewById(R.id.bsEditBtn).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditActivity.class);
            intent.putExtra("entry_id", entry.getId());
            startActivity(intent);
            dismiss();
        });

        return view;
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
        Toast.makeText(getContext(), label + " copied!", Toast.LENGTH_SHORT).show();
    }
}