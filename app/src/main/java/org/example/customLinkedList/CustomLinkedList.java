package org.example.customLinkedList;

import java.util.Collection;
import java.util.NoSuchElementException;

public class CustomLinkedList<T> {

    private Node<T> head;

    private Node<T> tail;

    private int size;

    public CustomLinkedList() {
        head = tail = null;
        size = 0;
    }

    public boolean add(T element) {
        Node<T> node = new Node<>(element);

        if (size == 0) {
            head = node;
        } else {
            tail.next = node;
        }

        tail = node;
        size++;

        return true;
    }

    public T getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("No elements in list, actual size is 0");
        }

        return head.element;
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index goes beyond the boundaries of the list");
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current.element;
    }

    public T remove() {
        if (size == 0) {
            throw new NoSuchElementException("No elements in list, actual size is 0");
        }

        if (size == 1) {
            tail = null;
        }

        T deletedElement = head.element;
        head = head.next;
        size--;

        return deletedElement;
    }

    public T remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index goes beyond the boundaries of the list");
        }

        if (index == 0) {
            return remove();
        }

        Node<T> prev = null;
        Node<T> current = head;

        for (int i = 0; i < index; i++) {
            prev = current;
            current = current.next;
        }

        if (current == tail) {
            tail = prev;
        }

        T deletedElement = current.element;
        prev.next = current.next;
        size--;

        return deletedElement;
    }

    public boolean contains(T element) {
        if (size == 0) {
            return false;
        }

        Node<T> current = head;

        do {
            if (current.element.equals(element)) {
                return true;
            }

            current = current.next;
        } while (current != null);

        return false;
    }

    public boolean addAll(Collection<? extends T> collection) {
        if (collection.isEmpty()) {
            return false;
        }

        for (T element : collection) {
            add(element);
        }

        return true;
    }

    public boolean addAll(CustomLinkedList<T> list) {
        if (list.size() == 0) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            add(list.get(i));
        }

        return true;
    }

    public int size() {
        return size;
    }

    private static class Node<T> {

        T element;

        Node<T> next;

        public Node(T element) {
            this.element = element;
            this.next = null;
        }
    }
}