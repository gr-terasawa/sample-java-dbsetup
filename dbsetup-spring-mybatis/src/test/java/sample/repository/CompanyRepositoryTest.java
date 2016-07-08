package sample.repository;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

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
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;

import sample.dto.Company;
import sample.dto.condition.CompanyCondition;
import sample.util.CommonOperations;
import sample.util.TestUtil;

/**
 * {@link CompanyRepository}のテストクラス.
 * @author t-aoyagi
 */
@ContextConfiguration(locations = {"classpath:test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CompanyRepositoryTest {

    /** テストデータ作成オブジェクト. */
    private static DbSetupTracker TRACKER = new DbSetupTracker();

    /** このテストクラスで作成するテストデータの総件数. */
    private static int COUNT_COMPANY_FOR_TEST_FIND = 9000;

    /**
     * {@link CompanyRepository#find(CompanyCondition)} 用のテストデータ.
     */
    private static Operation INSERT_COMPANY_FOR_TEST_FIND = insertInto("company")
            // 列 company_cd に "code-0001", "code-0002" といった連続した値を格納する指定
            .withGeneratedValue("company_cd",
                    ValueGenerators.stringSequence("code-").startingAt(1L).withLeftPadding(4))
            // 列 name も company_cd と同様
            .withGeneratedValue("name",
                    ValueGenerators.stringSequence("name-").startingAt(1L).withLeftPadding(4))
            // 列 remarks, created_at, updated_at には固定値を格納する
            .columns("remarks", "created_at", "updated_at")
            .repeatingValues("TEST-FIND", "2016-01-01", "2016-01-01")
            // 繰り返し回数の指定. この場合、COUNT_COMPANY_FOR_TEST_FIND 件分のレコードが作成されることになる
            .times(COUNT_COMPANY_FOR_TEST_FIND)
            .build();

    @Autowired
    private DataSource dataSource;
    @Autowired
    private CompanyRepository companyRepository;

    /**
     * 各テストの事前処理.
     */
    @Before
    public void before() {
        DbSetup setup = new DbSetup(getDestination(),
                    sequenceOf(CommonOperations.DELETE_ALL,     // 全データを削除
                                CommonOperations.INSERT_COMPANY,// システム共通のテストデータを作成
                                INSERT_COMPANY_FOR_TEST_FIND)); // このテストクラス固有のテストデータを作成
        TRACKER.launchIfNecessary(setup);
    }

    /**
     * テストデータセットアップの操作先(データベースのこと)を表すオブジェクトを返す.
     * <br/>
     * {@link DbSetup}ではこのオブジェクトのことを {@link Destination} と呼ぶ.
     * @return
     */
    private Destination getDestination() {
        return DataSourceDestination.with(dataSource);
    }

    /**
     * @see CompanyRepository#count()
     */
    @Test
    public void testCount() {
        // データを更新しないテストケースでは DbSetupTracker#skipNextLaunch(); を呼び出しておく
        // これにより、次の(＝別メソッドの)テストの事前処理で DbSetupTracker#launchIfNecessary() が
        // 呼び出されてもテストデータ作成処理が実行されなくなる
        TRACKER.skipNextLaunch();

        // 実行 & 検証
        assertThat(companyRepository.count(), is(greaterThan(0)));
    }

    /**
     * @see CompanyRepository#findAll()
     */
    @Test
    public void testFindAll() {
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

    /**
     * @see CompanyRepository#find(CompanyCondition)
     */
    @Test
    public void testFindWithCompanyCd() {
        TRACKER.skipNextLaunch();

        // 実行
        CompanyCondition condition = new CompanyCondition();
        condition.setCompanyCd("code-0001");
        List<Company> result = companyRepository.find(condition);

        // 検証
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getCompanyCd(), is(condition.getCompanyCd()));
    }

    /**
     * @see CompanyRepository#find(CompanyCondition)
     */
    @Test
    public void testFindWithCompanyCdNotFound() {
        TRACKER.skipNextLaunch();

        // 実行
        CompanyCondition condition = new CompanyCondition();
        condition.setCompanyCd("code-9999");
        List<Company> result = companyRepository.find(condition);

        // 検証
        assertThat(result.isEmpty(), is(true));
    }

    /**
     * @see CompanyRepository#find(CompanyCondition)
     */
    @Test
    public void testFindWithRemarks() {
        TRACKER.skipNextLaunch();

        // 実行
        CompanyCondition condition = new CompanyCondition();
        condition.setRemarks("TEST-FIND");
        List<Company> result = companyRepository.find(condition);

        // 検証
        assertThat(result.size(), is(COUNT_COMPANY_FOR_TEST_FIND));
        result.forEach(r -> {
            assertThat(r.getId(), is(notNullValue()));
            assertThat(r.getCompanyCd(), is(startsWith("code-")));
            assertThat(r.getName(), is(startsWith("name-")));
            assertThat(r.getRemarks(), is(condition.getRemarks()));
            assertThat(r.getCreatedAt(), is(notNullValue()));
            assertThat(r.getUpdatedAt(), is(notNullValue()));
        });
    }

    /**
     * @see CompanyRepository#create(Company)
     */
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

    /**
     * @see CompanyRepository#update(Company)
     */
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
