package my.edu.utar.passwordmanagerindividualassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PasswordAdapter adapter;
    private List<PasswordEntry> fullList = new ArrayList<>();
    private List<PasswordEntry> filteredList = new ArrayList<>();
    private AppDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean sortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PasswordAdapter(filteredList, entry -> {
            CardDetail sheet = CardDetail.newInstance(entry);
            sheet.show(getSupportFragmentManager(), "detail");
        });
        recyclerView.setAdapter(adapter);

        db.passwordDao().getAllPasswords().observe(this, entries -> {
            fullList.clear();
            fullList.addAll(entries);
            filteredList.clear();
            filteredList.addAll(entries);
            sortList();

            findViewById(R.id.emptyState).setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(entries.isEmpty() ? View.GONE : View.VISIBLE);
        });

        findViewById(R.id.sortButton).setOnClickListener(v -> {
            sortAscending = !sortAscending;
            sortList();
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, fullList);
                return true;
            }
        });

        findViewById(R.id.add_new_acc_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditActivity.class);
            startActivity(intent);
        });
    }

    private void sortList() {
        if (sortAscending) {
            filteredList.sort((a, b) -> a.getSiteName().compareToIgnoreCase(b.getSiteName()));
        } else {
            filteredList.sort((a, b) -> b.getSiteName().compareToIgnoreCase(a.getSiteName()));
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, sortAscending ? "Sorted A → Z" : "Sorted Z → A", Toast.LENGTH_SHORT).show();
    }
}