package tool.compet.appbundle.arch.vml;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tool.compet.appbundle.arch.vml.annotation.DkVmlInjectModelLogic;
import tool.compet.appbundle.arch.vml.annotation.DkVmlInjectPlain;
import tool.compet.appbundle.arch.vml.annotation.DkVmlInjectViewLogic;
import tool.compet.appbundle.arch.vml.annotation.DkVmlRequestArgument;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.util.DkLogs;

import static tool.compet.core.reflection.DkReflectionFinder.extractFields;

@SuppressWarnings("unchecked")
class VmlInjector {
   private final VmlView view;
   private final Class viewClass;
   private final List<DkVmlViewLogic> allViewLogics = new ArrayList<>();
   private ArrayMap<Class, VmlComponent> allComponentMap = new ArrayMap<>();
   private List<Class<? extends Annotation>> allAnnotations = Arrays.asList(
      DkVmlInjectModelLogic.class,
      DkVmlInjectViewLogic.class,
      DkVmlInjectPlain.class,
      DkVmlRequestArgument.class);

   VmlInjector(VmlView view) {
      this.view = view;
      this.viewClass = view.getClass();
   }
   
   /**
    * Start from all VML-annotated fields inside the View. Collect all VML-annotated fields
    * which be specified in each type of field. After at all, init them and inject to correspond field.
    * <p></p>
    * Note that, this method must be called after #super.onCreate() inside subclass of View.
    */
   List<DkVmlViewLogic> start() {
      VmlStore store = view.getOwnViewModel(VmlStore.class);

      ArrayMap fieldsMap = DkReflectionFinder.getIns()
         .findFields(viewClass, allAnnotations, true, false);

      List<Field> inViewMlFields = extractFields(DkVmlInjectModelLogic.class, viewClass, fieldsMap);
      List<Field> inViewVlFields = extractFields(DkVmlInjectViewLogic.class, viewClass, fieldsMap);
      List<Field> inViewArgFields = extractFields(DkVmlRequestArgument.class, viewClass, fieldsMap);
      List<Field> inViewPlainFields = extractFields(DkVmlInjectPlain.class, viewClass, fieldsMap);

      //+ Lookup cache from store first
      if (store.allViewLogics != null) {
         List<Field> inViewVmlFields = new ArrayList<>();

         inViewVmlFields.addAll(inViewMlFields);
         inViewVmlFields.addAll(inViewVlFields);
         inViewVmlFields.addAll(inViewArgFields);
         inViewVmlFields.addAll(inViewPlainFields);

         for (Field inViewVmlField : inViewVmlFields) {
            setFieldValue(inViewVmlField, view, store.inViewFieldTypeToVmlObjectMap.get(inViewVmlField.getType()));
         }
         return store.allViewLogics;
      }

      //+ Not found cache in the store, Init all vml-annotated fields in the View.
      store.inViewFieldTypeToVmlObjectMap = new ArrayMap<>();
      store.inViewFieldTypeToVmlObjectMap.putAll(initModelLogicFields(view, inViewMlFields));
      store.inViewFieldTypeToVmlObjectMap.putAll(initViewLogicFields(view, inViewVlFields));
      store.inViewFieldTypeToVmlObjectMap.putAll(initArgumentFields(view, inViewArgFields));
      store.inViewFieldTypeToVmlObjectMap.putAll(initPlainFields(view, inViewPlainFields));

      //+ Scan and Init all VML-annotated-fields recursively.
      if (store.inViewFieldTypeToVmlObjectMap.size() > 0) {
         for (int index = store.inViewFieldTypeToVmlObjectMap.size() - 1; index >= 0; --index) {
            Class inViewObjType = store.inViewFieldTypeToVmlObjectMap.keyAt(index);

            if (allComponentMap.getOrDefault(inViewObjType, null).needInitialize) {
               injectVmlAnnotatedFieldsInside(inViewObjType);
            }
         }
      }

      return (store.allViewLogics = allViewLogics);
   }

