package sample.dto;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * 従業員を表すクラス.
 * @author t-aoyagi
 */
@Data
@ToString
public class Employee {

    /** id. */
    private Long id;
    /** 従業員番号. */
    private String empNo;
    /** 名前. */
    private String name;
    /** メールアドレス. */
    private String mailAddress;
    /** 作成日時. */
    private Date createdAt;
    /** 更新日時. */
    private Date updatedAt;
}
