/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.collection.ArrayMap;

/**
 * Each Topic provides storage to hold multiple types of model.
 */
class MyTopic {
    // Unique id for each topic.
    public final String id;

    // Holds models for this topic.
    private final ArrayMap<Class, Object> models = new ArrayMap<>();

    public MyTopic(String id) {
        this.id = id;
    }

    /**
     * Get or Create new model instance which associate with given #modelClass.
     */
    @SuppressWarnings("unchecked")
    public <M> M getModel(Class<M> modelType) throws Exception {
        M model = (M) models.get(modelType);

        if (model == null) {
            model = modelType.newInstance();
            models.put(modelType, model);
        }

        return model;
    }
}
