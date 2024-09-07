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
            tail = node;
            size++;

            return true;
        }

        tail.next = node;
        tail = node;
        size++;

        return true;
    }

    public T getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return head.element;
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current.element;
    }

    public T remove() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        T deletedElement = head.element;

        if (size == 1) {
            tail = null;
        }

        head = head.next;
        size--;

        return deletedElement;
    }

    public T remove(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
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

        T deletedElement = current.element;

        if (current == tail) {
            tail = prev;
        }

        prev.next = current.next;
        size--;

        return deletedElement;
    }

    public boolean contains(T element) {
        if (size == 0) {
            return false;
        }

        Node<T> current = head;

        while (current.next != null) {
            if (current.element.equals(element)) {
                return true;
            }

            current = current.next;
        }

        return current.element.equals(element);
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

}

class Node<T> {

    T element;

    Node<T> next;

    public Node(T element) {
        this.element = element;
        this.next = null;
    }

}