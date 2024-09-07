package org.example.customLinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {

    private CustomLinkedList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    public void testAdd() {
        assertTrue(list.add(1));
        assertTrue(list.add(2));
        assertTrue(list.add(3));
        assertEquals(3, list.size());
    }

    @Test
    public void testGetFirst() {
        list.add(1);
        list.add(2);

        assertEquals(1, list.getFirst());
    }

    @Test
    public void testGetFirstEmptyList() {
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
    }

    @Test
    public void testGet() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    public void testGetOutOfBounds() {
        list.add(1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    public void testRemove() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.remove());
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
    }

    @Test
    public void testRemoveEmptyList() {
        assertThrows(NoSuchElementException.class, () -> list.remove());
    }

    @Test
    public void testRemoveAtIndex() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(2, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    public void testRemoveAtIndexOutOfBounds() {
        list.add(1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    }

    @Test
    public void testContains() {
        list.add(1);
        list.add(7);
        list.add(3);

        assertTrue(list.contains(1));
        assertTrue(list.contains(7));
        assertTrue(list.contains(3));
        assertFalse(list.contains(4));
    }

    @Test
    public void testAddAll() {
        assertTrue(list.addAll(Arrays.asList(1, 2, 3)));

        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    public void testAddAllEmptyCollection() {
        assertFalse(list.addAll(Arrays.asList()));
        assertEquals(0, list.size());
    }

    @Test
    public void testAddAllCustomLinkedList() {
        CustomLinkedList<Integer> otherList = new CustomLinkedList<>();
        otherList.add(4);
        otherList.add(5);
        otherList.add(6);

        list.add(1);
        list.add(2);
        list.add(3);

        assertTrue(list.addAll(otherList));
        assertEquals(6, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
        assertEquals(5, list.get(4));
        assertEquals(6, list.get(5));
        assertEquals(3, otherList.size());
    }

    @Test
    public void testAddAllEmptyCustomLinkedList() {
        CustomLinkedList<Integer> otherList = new CustomLinkedList<>();

        assertFalse(list.addAll(otherList));
        assertEquals(0, list.size());
    }

    @Test
    public void testSize() {
        assertEquals(0, list.size());

        list.add(1);
        assertEquals(1, list.size());

        list.add(2);
        assertEquals(2, list.size());

        list.remove(1);
        assertEquals(1, list.size());

        list.remove();
        assertEquals(0, list.size());
    }

}