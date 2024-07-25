package com.example.budgettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_EXPENSES = "expenses";
    public static final String TABLE_INCOME = "income";

    // Columns for expenses table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";
    public static final String COLUMN_EXPENSE_DESCRIPTION = "description";
    public static final String COLUMN_EXPENSE_DATE = "date";

    // Columns for income table
    public static final String COLUMN_INCOME_AMOUNT = "amount";
    public static final String COLUMN_INCOME_DESCRIPTION = "description";
    public static final String COLUMN_INCOME_DATE = "date";

    // Create table SQL statements
    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EXPENSE_AMOUNT + " REAL, " +
            COLUMN_EXPENSE_DESCRIPTION + " TEXT, " +
            COLUMN_EXPENSE_DATE + " TEXT" + ");";

    private static final String CREATE_TABLE_INCOME = "CREATE TABLE " + TABLE_INCOME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_INCOME_AMOUNT + " REAL, " +
            COLUMN_INCOME_DESCRIPTION + " TEXT, " +
            COLUMN_INCOME_DATE + " TEXT" + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_INCOME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        onCreate(db);
    }

    // Method to add an expense
    public void addExpense(double amount, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPENSE_AMOUNT, amount);
        values.put(COLUMN_EXPENSE_DESCRIPTION, description);
        values.put(COLUMN_EXPENSE_DATE, date);
        db.insert(TABLE_EXPENSES, null, values);
    }

    // Method to add income
    public void addIncome(double amount, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME_AMOUNT, amount);
        values.put(COLUMN_INCOME_DESCRIPTION, description);
        values.put(COLUMN_INCOME_DATE, date);
        db.insert(TABLE_INCOME, null, values);
    }

    // Method to calculate the total expenses
    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES, null);
        if (cursor.moveToFirst()) {
            double totalExpenses = cursor.getDouble(0);
            cursor.close();
            return totalExpenses;
        } else {
            cursor.close();
            return 0;
        }
    }

    // Method to calculate the total income
    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_INCOME_AMOUNT + ") FROM " + TABLE_INCOME, null);
        if (cursor.moveToFirst()) {
            double totalIncome = cursor.getDouble(0);
            cursor.close();
            return totalIncome;
        } else {
            cursor.close();
            return 0;
        }
    }

    // Method to calculate the remaining balance
    public double getRemainingBalance() {
        double totalIncome = getTotalIncome();
        double totalExpenses = getTotalExpenses();
        return totalIncome - totalExpenses;
    }

    // method to get all expenses
    public Cursor getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EXPENSES, null, null, null, null, null, COLUMN_EXPENSE_DATE + " DESC");
    }

    // Method to get all income

    public Cursor getAllIncomes(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_INCOME, null, null, null, null, null, COLUMN_INCOME_DATE + " DESC");
    }

}
