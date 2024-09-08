package org.example.customLinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {

    private CustomLinkedList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    @DisplayName("Test adding elements to the list")
    public void testAddElementsToList() {
        assertTrue(list.add(1));
        assertTrue(list.contains(1));

        assertTrue(list.add(2));
        assertTrue(list.contains(2));

        assertTrue(list.add(3));
        assertTrue(list.contains(3));

        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("Test getting the first element")
    public void testGetFirstElementsFromList() {
        list.add(1);
        list.add(2);

        assertEquals(1, list.getFirst());
    }

    @Test
    @DisplayName("Return NoSuchElementException when getting the first element from an empty list")
    public void testGetFirstElementFromEmptyList() {
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
    }

    @Test
    @DisplayName("Test getting elements by index")
    public void testGetElementByIndex() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    @DisplayName("Return IndexOutOfBoundsException when getting elements by out-of-bounds index")
    public void testGetElementsByOutOfBoundsIndex() {
        list.add(1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    @DisplayName("Test removing the first element")
    public void testRemoveFirstElementFromList() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.remove());
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
    }

    @Test
    @DisplayName("Return NoSuchElementException when removing the first element from an empty list")
    public void testRemoveFirstElementFromEmptyList() {
        assertThrows(NoSuchElementException.class, () -> list.remove());
    }

    @Test
    @DisplayName("Test removing an element by index")
    public void testRemoveElementByIndex() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(2, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    @DisplayName("Return IndexOutOfBoundsException when removing an element by out-of-bounds index")
    public void testRemoveElementByOutOfBoundsIndex() {
        list.add(1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    }

    @Test
    @DisplayName("Test checking if the list contains an element")
    public void testContainsElementInCustomLinkedList() {
        list.add(1);
        list.add(7);
        list.add(3);

        assertTrue(list.contains(1));
        assertTrue(list.contains(7));
        assertTrue(list.contains(3));
        assertFalse(list.contains(4));
    }

    @Test
    @DisplayName("Test adding all elements from a collection")
    public void testAddAllElementsToCustomLinkedListFromCollection() {
        assertTrue(list.addAll(Arrays.asList(1, 2, 3)));

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));

        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("Return false when adding all elements from an empty collection")
    public void testAddAllElementsToCustomLinkedListFromEmptyCollection() {
        assertFalse(list.addAll(List.of()));
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("Test adding all elements from another CustomLinkedList")
    public void testAddAllElementToCustomLinkedListFromAnotherCustomLinkedList() {
        CustomLinkedList<Integer> otherList = new CustomLinkedList<>();
        otherList.add(4);
        otherList.add(5);
        otherList.add(6);

        list.add(1);
        list.add(2);
        list.add(3);

        assertTrue(list.addAll(otherList));

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
        assertEquals(5, list.get(4));
        assertEquals(6, list.get(5));

        assertEquals(6, list.size());
        assertEquals(3, otherList.size());
    }

    @Test
    @DisplayName("Return false when adding all elements from an empty CustomLinkedList")
    public void testAddAllElementsToCustomLinkedListFromEmptyCustomLinkedList() {
        CustomLinkedList<Integer> otherList = new CustomLinkedList<>();

        assertFalse(list.addAll(otherList));
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("Test getting the size of the list")
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