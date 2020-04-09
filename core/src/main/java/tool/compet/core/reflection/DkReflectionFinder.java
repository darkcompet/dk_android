/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.core.reflection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Find fields or methods which annotated with specified annotations in class.
 * Note that, this class supports cache optimization.
 */
public class DkReflectionFinder {
   private static DkReflectionFinder INS;

   private Map<String, List<Field>> fieldCache;
   private Map<String, List<Method>> methodCache;

   // For reset search packages at runtime
   private final String[] searchPackages;

   private DkReflectionFinder(String... searchPackages) {
      if (searchPackages == null) {
         searchPackages = new String[] {"tool.compet"};
      }
      this.searchPackages = searchPackages;
      this.fieldCache = new ArrayMap<>();
      this.methodCache = new ArrayMap<>();
   }

   /**
    * Use this will install with {@code searchPackages = {"tool.compet", context.getPackageName()}}.
    */
   public static void installWithCompetTool(Context context) {
      install("tool.compet", context.getPackageName());
   }

   /**
    * Should only call in one thread (don't multiple threading install).
    */
   public static void install(String... searchPackages) {
      if (INS == null) {
         INS = new DkReflectionFinder(searchPackages);
      }
   }

   public static DkReflectionFinder getIns() {
      if (INS == null) {
         throw new RuntimeException("Must call install() first");
      }
      return INS;
   }

   /**
    * Calculate cache-key for a annotation of given class.
    */
   private static String keyOf(Class clazz, Class<? extends Annotation> annotation) {
      return clazz.getName() + "_" + annotation.getName();
   }

   /**
    * From #fieldsMap of #clazz, get field-list of #annotation.
    */
   @NonNull
   public static List<Field> extractFields(Class<? extends Annotation> annotation, Class clazz, ArrayMap<String, List<Field>> fieldsMap) {
      String key = keyOf(clazz, annotation);
      List<Field> fields = fieldsMap.get(key);

      return fields != null ? fields : Collections.emptyList();
   }

   /**
    * From #methodsMap of #clazz, get method-list of #annotation.
    */
   @NonNull
   public static List<Method> extractMethods(Class<? extends Annotation> annotation, Class clazz, ArrayMap<String, List<Method>> methodsMap) {
      String key = keyOf(clazz, annotation);
      List<Method> methods = methodsMap.get(key);

      return methods != null ? methods : Collections.emptyList();
   }

   /**
    * Find fields which be annotated with given #annotation inside a class.
    */
   @NonNull
   public List<Field> findFields(Class clazz, Class<? extends Annotation> annotation, boolean upSuper,  boolean cache) {
      //+ Lookup cache first
      String key = keyOf(clazz, annotation);
      List<Field> fields = fieldCache.get(key);

      if (fields != null) {
         return fields;
      }

      //+ Not found in cache, start search and cache
      fields = new FieldsMethodsFinder()
         .findFields(clazz, Collections.singletonList(annotation), upSuper, searchPackages)
         .get(annotation);

      if (fields == null) {
         fields = Collections.emptyList();
      }
      if (cache) {
         fieldCache.put(key, fields);
      }

      return fields;
   }

   /**
    * @return map which {@code key} is {@link DkReflectionFinder#keyOf(Class, Class)} and
    *    {@code value} is field list of that annoClass. To get fields of a annoClass, consider use
    *    {@link DkReflectionFinder#extractFields(Class, Class, ArrayMap)}.
    */
   @NonNull
   public ArrayMap<String, List<Field>> findFields(Class clazz, Iterable<Class<? extends Annotation>> annotations, boolean upSuper, boolean cache) {
      ArrayMap<String, List<Field>> result = new ArrayMap<>();

      for (Class<? extends Annotation> annoClass : annotations) {
         // Lookup cache for this annotation first
         String key = keyOf(clazz, annoClass);
         List<Field> fields = fieldCache.get(key);

         // Not found cache, start find
         if (fields == null) {
            fields = findFields(clazz, annoClass, upSuper, cache);
         }

         result.put(key, fields);
      }

      return result;
   }

   /**
    * Find methods which be annotated with given #annotation inside a class.
    */
   @NonNull
   public List<Method> findMethods(Class clazz, Class<? extends Annotation> annotation, boolean upSuper, boolean cache) {
      // Lookup cache first
      String key = keyOf(clazz, annotation);
      List<Method> methods = methodCache.get(key);

      if (methods != null) {
         return methods;
      }

      // Not found in cache, start search
      methods = new FieldsMethodsFinder()
         .findMethods(clazz, Collections.singletonList(annotation), upSuper, searchPackages)
         .get(annotation);

      if (methods == null) {
         methods = Collections.emptyList();
      }
      if (cache) {
         methodCache.put(key, methods);
      }

      return methods;
   }

   /**
    * @return map which {@code key} is {@link DkReflectionFinder#keyOf(Class, Class)} and
    *    {@code value} is field list of that annoClass. To get methods of a annoClass, consider use
    *    {@link DkReflectionFinder#extractMethods(Class, Class, ArrayMap)}.
    */
   @NonNull
   public ArrayMap<String, List<Method>> findMethods(Class clazz, Iterable<Class<? extends Annotation>> annotations, boolean upSuper, boolean cache) {
      ArrayMap<String, List<Method>> result = new ArrayMap<>();

      for (Class<? extends Annotation> annoClass : annotations) {
         // Lookup cache for this annotation
         String key = keyOf(clazz, annoClass);
         List<Method> methods = methodCache.get(key);

         // Not found cache, start find
         if (methods == null) {
            methods = findMethods(clazz, annoClass, upSuper, cache);
         }

         result.put(key, methods);
      }

      return result;
   }
}
