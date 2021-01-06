package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.requests.CreateUserRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TestUtils {

    private static AtomicLong atomicLong = new AtomicLong(1L);

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }
            field.set(target, toInject);
            if (wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createMockedUser() {
        User user = new User();
        user.setId(atomicLong.getAndIncrement());
        user.setUsername("user");
        user.setPassword("password");
        return user;
    }

    public static Item createMockedItem() {
        Item item = new Item();
        item.setId(atomicLong.getAndIncrement());
        item.setDescription("description");
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(0.55 + atomicLong.get()));
        return item;
    }

    public static Cart createMockedCart(User user, Item item) {
        List<Item> items = new ArrayList<>();
        items.add(createMockedItem());

        Cart cart = new Cart();
        cart.setId(atomicLong.getAndIncrement());
        cart.setUser(user);
        user.setCart(cart);
        cart.setItems(items);
        if (item != null) {
            cart.addItem(item);
        }

        return cart;
    }

    public static UserOrder createMockedUserOrder(User user) {
        Item item = createMockedItem();
        List<Item> items = new ArrayList<>();
        items.add(item);

        UserOrder order = new UserOrder();
        order.setId(atomicLong.getAndIncrement());
        order.setUser(user);
        order.setItems(items);
        order.setTotal(item.getPrice());

        return order;
    }
}
