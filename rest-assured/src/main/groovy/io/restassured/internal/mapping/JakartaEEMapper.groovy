/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.internal.mapping

import io.restassured.internal.path.xml.mapping.XmlPathJakartaEEObjectDeserializer
import io.restassured.mapper.ObjectMapper
import io.restassured.mapper.ObjectMapperDeserializationContext
import io.restassured.mapper.ObjectMapperSerializationContext
import io.restassured.path.xml.mapper.factory.JakartaEEObjectMapperFactory
import io.restassured.path.xml.mapping.XmlPathObjectDeserializer

class JakartaEEMapper implements ObjectMapper {

    private final JakartaEEObjectMapperFactory factory

    private XmlPathObjectDeserializer deserializer

  JakartaEEMapper(JakartaEEObjectMapperFactory factory) {
        this.factory = factory
        deserializer = new XmlPathJakartaEEObjectDeserializer(factory)
    }

    Object deserialize(ObjectMapperDeserializationContext context) {
        deserializer.deserialize(context)
    }

    @SuppressWarnings('UnnecessaryQualifiedReference')
    Object serialize(ObjectMapperSerializationContext context) {
        def object = context.getObjectToSerialize()
        def charset = context.getCharset()
        jakarta.xml.bind.JAXBContext jaxbContext = factory.create(object.getClass(), charset)
        jakarta.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller()
        if (charset != null) {
            marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_ENCODING, charset)
        }
        StringWriter sw = new StringWriter()
        marshaller.marshal(object, sw)
        return sw.toString()
    }
}
