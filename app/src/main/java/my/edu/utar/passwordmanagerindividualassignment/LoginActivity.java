package my.edu.utar.passwordmanagerindividualassignment;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.security.MessageDigest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPin, etConfirmPin;
    private TextInputLayout confirmPinLayout;
    private TextView tvSubtitle;
    private Button btnLogin;
    private SharedPreferences prefs;
    private boolean isSetupMode = false;

    private static final String PREFS_NAME = "password_manager_prefs";
    private static final String KEY_PIN = "app_pin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        etPin = findViewById(R.id.etPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        confirmPinLayout = findViewById(R.id.confirmPinLayout);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        btnLogin = findViewById(R.id.btnLogin);

        String savedPin = prefs.getString(KEY_PIN, null);
        if (savedPin == null) {
            isSetupMode = true;
            tvSubtitle.setText("Set a PIN to protect your passwords");
            confirmPinLayout.setVisibility(View.VISIBLE);
            btnLogin.setText("Set PIN");
        }

        btnLogin.setOnClickListener(v -> {
            if (isSetupMode) {
                setupPin();
            } else {
                verifyPin();
            }
        });
    }

    private void setupPin() {
        String pin = etPin.getText().toString().trim();
        String confirmPin = etConfirmPin.getText().toString().trim();

        if (pin.length() < 4) {
            Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pin.equals(confirmPin)) {
            Toast.makeText(this, "PINs do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit().putString(KEY_PIN, hashPin(pin)).apply();
        Toast.makeText(this, "PIN set successfully!", Toast.LENGTH_SHORT).show();
        goToMain();
    }

    private void verifyPin() {
        String pin = etPin.getText().toString().trim();
        String savedPin = prefs.getString(KEY_PIN, "");

        if (hashPin(pin).equals(savedPin)) {
            goToMain();
        } else {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            etPin.setText("");
        }
    }

    private String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return pin;
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}