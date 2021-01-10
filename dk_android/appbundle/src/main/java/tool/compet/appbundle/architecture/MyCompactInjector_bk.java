///*
// * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
// */
//
//package tool.compet.appbundle.arch.compact;
//
//import androidx.collection.ArrayMap;
//import androidx.collection.SimpleArrayMap;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import tool.compet.appbundle.arch.compact.annotation.DkInjectHostTopic;
//import tool.compet.appbundle.arch.compact.annotation.DkInjectPlain;
//import tool.compet.appbundle.arch.compact.annotation.DkInjectViewLogic;
//import tool.compet.core.log.DkLogs;
//import tool.compet.core.reflection.DkReflectionFinder;
//
//@SuppressWarnings("unchecked")
//class MyCompactInjector_bk {
//    private final MyCompactView view;
//    private final Class viewClass;
//    private final List<DkCompactViewLogic> allViewLogics = new ArrayList<>();
//    private final ArrayMap<Class, MyCompactComponent> allComponentMap = new ArrayMap<>();
//    private final List<Class<? extends Annotation>> allAnnotations = Arrays.asList(
//        DkInjectPlain.class,
//        DkInjectViewLogic.class,
//        DkInjectHostTopic.class
//    );
//
//    MyCompactInjector_bk(MyCompactView view) {
//        this.view = view;
//        this.viewClass = view.getClass();
//    }
//
//    /**
//     * Start from all compact-annotated fields inside the View. Collect all compact-annotated fields
//     * which be specified in each type of field. After at all, init them and inject to correspond field.
//     * <p></p>
//     * Note that, this method must be called after #super.onCreate() inside subclass of View.
//     */
//    List<DkCompactViewLogic> start() {
//        MyCompactStore store = view.getOwnViewModel(MyCompactStore.class);
//        ArrayMap fieldsMap = DkReflectionFinder.getInstalledIns().findFields(viewClass, allAnnotations, true, false);
//
//        List<Field> viewLogicFieldsInsideView = DkReflectionFinder.extractFields(DkInjectViewLogic.class, viewClass, fieldsMap);
//        List<Field> hostTopicFieldsInsideView = DkReflectionFinder.extractFields(DkInjectHostTopic.class, viewClass, fieldsMap);
//        List<Field> plainFieldsInsideView = DkReflectionFinder.extractFields(DkInjectPlain.class, viewClass, fieldsMap);
//
//        // Lookup cache from store first
//        if (store.allViewLogics != null) {
//            List<Field> compactFieldsInsideView = new ArrayList<>();
//
//            compactFieldsInsideView.addAll(viewLogicFieldsInsideView);
//            compactFieldsInsideView.addAll(hostTopicFieldsInsideView);
//            compactFieldsInsideView.addAll(plainFieldsInsideView);
//
//            for (Field compactFieldInsideView : compactFieldsInsideView) {
//                setFieldValue(compactFieldInsideView, view, store.fieldTypeToCompactObjectMap.get(compactFieldInsideView.getType()));
//            }
//            return store.allViewLogics;
//        }
//
//        // Not found cache in the store, Init all compact-annotated fields in the View.
//        store.fieldTypeToCompactObjectMap = new ArrayMap<>();
//        store.fieldTypeToCompactObjectMap.putAll(initViewLogicFields(view, viewLogicFieldsInsideView));
//        store.fieldTypeToCompactObjectMap.putAll(initHostTopicFields(view, hostTopicFieldsInsideView));
//        store.fieldTypeToCompactObjectMap.putAll(initPlainFields(view, plainFieldsInsideView));
//
//        // Scan and Init all compact-annotated-fields recursively.
//        if (store.fieldTypeToCompactObjectMap.size() > 0) {
//            for (int index = store.fieldTypeToCompactObjectMap.size() - 1; index >= 0; --index) {
//                Class inViewObjType = store.fieldTypeToCompactObjectMap.keyAt(index);
//
//                if (allComponentMap.getOrDefault(inViewObjType, null).needInitialize) {
//                    injectCompactAnnotatedFieldsInside(inViewObjType);
//                }
//            }
//        }
//
//        return (store.allViewLogics = allViewLogics);
//    }
//
//    /**
//     * Inject all VML-annotated fields inside the target class.
//     *
//     * @param targetType class whose fields of it are not yet initialized.
//     */
//    private void injectCompactAnnotatedFieldsInside(Class targetType) {
//        // Init VML-annotated fields inside the target
//        ArrayMap<String, List<Field>> fieldsMap = DkReflectionFinder.getInstalledIns()
//            .findFields(targetType, allAnnotations, true, false);
//
//        // targetComponent must exist in allComponentMap
//        final MyCompactComponent targetComponent = allComponentMap.getOrDefault(targetType, null);
//        final Object target = targetComponent.obj;
//        final SimpleArrayMap<Class, Object> injectedObjTypes = new ArrayMap<>();
//
//        List<Field> viewLogicFields = DkReflectionFinder.extractFields(DkInjectPlain.class, targetType, fieldsMap);
//        List<Field> hostTopicFields = DkReflectionFinder.extractFields(DkInjectHostTopic.class, targetType, fieldsMap);
//        List<Field> plainFields = DkReflectionFinder.extractFields(DkInjectPlain.class, targetType, fieldsMap);
//
//        if (viewLogicFields.size() > 0) {
//            injectedObjTypes.putAll(initViewLogicFields(target, viewLogicFields));
//        }
//        if (hostTopicFields.size() > 0) {
//            injectedObjTypes.putAll(initHostTopicFields(target, hostTopicFields));
//        }
//        if (plainFields.size() > 0) {
//            injectedObjTypes.putAll(initPlainFields(target, plainFields));
//        }
//
//        // mark all component-fields of the target was initialized.
//        targetComponent.needInitialize = false;
//
//        // Visit next fields
//        for (int index = injectedObjTypes.size() - 1; index >= 0; --index) {
//            Class injectedObjType = injectedObjTypes.keyAt(index);
//            // skip inject for field which all component-fields inside it was initialized,
//            // note that, this field was already registered in allComponentMap above.
//            if (allComponentMap.getOrDefault(injectedObjType, null).needInitialize) {
//                injectCompactAnnotatedFieldsInside(injectedObjType);
//            }
//        }
//    }
//
//    private ArrayMap<Class, Object> initPlainFields(Object target, List<Field> plainFields) {
//        ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();
//
//        for (Field plainField : plainFields) {
//            Class plainType = plainField.getType();
//
//            MyCompactComponent plainComponent = allComponentMap.getOrDefault(plainType, null);
//
//            if (plainComponent == null) {
//                plainComponent = new MyCompactComponent(instantiate(plainType));
//                allComponentMap.put(plainType, plainComponent);
//            }
//
//            fieldTypeMap.put(plainType, plainComponent.obj);
//            setFieldValue(plainField, target, plainComponent.obj);
//        }
//
//        return fieldTypeMap;
//    }
//
//    private ArrayMap<Class, Object> initViewLogicFields(Object target, List<Field> vlFields) {
//        ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();
//
//        for (Field vlField : vlFields) {
//            Class vlType = vlField.getType();
//
//            checkType(vlType, DkCompactViewLogic.class, target, vlField);
//
//            MyCompactComponent vlComponent = allComponentMap.getOrDefault(vlType, null);
//
//            if (vlComponent == null) {
//                vlComponent = new MyCompactComponent(instantiate(vlType));
//                allComponentMap.put(vlType, vlComponent);
//
//                // remember this ViewLogic
//                DkCompactViewLogic vl = (DkCompactViewLogic) vlComponent.obj;
//
//                if (!allViewLogics.contains(vl)) {
//                    allViewLogics.add(vl);
//                }
//            }
//
//            // attach view to ViewLogic
//            DkCompactViewLogic vl = (DkCompactViewLogic) vlComponent.obj;
//            vl.attachView(view);
//
//            fieldTypeMap.put(vlType, vl);
//            setFieldValue(vlField, target, vl);
//        }
//
//        return fieldTypeMap;
//    }
//
//    private ArrayMap<Class, Object> initHostTopicFields(Object target, List<Field> argumentFields) {
//        ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();
//
//        for (Field topicField : argumentFields) {
//            Class argType = topicField.getType();
//
//            MyCompactComponent argComponent = allComponentMap.getOrDefault(argType, null);
//
//            if (argComponent == null) {
//                argComponent = new MyCompactComponent(view.getHostTopic(argType, false));
//                allComponentMap.put(argType, argComponent);
//            }
//
//            fieldTypeMap.put(argType, argComponent.obj);
//            setFieldValue(topicField, target, argComponent.obj);
//        }
//
//        return fieldTypeMap;
//    }
//
//    private void checkType(Class subClass, Class superClass, Object owner, Field field) {
//        if (!superClass.isAssignableFrom(subClass)) {
//            DkLogs.complain(owner, "Type of field %s must be subclass of %s",
//                field.getName(), superClass.getName());
//        }
//    }
//
//    private Object instantiate(Class type) {
//        try {
//            return type.newInstance();
//        }
//        catch (Exception e) {
//            DkLogs.error(this, e);
//            DkLogs.complain(this, "Could not instantiate for class %s. Make sure the class" +
//                " is public, not abstract, interface and have a public empty constructor.", type.getName());
//        }
//        return null;
//    }
//
//    private void setFieldValue(Field field, Object target, Object value) {
//        try {
//            field.setAccessible(true);
//            field.set(target, value);
//        }
//        catch (IllegalAccessException e) {
//            DkLogs.error(this, e);
//            DkLogs.complain(this, "Could not set value for field %s in the class %s." +
//                    " Make sure the field is not final.",
//                field.getName(), target.getClass().getName());
//        }
//    }
//}
