package fi.vm.yti.comments.api.utils;

public interface StatusUtils {

    static String localizeResourceStatusToFinnish(final String status) {
        if (status != null) {
            switch (status) {
                case "VALID": {
                    return "Voimassa oleva";
                }
                case "DRAFT": {
                    return "Luonnos";
                }
                case "SUPERSEDED": {
                    return "Korvattu";
                }
                case "INVALID": {
                    return "Virheellinen";
                }
                case "RETIRED": {
                    return "Poistettu käytöstä";
                }
                case "INCOMPLETE": {
                    return "Keskeneräinen";
                }
                case "SUGGESTED": {
                    return "Ehdotus";
                }
                default: {
                    return status;
                }
            }
        } else {
            return null;
        }
    }}
