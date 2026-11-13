package com.example.myapplication;

import android.widget.EditText;
import android.widget.Spinner;

public final class OrderFormValidator {
    private OrderFormValidator() {}
    public static Order validate(long recordId, EditText number, EditText customer, EditText phone,
                                 EditText food, EditText quantity, EditText price, Spinner status) {
        EditText[] required = {number, customer, phone, food, quantity, price};
        for (EditText field : required) {
            field.setError(null);
            if (field.getText().toString().trim().isEmpty()) {
                field.setError("This field is required"); field.requestFocus(); return null;
            }
        }
        String contact = phone.getText().toString().trim();
        String digits = contact.replaceAll("[^0-9]", "");
        if (!contact.matches("\\+?[0-9 ()-]+") || digits.length() < 7 || digits.length() > 15) {
            phone.setError("Enter a valid phone number (7 to 15 digits)"); phone.requestFocus(); return null;
        }
        int count;
        try {
            count = Integer.parseInt(quantity.getText().toString().trim());
            if (count < 1 || count > 9999) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            quantity.setError("Quantity must be 1 to 9999"); quantity.requestFocus(); return null;
        }
        long cents;
        try { cents = Order.parsePrice(price.getText().toString().trim()); }
        catch (IllegalArgumentException e) {
            price.setError(e.getMessage()); price.requestFocus(); return null;
        }
        return new Order(recordId, number.getText().toString().trim(), customer.getText().toString().trim(),
                contact, food.getText().toString().trim(), count, cents, status.getSelectedItem().toString());
    }
}