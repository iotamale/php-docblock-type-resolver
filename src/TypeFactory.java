import java.util.List;

public abstract class TypeFactory {
    public static PhpType createType(String typeName) {
        return null;
    }

    public static UnionType createUnionType(List<PhpType> types) {
        return null;
    }
}
