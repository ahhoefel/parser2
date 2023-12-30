package com.github.ahhoefel.arm;

public interface ParameterType {

    public boolean matches(Parameter p);

    public static class ByInstanceOf<T> implements ParameterType {
        private Class<T> clazz;

        public ByInstanceOf(Class<T> clazz) {
            this.clazz = clazz;
        }

        public boolean matches(Parameter p) {
            return clazz.isInstance(p);
        }
    }

    public static ParameterType X = new ByInstanceOf<>(Register.class);
    public static ParameterType UINT_12 = new ByInstanceOf<>(UInt12.class);
    public static ParameterType UINT_15_MULTIPLE_OF_8 = new ByInstanceOf<>(UInt15MultipleOf8.class);
    public static ParameterType UINT_64 = new ByInstanceOf<>(UInt64.class);
    public static ParameterType LABEL = new ByInstanceOf<>(Label.class);
    public static ParameterType X_SHIFT_UINT_15_MULTIPLE_OF_8 = new ByInstanceOf<>(RegisterShift.class);
    public static ParameterType CONDITION = new ByInstanceOf<>(Condition.class);
    public static ParameterType COMMENT = new ByInstanceOf<>(Comment.class);
}
