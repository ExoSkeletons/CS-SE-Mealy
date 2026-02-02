package com.eanie.mealy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.eanie.mealy.data.KitchenItem;
import com.eanie.mealy.data.Quantity;
import com.eanie.mealy.data.UnitType;
import com.eanie.mealy.data.Quantifier;

import org.junit.Test;

import java.lang.reflect.Field;

public class UserItemsViewModelTest {

    // ===== Helper: יצירת Quantity בלי לקרוא לבנאים שמפעילים normalize =====
    private static Quantity makeQuantityRaw(double amount, UnitType unitType, Quantifier quantifier) {
        try {
            Quantity q = new Quantity(); // בנאי ריק (לא מפעיל setAmount)
            setField(q, "amount", amount);
            setField(q, "unitType", unitType);
            setField(q, "quantifier", quantifier);
            return q;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Quantity via reflection", e);
        }
    }

    private static void setQuantityAmountRaw(Quantity q, double amount) {
        try {
            setField(q, "amount", amount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Quantity.amount via reflection", e);
        }
    }

    private static void setField(Object obj, String fieldName, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private static void setField(Object obj, String fieldName, double value) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.setDouble(obj, value);
    }


    @Test
    public void test1_quantityToString_formatsCount() {

        Quantity q = makeQuantityRaw(5, UnitType.COUNT, Quantifier.NONE);

        String result = q.toString();

        assertEquals("5 ", result);
    }


    @Test
    public void test2_kitchenItemClone_createsDeepCopy() {

        Quantity originalQ = makeQuantityRaw(2, UnitType.GRAMS, Quantifier.NONE);
        KitchenItem original = new KitchenItem("ing_sugar", originalQ);

        KitchenItem copy = original.clone();
        setQuantityAmountRaw(copy.getQuantity(), 5);

        assertEquals(2.0, original.getQuantity().getAmount(), 0.001);
        assertEquals(5.0, copy.getQuantity().getAmount(), 0.001);

        assertNotSame(original.getQuantity(), copy.getQuantity());
    }


    @Test
    public void test3_kitchenItem_usesMockedQuantity_getAmount() {

        Quantity mockQuantity = mock(Quantity.class);
        when(mockQuantity.getAmount()).thenReturn(3.0);

        KitchenItem item = new KitchenItem("ing_apple", mockQuantity);

        double amount = item.getQuantity().getAmount();

        assertEquals(3.0, amount, 0.001);
        verify(mockQuantity, times(1)).getAmount();
    }
}
