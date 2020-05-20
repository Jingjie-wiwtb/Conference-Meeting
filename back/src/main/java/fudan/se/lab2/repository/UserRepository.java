package fudan.se.lab2.repository;

import fudan.se.lab2.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    @Query(value = "select email,organization,region,username from user u where u.real_name=?1 and u.id <> ?2",nativeQuery = true)
    List<Map<String,Object>> findByRealname(String real_name, Long chair_id);

    //User findById(int id);

    //User findByUsernameAndPassword(String username, String password);
}

/*这里关键的地方在于方法的名字。由于使用了 JPA，无需手动构建 SQL 语句，而只需要按照规范提供方法的名字即可实现对数据库的增删改查。

如 findByUsername，就是通过 username 字段查询到对应的行，并返回给 User 类。

这里我们构建了两个方法，一个是通过用户名查询，一个是通过用户名及密码查询。
*/
//？？在Spring Boot项目中数据访问层无需提供实现，直接继承数据访问接口即可。
