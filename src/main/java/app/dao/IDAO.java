package app.dao;

import java.util.Set;

public interface IDAO<T, I> {
    public T create(T t);
    public T readById(I id);
    public Set<T> readAll();
    public T update(T t);
    public boolean delete(T t);
}
