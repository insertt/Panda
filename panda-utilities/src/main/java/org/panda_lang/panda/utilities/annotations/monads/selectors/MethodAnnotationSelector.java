/*
 * Copyright (c) 2015-2019 Dzikoysk
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

package org.panda_lang.panda.utilities.annotations.monads.selectors;

import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import org.panda_lang.panda.utilities.annotations.AnnotationScannerStore;
import org.panda_lang.panda.utilities.annotations.AnnotationsScannerProcess;
import org.panda_lang.panda.utilities.annotations.AnnotationsScannerUtils;
import org.panda_lang.panda.utilities.annotations.adapter.MetadataAdapter;
import org.panda_lang.panda.utilities.annotations.monads.AnnotationsSelector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MethodAnnotationSelector implements AnnotationsSelector<Method> {

    private final Class<? extends Annotation> annotationType;

    public MethodAnnotationSelector(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public Collection<Method> select(AnnotationsScannerProcess process, AnnotationScannerStore store) {
        Set<String> selectedClasses = new HashSet<>();
        MetadataAdapter<ClassFile, FieldInfo, MethodInfo> adapter = process.getMetadataAdapter();

        for (ClassFile cachedClassFile : store.getCachedClassFiles()) {
            for (MethodInfo method : adapter.getMethods(cachedClassFile)) {
                for (String annotationName : adapter.getMethodAnnotationNames(method)) {
                    if (annotationType.getName().equals(annotationName)) {
                        selectedClasses.add(adapter.getMethodFullKey(cachedClassFile, method));
                    }
                }
            }
        }

        return AnnotationsScannerUtils.forMethods(process, selectedClasses);
    }

}
