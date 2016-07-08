package sample.dto;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * 部門を表すクラス.
 * @author t-aoyagi
 */
@Data
@ToString
public class Section {

    /** id. */
    private Long id;
    /** 部門コード. */
    private String sectionCd;
    /** 名称. */
    private String name;
    /** 会社id. */
    private Long companyId;
    /** 作成日時. */
    private Date createdAt;
    /** 更新日時. */
    private Date updatedAt;
}
