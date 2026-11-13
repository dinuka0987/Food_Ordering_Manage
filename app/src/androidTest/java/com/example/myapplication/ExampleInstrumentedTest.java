package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteConstraintException;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication", appContext.getPackageName());
    }

    @Test public void upgradesWithoutTurningStudentsIntoOrdersAndPersistsOrderChanges() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String databaseName = "food_order_migration_test.db";
        context.deleteDatabase(databaseName);
        try {
            try (SQLiteDatabase legacy = context.openOrCreateDatabase(databaseName, 0, null)) {
                legacy.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY, student_id TEXT, name TEXT, email TEXT, course TEXT, marks INTEGER, grade TEXT)");
                legacy.execSQL("INSERT INTO students VALUES (1, 'S001', 'Legacy student', 'student@example.com', 'IT', 80, 'A')");
                legacy.setVersion(1);
            }
            long id;
            try (DatabaseHelper helper = new DatabaseHelper(context, databaseName)) {
                assertTrue(helper.getAllOrders().isEmpty());
                try (android.database.Cursor c = helper.getReadableDatabase().rawQuery("SELECT name FROM students", null)) {
                    assertTrue(c.moveToFirst());
                    assertEquals("Legacy student", c.getString(0));
                }
                Order order = new Order(-1, "ORD001", "Customer", "0771234567", "Rice", 3, 85050, "Pending");
                id = helper.insertOrder(order);
                assertTrue(id > 0);
                assertEquals(255150L, helper.getOrder(id).getTotalCents());
                try {
                    helper.insertOrder(new Order(-1, "ord001", "Other", "0771234567", "Rice", 1, 100, "Pending"));
                    fail("Duplicate order numbers must be rejected ignoring case");
                } catch (SQLiteConstraintException expected) { }
                assertEquals(1, helper.updateOrder(new Order(id, "ORD001", "Customer", "0771234567", "Rice", 2, 85050, "Ready")));
            }
            try (DatabaseHelper reopened = new DatabaseHelper(context, databaseName)) {
                assertEquals("Ready", reopened.getOrder(id).getStatus());
                assertEquals(170100L, reopened.getOrder(id).getTotalCents());
                assertEquals(1, reopened.deleteOrder(id));
                assertNull(reopened.getOrder(id));
            }
        } finally { context.deleteDatabase(databaseName); }
    }
}
