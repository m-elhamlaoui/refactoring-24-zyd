import java.util.List;

public interface AbstractDAO<T> {
    T get(int id);
    List<T> getAll();
    void update(T t);
    void delete(int id);
}