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
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.ninja_squad.dbsetup.operation.Operation;

import sample.dto.Company;
import sample.util.CommonOperations;
import sample.util.TestUtil;

/**
 * @author t-aoyagi
 */
@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CompanyRepositoryTest {

    private static DbSetupTracker TRACKER = new DbSetupTracker();

    @Autowired
    private DataSource dataSource;
    @Autowired
    private CompanyRepository companyRepository;

    @Before
    public void before() {
        DbSetup setup = new DbSetup(getDestination(),
                    sequenceOf(CommonOperations.DELETE_ALL, CommonOperations.INSERT_COMPANY));
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
        assertThat(companyRepository.count(), is(2));
    }

    @Test
    public void testFindAll() {
        // テストデータ準備をスキップ (共通のテストデータを使うため)
        TRACKER.skipNextLaunch();

        // 実行
        List<Company> result = companyRepository.findAll();

        // 検証
        assertThat(result.isEmpty(), is(false));
        result.forEach(r -> {
           assertThat(r.getId(), is(greaterThan(0L)));
           assertThat(r.getCompanyCd(), is(notNullValue()));
           assertThat(r.getName(), is(notNullValue()));
           assertThat(r.getCreatedAt(), is(notNullValue()));
           assertThat(r.getUpdatedAt(), is(notNullValue()));
        });
    }

    @Test
    public void testCreate() {
        // テストデータ準備
        Company company = new Company();
        company.setCompanyCd("ZZ");
        company.setName("株式会社ZZZZZ");
        company.setRemarks("新規登録テスト");

        Date now = TestUtil.getNow();
        company.setCreatedAt(now);
        company.setUpdatedAt(now);

        // 実行
        int count = companyRepository.create(company);

        // 検証
        assertThat(count, is(1));
        assertThat(company.getId(), is(notNullValue()));

        assertThat(companyRepository.findById(company.getId()).toString(), is(company.toString()));
    }

    @Test
    public void testUpdate() throws Exception {
        // テストデータ準備
        // 更新対象オブジェクト
        Company company = new Company();
        company.setId(99999999L);
        company.setCompanyCd("ZZ");
        company.setName("株式会社ZZZZZ");
        company.setRemarks("ZZZZZ");

        Date date = new SimpleDateFormat("yyyyMMdd").parse("20151210");
        company.setCreatedAt(date);
        company.setUpdatedAt(date);

        // 予め更新対象レコードをテーブルにINSERT
        Operation op =
            insertInto("company")
                .columns("id", "company_cd", "name", "remarks", "created_at", "updated_at")
                .values(company.getId(),
                        company.getCompanyCd(),
                        company.getName(),
                        company.getRemarks(),
                        company.getCreatedAt(),
                        company.getUpdatedAt())
            .build();
        DbSetup setup = new DbSetup(getDestination(), op);
        TRACKER.launchIfNecessary(setup);

        // 実行
        company.setCompanyCd("zz-02");
        company.setName("株式会社zzzzz-02");
        company.setRemarks("zzzzz-02");
        company.setUpdatedAt(TestUtil.getNow());

        int count = companyRepository.update(company);

        // 検証
        assertThat(count, is(1));
        assertThat(companyRepository.findById(company.getId()).toString(), is(company.toString()));
    }
}
