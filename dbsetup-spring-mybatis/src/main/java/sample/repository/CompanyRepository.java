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

import sample.dto.Company;

/**
 * @author t-aoyagi
 *
 * <p>
 * $Date$
 * $Rev$
 * $Author$
 */
public interface CompanyRepository {

    @Select("SELECT count(*) FROM company")
    int count();

    @Select("SELECT * FROM company")
    List<Company> findAll();

    @Select("SELECT * FROM company WHERE id = #{value}")
    Company findById(Long id);

    @Insert("INSERT INTO company (company_cd, name, remarks, created_at, updated_at) "
            + "VALUES (#{companyCd}, #{name}, #{remarks}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int create(Company company);

    @Update("UPDATE company SET "
            + "company_cd = #{companyCd},"
            + "name = #{name}, "
            + "remarks = #{remarks}, "
            + "updated_at = #{updatedAt} "
            + "WHERE id = #{id}")
    int update(Company company);
}
