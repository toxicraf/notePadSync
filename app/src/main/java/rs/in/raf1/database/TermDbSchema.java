package rs.in.raf1.database;

public class TermDbSchema {
    public static final class TermTable {
        public static final String NAME = "terms";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String CREATED = "termCreated";
            public static final String UPDATED = "termUpdated";
            public static final String FLAG = "termFlag";
            public static final String ENG = "termEnglish";
            public static final String SRB = "termSerbian";
            public static final String DSC = "termDescription";
        }
    }
}
