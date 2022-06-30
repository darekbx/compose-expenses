package com.darekbx.expenses.di
import android.content.Context
import androidx.room.Room
import com.darekbx.expenses.repository.database.AppDatabase
import com.darekbx.expenses.repository.database.ExpenseDao
import com.darekbx.expenses.repository.database.PaymentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ExpensesModule {

    @Provides
    fun provideExpensesDao(appDatabase: AppDatabase): ExpenseDao {
        return appDatabase.expensedao()
    }

    @Provides
    fun providePaymentDao(appDatabase: AppDatabase): PaymentDao {
        return appDatabase.paymentdao()
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder<AppDatabase>(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        ).build()
    }
}
