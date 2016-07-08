package sample.dto.condition;

import lombok.Data;

/**
 * 会社の検索条件.
 * @author t-aoyagi
 */
@Data
public class CompanyCondition {
    /** 会社コード. */
    private String companyCd;
    /** 名称. */
    private String name;
    /** 備考. */
    private String remarks;
}
