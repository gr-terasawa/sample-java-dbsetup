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
package sample.util;

import static com.ninja_squad.dbsetup.Operations.*;

import com.ninja_squad.dbsetup.operation.Operation;
/**
 * @author t-aoyagi
 */
public class CommonOperations {

    /** 全テーブルのレコードを削除するOperation. */
    public static Operation DELETE_ALL =
            deleteAllFrom("foo", "section_employee", "employee", "section", "company");

    /** 会社データ投入Operation. */
    public static Operation INSERT_COMPANY =
            insertInto("company")
                .columns("id", "company_cd", "name", "remarks", "created_at", "updated_at")
                .values(1L, "AC", "A建設", null, "2016-07-05", "2016-07-05")
                .values(2L, "MB", "M商事", null, "2016-01-20", "2016-06-30")
            .build();

    /** 部門データ投入Operation. */
    public static Operation INSERT_SECTION =
            insertInto("section")
                .columns("id", "section_cd", "name", "company_id", "created_at", "updated_at")
                .values(1L, "BULD", "建設部", 1L, "2016-07-05", "2016-07-05")
                .values(2L, "IT", "情報システム部", 1L, "2016-01-20", "2016-06-30")
                .values(3L, "HR", "人事部", 2L, "2016-07-05", "2016-07-05")
            .build();

    /** 従業員データ投入Operation. */
    public static Operation INSERT_EMPLOYEE = sequenceOf(
            insertInto("employee")
                .columns("id", "emp_no", "name", "mail_address", "created_at", "updated_at")
                .values(1L, "990001", "塚本 顕", "t.a@example.com", "2016-07-05", "2016-07-05")
                .values(2L, "060001", "島田 二郎", null, "2016-01-20", "2016-06-30")
                .values(3L, "A00-0001", "沖 朋美", "o.t@example.com", "2016-07-05", "2016-07-05")
                .values(4L, "A00-0001", "三ノ宮 五郎", "s.g@example.com", "2016-07-05", "2016-07-05")
                .values(5L, "A00-0001", "青山 譲", null, "2016-07-05", "2016-07-05")
            .build(),
            insertInto("section_employee")
                .columns("id", "section_id", "employee_id", "created_at", "updated_at")
                .values(1L, 1L, 1L, "2016-07-05", "2016-07-05")
                .values(2L, 1L, 5L, "2016-07-05", "2016-07-05")
                .values(3L, 3L, 2L, "2016-07-05", "2016-07-05")
                .values(4L, 2L, 1L, "2016-07-05", "2016-07-05")
                .values(5L, 2L, 3L, "2016-07-05", "2016-07-05")
            .build()
            );

    /** 全データ投入Operation. */
    public static Operation INSERT_ALL = sequenceOf(
            INSERT_COMPANY,
            INSERT_SECTION,
            INSERT_EMPLOYEE);
}

