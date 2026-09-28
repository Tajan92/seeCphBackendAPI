package app.dao;

import java.util.List;

public interface IDAO<T, I> {
    public T create(T t);
    public T readById(I id);
    public List<T> readAll();
    public T update(T t);
    public boolean delete(T t);
}
