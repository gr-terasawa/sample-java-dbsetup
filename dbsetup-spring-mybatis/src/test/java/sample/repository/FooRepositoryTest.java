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

import static com.ninja_squad.dbsetup.Operations.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;

import sample.dto.Foo;

/**
 * @author t-aoyagi
 */
@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class FooRepositoryTest {

    private static DbSetupTracker TRACKER = new DbSetupTracker();
    private static Operation DELETE_ALL = deleteAllFrom("foo");
    private static Operation INSERT_FOO = sequenceOf(
                // テーブル foo にレコード100件作成
                insertInto("foo")
                    .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1L))
                    .columns("name")
                    .repeatingValues("test").times(100)
                    .build()
            );

    @Autowired
    private DataSource dataSource;
    @Autowired
    private FooRepository fooRepository;

    @Before
    public void before() {
        DbSetup setup = new DbSetup(getDestination(), sequenceOf(DELETE_ALL, INSERT_FOO));
        TRACKER.launchIfNecessary(setup);
    }

    private Destination getDestination() {
        return new DataSourceDestination(dataSource);
    }

    @Test
    public void testCount() {
        // テストデータ準備をスキップ (共通のテストデータを使うため)
        TRACKER.skipNextLaunch();

        // 実行 & 検証
        assertThat(fooRepository.count(), is(100));
    }

    @Test
    public void testFindAll() {
        // テストデータ準備をスキップ (共通のテストデータを使うため)
        TRACKER.skipNextLaunch();

        // 実行
        List<Foo> result = fooRepository.findAll();

        // 検証
        assertThat(result.isEmpty(), is(false));
        result.forEach(r -> {
           assertThat(r.getId(), is(greaterThan(0L)));
           assertThat(r.getName(), is(startsWith("test")));
        });
    }

    @Test
    public void testCreate() {
        // テストデータ準備
        Foo foo = new Foo();
        foo.setName("Taro Yamada");

        // 実行
        int count = fooRepository.create(foo);

        // 検証
        assertThat(count, is(1));
        assertThat(foo.getId(), is(notNullValue()));

        assertThat(fooRepository.findById(foo.getId()).toString(), is(foo.toString()));
    }

    @Test
    public void testUpdate() {
        // テストデータ準備
        // 更新対象オブジェクト
        Foo foo = new Foo();
        foo.setId(99999999L);
        foo.setName("testdata for testUpdate");
        // 予め更新対象レコードをテーブルにINSERT
        Operation op =
                insertInto("foo")
                        .columns("id", "name")
                        .values(foo.getId(), foo.getName())
                        .build();
        DbSetup setup = new DbSetup(getDestination(), op);
        TRACKER.launchIfNecessary(setup);

        // 実行
        foo.setName("xxxxxxxxxxx");
        int count = fooRepository.update(foo);

        // 検証
        assertThat(count, is(1));
        assertThat(fooRepository.findById(foo.getId()).toString(), is(foo.toString()));
    }
}
