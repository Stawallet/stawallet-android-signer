package io.stawallet.signer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(user: User)

    @Query("DELETE FROM User WHERE email NOT IN (:whitelist)")
    fun deleteAllBut(whitelist: List<String>)

    @Query("DELETE FROM User")
    fun deleteAll()

    @Query("SELECT * FROM User WHERE email = :email")
    fun loadByEmail(email: String): LiveData<User>

    @Query("SELECT * FROM User LIMIT 1")
    fun loadTheOnlyOne(): LiveData<User>
}
