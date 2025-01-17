/*
 * Copyright (c) 2017-2020. Nitrite author or authors.
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


package org.dizitart.no2.repository.data;

import lombok.Getter;
import lombok.Setter;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.exceptions.ObjectMappingException;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Anindya Chatterjee.
 */
@Getter
public class WithFinalField implements Mappable {
    private final long number;
    @Setter
    private String name;

    public WithFinalField() {
        number = 2;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        return Document.createDocument()
            .put("name", name)
            .put("number", number);
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        name = document.get("name", String.class);
        try {
            Field field = getClass().getDeclaredField("number");
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(this, document.get("number", Long.class));
        } catch (Exception e) {
            throw new ObjectMappingException("failed to set value", e);
        }
    }
}
