import java.util.ArrayList;
import java.util.List;

public class TypeResolver {

    private static final String TYPE_MIXED = "mixed";
    private static final String TAG_VAR = "var";

    private PhpType parseSingleTag(String value, String targetVarName) {
        if (value == null) {
            return null;
        }

        value = value.trim();
        if (value.isEmpty()) {
            return null;
        }

        final String[] parts = value.split("\\s+");
        final String typeString = parts[0];
        final String docVarName = parts.length > 1 ? parts[1] : null;

        if (docVarName != null && !docVarName.equals(targetVarName)) {
            return null;
        }

        if (typeString.contains("|")) {
            final String[] unionTypes = typeString.split("\\|");
            List<PhpType> typesList = new ArrayList<>();
            for (String ut : unionTypes) {
                if (ut.isEmpty()) {
                    continue;       /* Edge-case: @var |  */
                }
                typesList.add(TypeFactory.createType(ut));
            }

            if (typesList.isEmpty()) {
                return null;
            }
            return TypeFactory.createUnionType(typesList);
        }
        return TypeFactory.createType(typeString);
    }

    public PhpType inferTypeFromDoc(PhpVariable variable) {
        if (variable == null) {
            return TypeFactory.createType(TYPE_MIXED);
        }

        final PhpDocBlock docBlock = variable.getDocBlock();
        if (docBlock == null) {
            return TypeFactory.createType(TYPE_MIXED);
        }

        final List<DocTag> tags = docBlock.getTagsByName(TAG_VAR);
        if (tags == null || tags.isEmpty()) {
            return TypeFactory.createType(TYPE_MIXED);
        }

        final String targetVarName = variable.getName();
        for (DocTag tag : tags) {
            final PhpType resolvedType = parseSingleTag(tag.getValue(), targetVarName);
            if (resolvedType != null) {
                return resolvedType;
            }
        }

        return TypeFactory.createType(TYPE_MIXED);
    }

}
