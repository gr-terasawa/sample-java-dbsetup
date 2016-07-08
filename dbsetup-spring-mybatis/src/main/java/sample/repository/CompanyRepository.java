package sample.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.jdbc.SQL;

import sample.dto.Company;
import sample.dto.condition.CompanyCondition;

/**
 * 会社リポジトリ.
 * @author t-aoyagi
 */
public interface CompanyRepository {

    /**
     * 登録済みの会社の全件数を返す.
     * @return 件数
     */
    @Select("SELECT count(*) FROM company")
    int count();

    /**
     * すべての会社を返す.
     * @return 検索結果
     */
    @Select("SELECT * FROM company")
    List<Company> findAll();

    /**
     * 検索条件に該当する会社を返す.
     * @param condition 検索条件
     * @return 検索結果. 見つからない場合は空のリスト
     */
    @SelectProvider(type = CompanyRepository.SqlBuilder.class, method = "createFindQuery")
    List<Company> find(CompanyCondition condition);

    /**
     * 指定されたidに該当する会社を返す.
     * @param id
     * @return 検索結果. 見つからない場合はnull
     */
    @Select("SELECT * FROM company WHERE id = #{value}")
    Company findById(Long id);

    /**
     * 新しい会社を登録する.
     * @param company 会社
     * @return 登録件数. 通常は1が返される
     */
    @Insert("INSERT INTO company (company_cd, name, remarks, created_at, updated_at) "
            + "VALUES (#{companyCd}, #{name}, #{remarks}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int create(Company company);

    /**
     * idで会社を更新する.
     * @param company 会社
     * @return 更新件数. 更新に成功した場合は1が返される.
     */
    @Update("UPDATE company SET "
            + "company_cd = #{companyCd},"
            + "name = #{name}, "
            + "remarks = #{remarks}, "
            + "updated_at = #{updatedAt} "
            + "WHERE id = #{id}")
    int update(Company company);

    /**
     * 会社関連のSQL生成クラス.
     */
    public static class SqlBuilder {
        /**
         * 条件に該当する会社を検索するためのSQL文を生成する.
         * @param condition 検索条件
         * @return SQL文
         */
        public static String createFindQuery(final CompanyCondition condition) {
            return new SQL() {{
                SELECT("*");
                FROM("company");
                if (condition.getCompanyCd() != null) {
                    WHERE("company_cd = #{companyCd}");
                }
                if (condition.getName() != null) {
                    WHERE("name = #{name}");
                }
                if (condition.getRemarks() != null) {
                    WHERE("remarks = #{remarks}");
                }
                ORDER_BY("company_cd ASC");
            }}.toString();
        }
    }
}
