package my.edu.utar.passwordmanagerindividualassignment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditActivity extends AppCompatActivity {

    private TextInputEditText etSiteName, etUsername, etPassword;
    private TextInputEditText etExtraPin, etChallengeQuestion, etChallengeAnswer;
    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private PasswordEntry existingEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = AppDatabase.getInstance(this);

        etSiteName = findViewById(R.id.etSiteName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etExtraPin = findViewById(R.id.etExtraPin);
        etChallengeQuestion = findViewById(R.id.etChallengeQuestion);
        etChallengeAnswer = findViewById(R.id.etChallengeAnswer);
        TextView pageTitle = findViewById(R.id.pageTitle);
        ImageView deleteButton = findViewById(R.id.deleteButton);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        int entryId = getIntent().getIntExtra("entry_id", -1);
        if (entryId != -1) {
            pageTitle.setText("Edit Entry");
            deleteButton.setVisibility(View.VISIBLE);

            executor.execute(() -> {
                existingEntry = db.passwordDao().getById(entryId);
                runOnUiThread(() -> {
                    etSiteName.setText(existingEntry.getSiteName());
                    etUsername.setText(existingEntry.getUsername());
                    etPassword.setText(EncryptionUtil.decrypt(existingEntry.getPassword()));
                    etExtraPin.setText(EncryptionUtil.decrypt(existingEntry.getExtraPin()));
                    etChallengeQuestion.setText(existingEntry.getChallengeQuestion());
                    etChallengeAnswer.setText(existingEntry.getChallengeAnswer());
                });
            });

            deleteButton.setOnClickListener(v -> {
                executor.execute(() -> {
                    db.passwordDao().delete(existingEntry);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            });

        } else {
            pageTitle.setText("Add Entry");
        }

        findViewById(R.id.saveButton).setOnClickListener(v -> saveEntry());
    }

    private void saveEntry() {
        String siteName = etSiteName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (siteName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Site Name, Username and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String extraPin = etExtraPin.getText().toString().trim();
        String challengeQ = etChallengeQuestion.getText().toString().trim();
        String challengeA = etChallengeAnswer.getText().toString().trim();

        executor.execute(() -> {
            if (existingEntry == null) {
                PasswordEntry newEntry = new PasswordEntry(0, siteName, username,
                        EncryptionUtil.encrypt(password),
                        EncryptionUtil.encrypt(extraPin),
                        challengeQ, challengeA);
                db.passwordDao().insert(newEntry);
            } else {
                existingEntry.setSiteName(siteName);
                existingEntry.setUsername(username);
                existingEntry.setPassword(EncryptionUtil.encrypt(password));
                existingEntry.setExtraPin(EncryptionUtil.encrypt(extraPin));
                existingEntry.setChallengeQuestion(challengeQ);
                existingEntry.setChallengeAnswer(challengeA);
                db.passwordDao().update(existingEntry);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}