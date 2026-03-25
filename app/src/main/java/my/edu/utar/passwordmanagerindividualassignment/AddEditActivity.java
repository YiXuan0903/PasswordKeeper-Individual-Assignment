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

    private TextInputEditText siteName, username, password;
    private TextInputEditText extraPIN, challengeQuestion, challengeAnswer;
    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private PasswordEntry existingEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = AppDatabase.getInstance(this);

        siteName = findViewById(R.id.siteName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        extraPIN = findViewById(R.id.extraPIN);
        challengeQuestion = findViewById(R.id.challengeQuestion);
        challengeAnswer = findViewById(R.id.challengeAnswer);
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
                    siteName.setText(existingEntry.getSiteName());
                    username.setText(existingEntry.getUsername());
                    password.setText(EncryptionUtil.decrypt(existingEntry.getPassword()));
                    extraPIN.setText(EncryptionUtil.decrypt(existingEntry.getExtraPin()));
                    challengeQuestion.setText(existingEntry.getChallengeQuestion());
                    challengeAnswer.setText(existingEntry.getChallengeAnswer());
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
        String sitenameInput = siteName.getText().toString().trim();
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (sitenameInput.isEmpty() || usernameInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Site Name, Username and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String extraPin = extraPIN.getText().toString().trim();
        String challengeQ = challengeQuestion.getText().toString().trim();
        String challengeA = challengeAnswer.getText().toString().trim();

        executor.execute(() -> {
            if (existingEntry == null) {
                PasswordEntry newEntry = new PasswordEntry(0, sitenameInput, usernameInput,
                        EncryptionUtil.encrypt(passwordInput),
                        EncryptionUtil.encrypt(extraPin),
                        challengeQ, challengeA);
                db.passwordDao().insert(newEntry);
            } else {
                existingEntry.setSiteName(sitenameInput);
                existingEntry.setUsername(usernameInput);
                existingEntry.setPassword(EncryptionUtil.encrypt(passwordInput));
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