   /**
    * Inject all VML-annotated fields inside the target class.
    *
    * @param targetType class whose fields of it are not yet initialized.
    */
   private void injectVmlAnnotatedFieldsInside(Class targetType) {
      //+ Init VML-annotated fields inside the target
      ArrayMap<String, List<Field>> fieldsMap = DkReflectionFinder.getIns()
         .findFields(targetType, allAnnotations,true,false);

      //- targetComponent must exist in allComponentMap
      final VmlComponent targetComponent = allComponentMap.getOrDefault(targetType, null);
      final Object target = targetComponent.obj;
      final SimpleArrayMap<Class, Object> injectedObjTypes = new ArrayMap<>();

      List<Field> mlFields = extractFields(DkVmlInjectModelLogic.class, targetType, fieldsMap);
      List<Field> vlFields = extractFields(DkVmlInjectViewLogic.class, targetType, fieldsMap);
      List<Field> argFields = extractFields(DkVmlRequestArgument.class, targetType, fieldsMap);
      List<Field> plainFields = extractFields(DkVmlInjectPlain.class, targetType, fieldsMap);

      if (mlFields.size() > 0) {
         injectedObjTypes.putAll(initModelLogicFields(target, mlFields));
      }
      if (vlFields.size() > 0) {
         injectedObjTypes.putAll(initViewLogicFields(target, vlFields));
      }
      if (argFields.size() > 0) {
         injectedObjTypes.putAll(initArgumentFields(target, argFields));
      }
      if (plainFields.size() > 0) {
         injectedObjTypes.putAll(initPlainFields(target, plainFields));
      }

      //- mark all component-fields of the target was initialized.
      targetComponent.needInitialize = false;

      //+ Visit next fields
      for (int index = injectedObjTypes.size() - 1; index >= 0; --index) {
         Class injectedObjType = injectedObjTypes.keyAt(index);
         // skip inject for field which all component-fields inside it was initialized,
         // note that, this field was already registered in allComponentMap above.
         if (allComponentMap.getOrDefault(injectedObjType, null).needInitialize) {
            injectVmlAnnotatedFieldsInside(injectedObjType);
         }
      }
   }

   private ArrayMap<Class, Object> initModelLogicFields(Object target, List<Field> mlFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field mlField : mlFields) {
         Class mlType = mlField.getType();

         checkType(mlType, DkVmlModelLogic.class, target, mlField);

         VmlComponent mlComponent = allComponentMap.getOrDefault(mlType, null);

         if (mlComponent == null) {
            mlComponent = new VmlComponent(instantiate(mlType));
            allComponentMap.put(mlType, mlComponent);
         }

         fieldTypeMap.put(mlType, mlComponent.obj);
         setFieldValue(mlField, target, mlComponent.obj);
      }

      return fieldTypeMap;
   }

   private ArrayMap<Class, Object> initViewLogicFields(Object target, List<Field> vlFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field vlField : vlFields) {
         Class vlType = vlField.getType();

         checkType(vlType, DkVmlViewLogic.class, target, vlField);

         VmlComponent vlComponent = allComponentMap.getOrDefault(vlType, null);

         if (vlComponent == null) {
            vlComponent = new VmlComponent(instantiate(vlType));
            allComponentMap.put(vlType, vlComponent);

            // remember this ViewLogic
            DkVmlViewLogic vl = (DkVmlViewLogic) vlComponent.obj;

            if (!allViewLogics.contains(vl)) {
               allViewLogics.add(vl);
            }
         }

         // attach view to ViewLogic
         DkVmlViewLogic vl = (DkVmlViewLogic) vlComponent.obj;
         vl.attachView(view);

         fieldTypeMap.put(vlType, vl);
         setFieldValue(vlField, target, vl);
      }

      return fieldTypeMap;
   }

   private ArrayMap<Class, Object> initArgumentFields(Object target, List<Field> argumentFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field topicField : argumentFields) {
         Class argType = topicField.getType();

         VmlComponent argComponent = allComponentMap.getOrDefault(argType, null);

         if (argComponent == null) {
            argComponent = new VmlComponent(view.getHostTopic(argType, false));
            allComponentMap.put(argType, argComponent);
         }

         fieldTypeMap.put(argType, argComponent.obj);
         setFieldValue(topicField, target, argComponent.obj);
      }

      return fieldTypeMap;
   }

   private ArrayMap<Class, Object> initPlainFields(Object target, List<Field> plainFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field plainField : plainFields) {
         Class plainType = plainField.getType();

         VmlComponent plainComponent = allComponentMap.getOrDefault(plainType, null);

         if (plainComponent == null) {
            plainComponent = new VmlComponent(instantiate(plainType));
            allComponentMap.put(plainType, plainComponent);
         }

         fieldTypeMap.put(plainType, plainComponent.obj);
         setFieldValue(plainField, target, plainComponent.obj);
      }

      return fieldTypeMap;
   }

   private void checkType(Class subClass, Class superClass, Object owner, Field field) {
      if (!superClass.isAssignableFrom(subClass)) {
         DkLogs.complain(owner, "Type of field %s must be subclass of %s",
            field.getName(), superClass.getName());
      }
   }

   private Object instantiate(Class type) {
      try {
         return type.newInstance();
      }
      catch (Exception e) {
         DkLogs.logex(this, e);
         DkLogs.complain(this, "Could not instantiate for class %s. Make sure the class" +
               " is public, not abstract, interface and have a public empty constructor.", type.getName());
      }
      return null;
   }

   private void setFieldValue(Field field, Object target, Object value) {
      try {
         field.setAccessible(true);
         field.set(target, value);
      }
      catch (IllegalAccessException e) {
         DkLogs.logex(this, e);
         DkLogs.complain(this, "Could not set value for field %s in the class %s." +
               " Make sure the field is not final.",
            field.getName(), target.getClass().getName());
      }
   }
}
