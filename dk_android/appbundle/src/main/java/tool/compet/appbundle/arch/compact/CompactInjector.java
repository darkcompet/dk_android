package tool.compet.appbundle.arch.compact;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tool.compet.appbundle.arch.compact.annotation.DkCompactInject;
import tool.compet.appbundle.arch.compact.annotation.DkInjectHostTopic;
import tool.compet.appbundle.arch.compact.annotation.DkInjectViewLogic;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.util.DkLogs;

import static tool.compet.core.reflection.DkReflectionFinder.extractFields;

@SuppressWarnings("unchecked")
class CompactInjector {
   private final CompactView view;
   private final Class viewClass;
   private final List<DkCompactViewLogic> allViewLogics = new ArrayList<>();
   private ArrayMap<Class, CompactComponent> allComponentMap = new ArrayMap<>();
   private List<Class<? extends Annotation>> allAnnotations = Arrays.asList(
      DkCompactInject.class,
      DkInjectHostTopic.class);

   CompactInjector(CompactView view) {
      this.view = view;
      this.viewClass = view.getClass();
   }
   
   /**
    * Start from all compact-annotated fields inside the View. Collect all compact-annotated fields
    * which be specified in each type of field. After at all, init them and inject to correspond field.
    * <p></p>
    * Note that, this method must be called after #super.onCreate() inside subclass of View.
    */
   List<DkCompactViewLogic> start() {
      CompactStore store = view.getOwnViewModel(CompactStore.class);
      ArrayMap fieldsMap = DkReflectionFinder.getIns().findFields(viewClass, allAnnotations, true, false);

      List<Field> viewLogicFieldsInsideView = extractFields(DkInjectViewLogic.class, viewClass, fieldsMap);
      List<Field> hostTopicFieldsInsideView = extractFields(DkInjectHostTopic.class, viewClass, fieldsMap);
      List<Field> plainFieldsInsideView = extractFields(DkCompactInject.class, viewClass, fieldsMap);

      // Lookup cache from store first
      if (store.allViewLogics != null) {
         List<Field> compactFieldsInsideView = new ArrayList<>();

         compactFieldsInsideView.addAll(viewLogicFieldsInsideView);
         compactFieldsInsideView.addAll(hostTopicFieldsInsideView);
         compactFieldsInsideView.addAll(plainFieldsInsideView);

         for (Field compactFieldInsideView : compactFieldsInsideView) {
            _setFieldValue(compactFieldInsideView, view, store.fieldTypeToCompactObjectMap.get(compactFieldInsideView.getType()));
         }
         return store.allViewLogics;
      }

      // Not found cache in the store, Init all compact-annotated fields in the View.
      store.fieldTypeToCompactObjectMap = new ArrayMap<>();
      store.fieldTypeToCompactObjectMap.putAll(_initViewLogicFields(view, viewLogicFieldsInsideView));
      store.fieldTypeToCompactObjectMap.putAll(_initHostTopicFields(view, hostTopicFieldsInsideView));
      store.fieldTypeToCompactObjectMap.putAll(_initPlainFields(view, plainFieldsInsideView));

      // Scan and Init all compact-annotated-fields recursively.
      if (store.fieldTypeToCompactObjectMap.size() > 0) {
         for (int index = store.fieldTypeToCompactObjectMap.size() - 1; index >= 0; --index) {
            Class inViewObjType = store.fieldTypeToCompactObjectMap.keyAt(index);

            if (allComponentMap.getOrDefault(inViewObjType, null).needInitialize) {
               _injectCompactAnnotatedFieldsInside(inViewObjType);
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
   private void _injectCompactAnnotatedFieldsInside(Class targetType) {
      // Init VML-annotated fields inside the target
      ArrayMap<String, List<Field>> fieldsMap = DkReflectionFinder.getIns()
         .findFields(targetType, allAnnotations,true,false);

      // targetComponent must exist in allComponentMap
      final CompactComponent targetComponent = allComponentMap.getOrDefault(targetType, null);
      final Object target = targetComponent.obj;
      final SimpleArrayMap<Class, Object> injectedObjTypes = new ArrayMap<>();

      List<Field> viewLogicFields = extractFields(DkCompactInject.class, targetType, fieldsMap);
      List<Field> hostTopicFields = extractFields(DkInjectHostTopic.class, targetType, fieldsMap);
      List<Field> plainFields = extractFields(DkCompactInject.class, targetType, fieldsMap);

      if (viewLogicFields.size() > 0) {
         injectedObjTypes.putAll(_initViewLogicFields(target, viewLogicFields));
      }
      if (hostTopicFields.size() > 0) {
         injectedObjTypes.putAll(_initHostTopicFields(target, hostTopicFields));
      }
      if (plainFields.size() > 0) {
         injectedObjTypes.putAll(_initPlainFields(target, plainFields));
      }

      // mark all component-fields of the target was initialized.
      targetComponent.needInitialize = false;

      // Visit next fields
      for (int index = injectedObjTypes.size() - 1; index >= 0; --index) {
         Class injectedObjType = injectedObjTypes.keyAt(index);
         // skip inject for field which all component-fields inside it was initialized,
         // note that, this field was already registered in allComponentMap above.
         if (allComponentMap.getOrDefault(injectedObjType, null).needInitialize) {
            _injectCompactAnnotatedFieldsInside(injectedObjType);
         }
      }
   }

   private ArrayMap<Class, Object> _initPlainFields(Object target, List<Field> plainFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field plainField : plainFields) {
         Class plainType = plainField.getType();

         CompactComponent plainComponent = allComponentMap.getOrDefault(plainType, null);

         if (plainComponent == null) {
            plainComponent = new CompactComponent(_instantiate(plainType));
            allComponentMap.put(plainType, plainComponent);
         }

         fieldTypeMap.put(plainType, plainComponent.obj);
         _setFieldValue(plainField, target, plainComponent.obj);
      }

      return fieldTypeMap;
   }

   private ArrayMap<Class, Object> _initModelLogicFields(Object target, List<Field> mlFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field mlField : mlFields) {
         Class mlType = mlField.getType();

         _checkType(mlType, DkCompactModelLogic.class, target, mlField);

         CompactComponent mlComponent = allComponentMap.getOrDefault(mlType, null);

         if (mlComponent == null) {
            mlComponent = new CompactComponent(_instantiate(mlType));
            allComponentMap.put(mlType, mlComponent);
         }

         fieldTypeMap.put(mlType, mlComponent.obj);
         _setFieldValue(mlField, target, mlComponent.obj);
      }

      return fieldTypeMap;
   }

   private ArrayMap<Class, Object> _initViewLogicFields(Object target, List<Field> vlFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field vlField : vlFields) {
         Class vlType = vlField.getType();

         _checkType(vlType, DkCompactViewLogic.class, target, vlField);

         CompactComponent vlComponent = allComponentMap.getOrDefault(vlType, null);

         if (vlComponent == null) {
            vlComponent = new CompactComponent(_instantiate(vlType));
            allComponentMap.put(vlType, vlComponent);

            // remember this ViewLogic
            DkCompactViewLogic vl = (DkCompactViewLogic) vlComponent.obj;

            if (!allViewLogics.contains(vl)) {
               allViewLogics.add(vl);
            }
         }

         // attach view to ViewLogic
         DkCompactViewLogic vl = (DkCompactViewLogic) vlComponent.obj;
         vl.attachView(view);

         fieldTypeMap.put(vlType, vl);
         _setFieldValue(vlField, target, vl);
      }

      return fieldTypeMap;
   }

   private ArrayMap<Class, Object> _initHostTopicFields(Object target, List<Field> argumentFields) {
      ArrayMap<Class, Object> fieldTypeMap = new ArrayMap<>();

      for (Field topicField : argumentFields) {
         Class argType = topicField.getType();

         CompactComponent argComponent = allComponentMap.getOrDefault(argType, null);

         if (argComponent == null) {
            argComponent = new CompactComponent(view.getHostTopic(argType, false));
            allComponentMap.put(argType, argComponent);
         }

         fieldTypeMap.put(argType, argComponent.obj);
         _setFieldValue(topicField, target, argComponent.obj);
      }

      return fieldTypeMap;
   }

   private void _checkType(Class subClass, Class superClass, Object owner, Field field) {
      if (!superClass.isAssignableFrom(subClass)) {
         DkLogs.complain(owner, "Type of field %s must be subclass of %s",
            field.getName(), superClass.getName());
      }
   }

   private Object _instantiate(Class type) {
      try {
         return type.newInstance();
      }
      catch (Exception e) {
         DkLogs.error(this, e);
         DkLogs.complain(this, "Could not instantiate for class %s. Make sure the class" +
               " is public, not abstract, interface and have a public empty constructor.", type.getName());
      }
      return null;
   }

   private void _setFieldValue(Field field, Object target, Object value) {
      try {
         field.setAccessible(true);
         field.set(target, value);
      }
      catch (IllegalAccessException e) {
         DkLogs.error(this, e);
         DkLogs.complain(this, "Could not set value for field %s in the class %s." +
               " Make sure the field is not final.",
            field.getName(), target.getClass().getName());
      }
   }
}
