package com.example.protocollectorframework.DataModule.Data;

public class MethodData {
    private String package_class_name;
    private String method_name;
    private String[] args_type;

    public MethodData(String package_class_name, String method_name, String[] args_type) {
        this.package_class_name = package_class_name;
        this.method_name = method_name;
        this.args_type = args_type;
    }

    public String getPackage_class_name() {
        return package_class_name;
    }

    public void setPackage_class_name(String package_class_name) {
        this.package_class_name = package_class_name;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public String[] getArgs_type() {
        return args_type;
    }

    public Class<?>[] getProcessedClasses() {
        Class<?>[] classes = null;
        if (args_type != null)
            classes = new Class<?>[args_type.length];
        else
            return null;

        for (int i = 0; i < args_type.length; i++) {
            try {
                String packageNameClass = args_type[i];
                classes[i] = Class.forName(packageNameClass);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classes;
    }


    public void setArgs_type(String[] args_type) {
        this.args_type = args_type;
    }

}
