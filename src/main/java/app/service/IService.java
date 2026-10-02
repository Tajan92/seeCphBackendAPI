package app.service;

import java.util.List;

public interface IService<T, R> {
    public R create(T input);
    public R updateById(int id, T input);
    public R getById(int id);
    public List<R> getAll();
    public boolean deleteById(int id);

}
