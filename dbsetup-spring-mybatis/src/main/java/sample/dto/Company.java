package sample.dto;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * 会社を表すクラス.
 * @author t-aoyagi
 */
@Data
@ToString
public class Company {

    /** id. */
    private Long id;
    /** 会社コード. */
    private String companyCd;
    /** 名称. */
    private String name;
    /** 備考. */
    private String remarks;
    /** 作成日時. */
    private Date createdAt;
    /** 更新日時. */
    private Date updatedAt;
}
