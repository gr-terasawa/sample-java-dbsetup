/*
 * Copyright 2015 OPEN TONE Inc.
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
package sample.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import sample.dto.Foo;

/**
 * @author t-aoyagi
 */
public interface FooRepository {

    @Select("SELECT count(*) FROM foo")
    int count();

    @Select("SELECT * FROM foo")
    List<Foo> findAll();

    @Select("SELECT * FROM foo where id = #{value}")
    Foo findById(Long id);

    @Insert("INSERT INTO foo (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int create(Foo foo);

    @Update("UPDATE foo SET name = #{name} WHERE id = #{id}")
    int update(Foo foo);
}
