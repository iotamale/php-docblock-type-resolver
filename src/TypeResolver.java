import java.util.ArrayList;
import java.util.List;

public class TypeResolver {

    private static final String TYPE_MIXED = "mixed";
    private static final String TAG_VAR = "var";

//    private PhpType createMixedType() {
//        return TypeFactory.createType(TYPE_MIXED);
//    }

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
            String value = tag.getValue();
            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            final String[] parts = value.trim().split("\\s+");
            final String typeString = parts[0];
            final String docVarName = parts.length > 1 ? parts[1] : null;

            if (docVarName != null && !docVarName.equals(targetVarName)) {
                continue;
            }

            if (typeString.contains("|")) {
                final String[] unionTypes = typeString.split("\\|");
                List<PhpType> typesList = new ArrayList<>();
                for (String ut : unionTypes) {
                    typesList.add(TypeFactory.createType(ut));
                }

                return TypeFactory.createUnionType(typesList);
            } else {
                return TypeFactory.createType(typeString);
            }
        }

        return TypeFactory.createType(TYPE_MIXED);
    }
}
