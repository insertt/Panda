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

package org.panda_lang.panda.utilities.annotations.adapter;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.panda.utilities.annotations.AnnotationsScanner;
import org.panda_lang.panda.utilities.annotations.AnnotationsScannerFile;
import org.panda_lang.panda.utilities.commons.IOUtils;
import org.panda_lang.panda.utilities.commons.text.ContentJoiner;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavassistAdapter implements MetadataAdapter<ClassFile, FieldInfo, MethodInfo> {

    public static boolean includeInvisibleTag = true;

    @Override
    public boolean acceptsInput(String file) {
        return file.endsWith(".class");
    }

    private List<String> splitDescriptorToTypeNames(final String descriptors) {
        List<String> result = new ArrayList<>();

        if (descriptors != null && descriptors.length() != 0) {
            List<Integer> indices = new ArrayList<>();
            Descriptor.Iterator iterator = new Descriptor.Iterator(descriptors);

            while (iterator.hasNext()) {
                indices.add(iterator.next());
            }

            indices.add(descriptors.length());

            for (int i = 0; i < indices.size() - 1; i++) {
                String s1 = Descriptor.toString(descriptors.substring(indices.get(i), indices.get(i + 1)));
                result.add(s1);
            }
        }

        return result;
    }

    @Override
    public boolean isPublic(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof ClassFile) {
            return AccessFlag.isPublic(((ClassFile) o).getAccessFlags());
        }

        if (o instanceof MethodInfo) {
            return AccessFlag.isPublic(((MethodInfo) o).getAccessFlags());
        }

        if (o instanceof FieldInfo) {
            return AccessFlag.isPublic(((FieldInfo) o).getAccessFlags());
        }

        return false;
    }

    @Override
    public List<FieldInfo> getFields(ClassFile cls) {
        //noinspection unchecked
        return cls.getFields();
    }

    @Override
    public List<MethodInfo> getMethods(ClassFile cls) {
        //noinspection unchecked
        return cls.getMethods();
    }

    @Override
    public String getMethodName(MethodInfo method) {
        return method.getName();
    }

    @Override
    public List<String> getParameterNames(MethodInfo method) {
        String descriptor = method.getDescriptor();
        descriptor = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.lastIndexOf(")"));
        return splitDescriptorToTypeNames(descriptor);
    }

    @Override
    public List<String> getClassAnnotationNames(ClassFile aClass) {
        return getAnnotationNames((AnnotationsAttribute) aClass.getAttribute(AnnotationsAttribute.visibleTag),
                includeInvisibleTag ? (AnnotationsAttribute) aClass.getAttribute(AnnotationsAttribute.invisibleTag) : null);
    }

    @Override
    public List<String> getFieldAnnotationNames(FieldInfo field) {
        return getAnnotationNames((AnnotationsAttribute) field.getAttribute(AnnotationsAttribute.visibleTag),
                includeInvisibleTag ? (AnnotationsAttribute) field.getAttribute(AnnotationsAttribute.invisibleTag) : null);
    }

    @Override
    public List<String> getMethodAnnotationNames(MethodInfo method) {
        return getAnnotationNames((AnnotationsAttribute) method.getAttribute(AnnotationsAttribute.visibleTag),
                includeInvisibleTag ? (AnnotationsAttribute) method.getAttribute(AnnotationsAttribute.invisibleTag) : null);
    }

    @Override
    public List<String> getParameterAnnotationNames(final MethodInfo method, final int parameterIndex) {
        List<String> result = new ArrayList<>();

        List<ParameterAnnotationsAttribute> parameterAnnotationsAttributes =
                Arrays.asList((ParameterAnnotationsAttribute) method.getAttribute(ParameterAnnotationsAttribute.visibleTag),
                        (ParameterAnnotationsAttribute) method.getAttribute(ParameterAnnotationsAttribute.invisibleTag));

        for (ParameterAnnotationsAttribute parameterAnnotationsAttribute : parameterAnnotationsAttributes) {
            if (parameterAnnotationsAttribute == null) {
                continue;
            }

            Annotation[][] annotations = parameterAnnotationsAttribute.getAnnotations();

            if (parameterIndex < annotations.length) {
                Annotation[] annotation = annotations[parameterIndex];
                result.addAll(getAnnotationNames(annotation));
            }
        }

        return result;
    }

    @Override
    public String getReturnTypeName(final MethodInfo method) {
        String descriptor = method.getDescriptor();
        descriptor = descriptor.substring(descriptor.lastIndexOf(")") + 1);
        return splitDescriptorToTypeNames(descriptor).get(0);
    }

    @Override
    public String getFieldName(final FieldInfo field) {
        return field.getName();
    }

    @Override
    public @Nullable ClassFile getOfCreateClassObject(AnnotationsScanner scanner, AnnotationsScannerFile file) {
        InputStream inputStream = null;

        try {
            inputStream = file.openInputStream();
            DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
            return new ClassFile(dis);
        } catch (Exception e) {
            scanner.getLogger().exception(e);
            scanner.getLogger().error("Could not create class file from " + file.getInternalPath());
        } finally {
            IOUtils.close(inputStream);
        }

        return null;
    }

    @Override
    public String getMethodModifier(MethodInfo method) {
        int accessFlags = method.getAccessFlags();
        return AccessFlag.isPrivate(accessFlags) ? "private" : AccessFlag.isProtected(accessFlags) ? "protected" : isPublic(accessFlags) ? "public" : "";
    }

    @Override
    public String getMethodKey(ClassFile cls, MethodInfo method) {
        return getMethodName(method) + "(" + ContentJoiner.on(", ").join(getParameterNames(method)) + ")";
    }

    @Override
    public String getMethodFullKey(ClassFile cls, MethodInfo method) {
        return getClassName(cls) + "." + getMethodKey(cls, method);
    }

    @Override
    public String getClassName(final ClassFile cls) {
        return cls.getName();
    }

    @Override
    public String getSuperclassName(final ClassFile cls) {
        return cls.getSuperclass();
    }

    @Override
    public List<String> getInterfacesNames(final ClassFile cls) {
        return Arrays.asList(cls.getInterfaces());
    }

    private List<String> getAnnotationNames(AnnotationsAttribute... annotationsAttributes) {
        List<String> result = new ArrayList<>();

        if (annotationsAttributes == null) {
            return result;
        }

        for (AnnotationsAttribute annotationsAttribute : annotationsAttributes) {
            if (annotationsAttribute == null) {
                continue;
            }

            for (Annotation annotation : annotationsAttribute.getAnnotations()) {
                result.add(annotation.getTypeName());
            }
        }

        return result;
    }

    private List<String> getAnnotationNames(Annotation[] annotations) {
        List<String> result = new ArrayList<>();

        for (Annotation annotation : annotations) {
            result.add(annotation.getTypeName());
        }

        return result;
    }

}