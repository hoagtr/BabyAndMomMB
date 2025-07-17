package com.example.prm392mnlv.retrofit.json;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public class PathedJsonAdapterFactory implements JsonAdapter.Factory {
    private static final String OVERENGINEERED_SPLIT_PATTERN = "(?<!^)(?<!^[:\\s]):(?![:\\s])(?!$)";

    @Override
    public JsonAdapter<?> create(@NonNull Type type, @NonNull Set<? extends Annotation> annotations, @NonNull Moshi moshi) {
        JsonPath jsonPathAnn = findJsonPath(annotations);
        if (jsonPathAnn == null) return null;

        String jsonPath = jsonPathAnn.value();
        String[] segments = jsonPath.split(OVERENGINEERED_SPLIT_PATTERN);
        JsonAdapter<?> delegate = moshi.adapter(type);

        return new JsonAdapter<>() {
            @Override
            public Object fromJson(@NonNull JsonReader reader) throws IOException {
                return readSection(reader, 0);
            }

            private Object readSection(@NonNull JsonReader reader, int depth) throws IOException {
                Object result = null;
                reader.beginObject();
                while (reader.hasNext()) {
                    if (reader.nextName().equals(segments[depth])) {
                        if (depth == segments.length - 1) {
                            result = delegate.fromJson(reader);
                        } else {
                            result = readSection(reader, ++depth);
                        }
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                return result;
            }

            @Override
            public void toJson(@NonNull JsonWriter writer, Object obj) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Nullable
    private JsonPath findJsonPath(@NonNull Set<? extends Annotation> annotations) {
        for (Annotation ann : annotations) {
            if (ann.annotationType().equals(JsonPath.class)) {
                return (JsonPath) ann;
            }
        }
        return null;
    }
}
