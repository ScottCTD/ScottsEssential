package xyz.scottc.scessential.utils;

import java.util.ArrayList;
import java.util.List;

public class PageableList<T> {

    private List<T> all;

    private List<List<T>> pages;

    private int index = 0;

    public PageableList(List<T> all, int onePage, int totalPages) {
        this.all = all;
        this.init(onePage, totalPages);
    }

    public void nextPage() {
        if (this.index < this.pages.size() - 1) {
            this.index++;
        }
    }

    public void prevPage() {
        if (this.index > 0) {
            this.index--;
        }
    }

    public List<T> getCurrentPage() {
        return this.pages.get(this.index);
    }

    private void init(int onePage, int totalPages) {
        this.pages = new ArrayList<>(totalPages);
        int index = 0;
        List<T> temp = new ArrayList<>(onePage);
        for (T t : this.all) {
            if (index == onePage) {
                index = 0;
                this.pages.add(temp);
                temp = new ArrayList<>(onePage);
            }
            temp.add(t);
            index++;
        }
        this.pages.add(temp);
    }

    public List<T> getAll() {
        return all;
    }

    public void setAll(List<T> all, int onePage, int totalPages) {
        this.all = all;
        this.init(onePage, totalPages);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotalPages() {
        return this.pages.size();
    }
}
