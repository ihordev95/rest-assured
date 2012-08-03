/*
 * Copyright 2012 the original author or authors.
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

package com.jayway.restassured.internal.mapping

import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.XmlRootElement

import com.jayway.restassured.internal.http.CharsetExtractor
import com.jayway.restassured.mapper.ObjectMapper
import com.jayway.restassured.mapper.ObjectMapperDeserializationContext
import com.jayway.restassured.mapper.ObjectMapperSerializationContext

class JaxbMapper implements ObjectMapper {
	private static JAXBContextFactory jaxbContextFactory=new JAXBContextFactory()

	def static register(JAXBContextFactory jaxbContextFactory) {
		JaxbMapper.jaxbContextFactory = jaxbContextFactory
	}

	private JAXBContext createJAXBContext(Class clazz, String charset) {
		return JaxbMapper.jaxbContextFactory.createJAXBContext(clazz, charset)
	}

	def Object serialize(ObjectMapperSerializationContext context) {
        def object = context.getObjectToSerialize();
        def charset = context.getCharset()
        JAXBContext jaxbContext = createJAXBContext(object.getClass(), charset)
		Marshaller marshaller = jaxbContext.createMarshaller()
		if (charset != null) {
			marshaller.setProperty(Marshaller.JAXB_ENCODING, charset)
		}
		StringWriter sw = new StringWriter()
		marshaller.marshal(object, sw)
		return sw.toString()
	}

	def Object deserialize(ObjectMapperDeserializationContext context) {
        def cls = context.getType();
        def object = context.getObjectToDeserializeAs(String.class);
		JAXBContext jaxbContext = createJAXBContext(cls, context.getCharset())

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller()
		def reader = new StringReader(object)
		if (cls.isAnnotationPresent(XmlRootElement.class)) {
			return unmarshaller.unmarshal(reader)
		} else {
			JAXBElement jaxbElement = unmarshaller.unmarshal(reader, cls)
			return jaxbElement.getValue()
		}
	}
}
