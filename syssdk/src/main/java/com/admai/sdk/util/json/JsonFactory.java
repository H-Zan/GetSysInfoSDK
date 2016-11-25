package com.admai.sdk.util.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zan on 2016/11/20.
 */
public class JsonFactory {

    public static boolean isBasicType(Class<?> clz) {
        if (clz.isPrimitive() || String.class.isAssignableFrom(clz)) {
            return true;
        }
        return isWrapType(clz);
    }

    public static boolean isWrapType(Class<?> clz) {
        try {
            if (((Class<?>) clz.getField("TYPE").get(null)).isPrimitive()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static String toJson(Object src) throws Exception {
        return toJson(src, src.getClass());
    }

    public static String toJson(Object src, Class<?> classOfSrc)
            throws Exception {
        if (isBasicType(classOfSrc)) {
            return src.toString();
        } else if (List.class.isAssignableFrom(classOfSrc)) {// 列表
            JSONArray jsonArray = new JSONArray();
            List<?> list = (List<?>) src;
            for (Object object : list) {
                jsonArray.put(new JSONObject(toJson(object)));
            }
            return jsonArray.toString();
        } else if (classOfSrc.isArray()) {// 数组
            JSONArray jsonArray = new JSONArray();
            Object[] objArray = (Object[]) src;
            for (Object object : objArray) {
                jsonArray.put(new JSONObject(toJson(object)));
            }
            return jsonArray.toString();
        } else {// 对象
            return object2Json(src);
        }
    }

    private static String object2Json(Object entity) throws Exception {
        JSONObject jsonObject = new JSONObject();
        List<Field> fields = getAllFields(entity.getClass());
        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
            }
            JsonParam JsonParam = field.getAnnotation(JsonParam.class);
            if (JsonParam != null) {
                Object value = field.get(entity);
                if (value != null) {
                    String jsonKey = JsonParam.key();
                    Class<?> fieldClazz = field.getType();
                    if (isBasicType(fieldClazz)) {// 基础类型
                        if (!jsonObject.has(jsonKey)) {
                            jsonObject.put(jsonKey, value);
                        } else {
                            Log.w("JSON::",
                                    "class["
                                            + field.getDeclaringClass()
                                            .getSimpleName()
                                            + "] declares multiple JSON fields named name["
                                            + jsonKey + "]..");
                        }
                    } else {
                        if (List.class.isAssignableFrom(fieldClazz)) {// 列表
                            JSONArray jsonArray = new JSONArray();
                            List<?> list = (List<?>) value;
                            for (Object object : list) {
                                if (isBasicType(object.getClass())) {
                                    jsonArray.put(object);
                                } else {
                                    jsonArray
                                            .put(new JSONObject(toJson(object)));
                                }
                            }
                            jsonObject.put(jsonKey, jsonArray);
                        } else if (fieldClazz.isArray()) {// 数组
                            JSONArray jsonArray = new JSONArray();
                            Object[] objArray = (Object[]) value;
                            for (Object object : objArray) {
                                if (isBasicType(object.getClass())) {
                                    jsonArray.put(object);
                                } else {
                                    jsonArray
                                            .put(new JSONObject(toJson(object)));
                                }
                            }
                            jsonObject.put(jsonKey, jsonArray);
                        } else {// 对象
                            jsonObject.put(jsonKey, new JSONObject(
                                    toJson(value)));
                        }
                    }
                }
            }
        }
        return jsonObject.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Type typeOfT) throws Exception {
        if (typeOfT instanceof Class) {// 对象
            return (T) fromJson(new JSONObject(json),
                    TypeToken.getRawType(typeOfT));
        } else if (typeOfT instanceof ParameterizedType) {// 列表
            JSONArray jsonArray = new JSONArray(json);
            return (T) jsonArray2List(jsonArray, TypeToken.getRawType(typeOfT),
                    typeOfT);
        } else if (typeOfT instanceof GenericArrayType) {// 数组
            JSONArray jsonArray = new JSONArray(json);
            return (T) jsonArray2Array(jsonArray, TypeToken.getRawType(typeOfT));
        } else {
            String className = typeOfT == null ? "null" : typeOfT.getClass()
                    .getName();
            throw new IllegalArgumentException(
                    "Expected a Class, ParameterizedType, or "
                            + "GenericArrayType, but <" + typeOfT
                            + "> is of type " + className);
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT)
            throws Exception {
        return fromJson(new JSONObject(json), classOfT);
    }

    private static <T> T fromJson(JSONObject jsonObject, Class<T> classOfT)
            throws Exception {
        Constructor<T> cons = classOfT.getDeclaredConstructor();
        cons.setAccessible(true);
        T entity = cons.newInstance();
        List<Field> fields = getAllFields(classOfT);
        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
            }
            try {
                fillField(entity, field, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return entity;
    }

    private static void fillField(Object entity, Field field, JSONObject jsonObject) throws Exception {
        JsonParam JsonParam = field.getAnnotation(JsonParam.class);
        if (JsonParam != null) {
            String jsonKey = JsonParam.key();
            if (jsonObject.has(jsonKey)) {
                Class<?> fieldClazz = field.getType();
                if (isBasicType(fieldClazz)) {// 基础类型
                    Object value = jsonObject.get(jsonKey);
                    jsonObject.remove(jsonKey);
                    try {
                        if (isWrapType(fieldClazz)
                                || String.class.isAssignableFrom(fieldClazz)) {
                            Constructor<?> cons = fieldClazz
                                    .getConstructor(String.class);// 包装类，均有带String的构造
                            Object attrValue = cons.newInstance(value
                                    .toString());
                            field.set(entity, attrValue);
                        } else {
                            field.set(entity, value);
                        }
                    } catch (Exception e) {
                        String error = "invalid value[" + value
                                + "] for field[" + field.getName()
                                + "]; valueClass[" + value.getClass()
                                + "]; annotationName[" + jsonKey + "]";
                        throw new JSONException(error);
                    }
                } else {
                    Object objValue = jsonObject.get(jsonKey);
                    if (objValue != null && !objValue.equals(null)) {
                        if (List.class.isAssignableFrom(fieldClazz)) {// 列表
                            List<Object> listValue = jsonArray2List(
                                    jsonObject.getJSONArray(jsonKey),
                                    fieldClazz, field.getGenericType());
                            field.set(entity, listValue);
                        } else if (fieldClazz.isArray()) {// 数组
                            field.set(
                                    entity,
                                    jsonArray2Array(
                                            jsonObject.getJSONArray(jsonKey),
                                            fieldClazz));
                        } else {// 对象
                            field.set(
                                    entity,
                                    fromJson(jsonObject.getJSONObject(jsonKey),
                                            fieldClazz));
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> jsonArray2List(JSONArray jsonArray,
                                               Class<?> listClazz, Type type) throws Exception {
        List<Object> listValue = null;
        if (jsonArray != null) {
            if (type instanceof ParameterizedType) {
                if (listClazz.isInterface()
                        || Modifier.isAbstract(listClazz.getModifiers())) {
                    listClazz = ArrayList.class;
                }
                listValue = (List<Object>) listClazz.newInstance();
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> paramClazz = (Class<?>) parameterizedType
                        .getActualTypeArguments()[0];
                int jsonArrayLength = jsonArray.length();
                for (int i = 0; i < jsonArrayLength; i++) {
                    if (isBasicType(paramClazz)) {
                        listValue.add(jsonArray.get(i));
                    } else {
                        listValue.add(fromJson(jsonArray.getJSONObject(i),
                                paramClazz));
                    }
                }
            }
        }
        return listValue;
    }

    private static Object jsonArray2Array(JSONArray jsonArray, Class<?> rawType)
            throws Exception {
        Class<?> componentType = rawType.getComponentType();
        int len = jsonArray.length();
        Object array = Array.newInstance(componentType, len);
        Object objValue = null;
        for (int i = 0; i < len; i++) {
            if (isBasicType(componentType)) {
                objValue = jsonArray.get(i);
            } else {
                objValue = fromJson(jsonArray.getJSONObject(i), componentType);
            }
            Array.set(array, i, objValue);
        }
        return array;
    }

    private static List<Field> getAllFields(Class<?> objClass) {
        List<Field> fields = new ArrayList<Field>();
        Field[] declaredFields = objClass.getDeclaredFields();
        Collections.addAll(fields, declaredFields);
        fillSuperFields(fields, objClass);
        return fields;
    }

    private static void fillSuperFields(List<Field> fields, Class<?> subClass) {
        Class<?> superClass = subClass.getSuperclass();
        if (superClass != null) {
            Field[] superFields = superClass.getDeclaredFields();
            if (superFields.length > 0) {
                Collections.addAll(fields, superFields);
                fillSuperFields(fields, superClass);
            }
        }
    }

    public static abstract class TypeToken<T> {
        final Class<? super T> rawType;
        final Type type;

        @SuppressWarnings("unchecked")
        protected TypeToken() {
            this.type = getSuperclassTypeParameter(getClass());
            this.rawType = (Class<? super T>) getRawType(type);
        }

        public Type getType() {
            return type;
        }

        public Class<?> getRawType() {
            return rawType;
        }

        public static Type getInterfacesTypeParameter(Class<?> subclass,
                                                      Class<?> interfClass) {
            Type resultType = null;
            Type[] types = subclass.getGenericInterfaces();
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    if (interfClass.isAssignableFrom(getRawType(type))) {
                        ParameterizedType parameterized = (ParameterizedType) type;
                        return parameterized.getActualTypeArguments()[0];
                    }
                }
            }
            return resultType;
        }

        public static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return parameterized.getActualTypeArguments()[0];
        }

        public static Class<?> getRawType(Type type) {
            if (type instanceof Class<?>) {
                return (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                if (!(rawType instanceof Class)) {
                    throw new IllegalArgumentException();
                }
                return (Class<?>) rawType;
            } else if (type instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) type)
                        .getGenericComponentType();
                return Array.newInstance(getRawType(componentType), 0)
                        .getClass();
            } else if (type instanceof TypeVariable) {
                return Object.class;
            } else if (type instanceof WildcardType) {
                return getRawType(((WildcardType) type).getUpperBounds()[0]);
            } else {
                String className = type == null ? "null" : type.getClass()
                        .getName();
                throw new IllegalArgumentException(
                        "Expected a Class, ParameterizedType, or "
                                + "GenericArrayType, but <" + type
                                + "> is of type " + className);
            }
        }
    }

}
