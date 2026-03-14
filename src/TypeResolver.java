import java.util.ArrayList;
import java.util.List;

public class TypeResolver {

    private static final String TYPE_MIXED = "mixed";
    private static final String TAG_VAR = "var";

    private PhpType parseTypeString(String typeString) {
        if (typeString == null) {
            return null;
        }

        if (typeString.contains("|")) {
            final String[] unionTypes = typeString.split("\\|");
            List<PhpType> typesList = new ArrayList<>();
            for (String ut : unionTypes) {
                typesList.add(TypeFactory.createType(ut));
            }

            if (typesList.isEmpty()) {
                return null;
            } else if (typesList.size() == 1) {
                return typesList.getFirst();    /* "int|" is simply a single type */
            } else {
                return TypeFactory.createUnionType(typesList);
            }
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
        PhpType fallbackGenericType = null;

        for (DocTag tag : tags) {
            String value = tag.getValue();
            if (value == null) {
                continue;
            }
            value = value.trim();
            if (value.isEmpty()) {
                continue;
            }

            final String[] parts = value.split("\\s+");
            final String typeString = parts[0];
            final String docVarName = parts.length > 1 ? parts[1] : null;

            if (docVarName != null) {
                if (docVarName.equals(targetVarName)) {
                    return parseTypeString(typeString);     /* Extact match */
                }
            } else {
                if (fallbackGenericType == null) {
                    fallbackGenericType = parseTypeString(typeString);
                }
            }
        }

        if (fallbackGenericType != null) {
            return fallbackGenericType;    /* No exact match was found, however unnamed type exists */
        }

        return TypeFactory.createType(TYPE_MIXED);
    }

}
