package my.edu.utar.passwordmanagerindividualassignment;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface PasswordDao {

    @Insert
    void insert(PasswordEntry entry);

    @Update
    void update(PasswordEntry entry);

    @Delete
    void delete(PasswordEntry entry);

    @Query("SELECT * FROM password_table ORDER BY siteName ASC")
    LiveData<List<PasswordEntry>> getAllPasswords();

    @Query("SELECT * FROM password_table WHERE siteName LIKE '%' || :query || '%'")
    LiveData<List<PasswordEntry>> searchByName(String query);

    @Query("SELECT * FROM password_table WHERE id = :id LIMIT 1")
    PasswordEntry getById(int id);
}
