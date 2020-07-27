package org.tinywind.server.util.page;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class PageNavigation {
    private final int first;
    private final int[] items;
    private final int last;
    private final int next;
    private final int previous;
    private final int page;
    private final int rowsPerPage;

    public PageNavigation(int page, int totalCount, int numberOfRowsPerPage, int numberOfPagesPerNavigation) {
        page--;

        final int first = 0;
        final int last = ((int) Math.ceil(totalCount * 1.0 / numberOfRowsPerPage));
        final int begin = page - (page % numberOfPagesPerNavigation);
        final int end = Math.min(begin + numberOfPagesPerNavigation, last);

        final int max = Math.max(end, 1);
        final int size = max - begin;
        final int[] items = new int[Math.max(size, 0)];

        for (int i = begin; i < max; i++)
            items[i - begin] = i + 1;

        this.items = items;
        this.previous = Math.max(page - 1, first) + 1;
        this.next = Math.max(Math.min(last - 1, page + 1), first) + 1;
        this.last = Math.max(last - 1, first) + 1;
        this.page = page + 1;
        this.rowsPerPage = numberOfRowsPerPage;
        this.first = first + 1;
    }

    public boolean contains(int i) {
        for (int item : items)
            if (item == i)
                return true;
        return false;
    }

    public int getFirst() {
        return first;
    }

    public List<Integer> getItems() {
        final List<Integer> list = new ArrayList<>();
        for (int e : items)
            list.add(e);

        return list;
    }

    public int getLast() {
        return last;
    }

    public int getNext() {
        return next;
    }

    public int getPrevious() {
        return previous;
    }

    public int getPage() {
        return page;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }
}
