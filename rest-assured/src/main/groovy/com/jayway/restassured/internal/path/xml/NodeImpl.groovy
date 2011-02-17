/*
 * Copyright 2011 the original author or authors.
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

package com.jayway.restassured.internal.path.xml

import com.jayway.restassured.path.xml.element.Node
import com.jayway.restassured.path.xml.element.NodeChildren

class NodeImpl extends NodeBase implements Node {

  def attributes = [:]
  def NodeChildren children = new NodeChildrenImpl()
  def name
  def value = null

  @Override
  Map<String, String> attributes() {
    return Collections.unmodifiableMap(attributes)
  }

  @Override
  String name() {
    return name
  }

  @Override
  Iterator<String> iterator() {
    if(!children.isEmpty()) {
      return children.iterator()
    } else {
      return new ValueIterator()
    }
  }

  public String toString () {
    def builder = new StringBuilder()
    if(children.isEmpty()) {
      builder.append(value)
    } else {
      def iterator = children.iterator()
      while(iterator.hasNext()) {
        def next = iterator.next()
        builder.append(next)
      }
    }
    builder.toString()
  }

  NodeChildren children() {
    return children
  }

  String getAttribute(String name) {
    return get(name)
  }

  float getFloat(String name) {
    return get(name)
  }

  double getDouble(String name) {
    return get(name)
  }

  char getChar(String name) {
    return get(name)
  }

  boolean getBoolean(String name) {
    return get(name)
  }

  long getLong(String name) {
    return get(name)
  }

  int getInt(String name) {
    return get(name)
  }

  short getShort(String name) {
    return get(name)
  }

  byte getByte(String name) {
    return get(name)
  }

  public <T> T get(String name) {
    if(name.startsWith("@")) {
      return attributes.get(name.substring(1))
    }
    return get(name, children.nodeIterator(), false)
  }

  @Override
  String value() {
    return value
  }

  @Override def <T> List<T> getList(String name) {
    return get(name, children.nodeIterator(), true)
  }


  class ValueIterator implements Iterator<String> {
    def hasNext = true
    @Override
    boolean hasNext() {
      return hasNext
    }

    @Override
    String next() {
      hasNext = false
      return value
    }

    @Override
    void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
