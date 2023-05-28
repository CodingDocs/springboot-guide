package github.snailclimb.jpademo.repository;

import github.snailclimb.jpademo.model.po.Person;
import github.snailclimb.jpademo.model.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByName(String name);

    List<Person> findByAgeGreaterThan(int age);

    @Query("select p from Person p where p.name = :name")
    Optional<Person> findByNameCustomeQuery(@Param("name") String name);

    @Query("select p.name from Person p where p.id = :id")
    String findPersonNameById(@Param("id") Long id);

    /**
     * 自定义 sql 语句更新操作
     */
    @Modifying
    @Transactional
    @Query("update Person p set p.name = ?1 where p.id = ?2")
    void updatePersonNameById(String name, Long id);

    /**
     * 连表查询
     */
    @Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " + "from Person p left join Company c on  p.companyId=c.id " + "left join School s on p.schoolId=s.id " + "where p.id=:personId")
    Optional<UserDTO> getUserInformation(@Param("personId") Long personId);

    @Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " + "from Person p left join Company c on  p.companyId=c.id " + "left join School s on p.schoolId=s.id ", countQuery = "select count(p.id) " + "from Person p left join Company c on  p.companyId=c.id " + "left join School s on p.schoolId=s.id ")
    Page<UserDTO> getUserInformationList(Pageable pageable);

    @Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " + "from Person p left join Company c on  p.companyId=c.id " + "left join School s on p.schoolId=s.id " + "where p.name IN :peopleList")
    List<UserDTO> filterUserInfo(List peopleList);

    @Query(value = "select new github.snailclimb.jpademo.model.dto.UserDTO(p.name,p.age,c.companyName,s.name) " + "from Person p left join Company c on  p.companyId=c.id " + "left join School s on p.schoolId=s.id " + "where p.age between :small and :big")
    List<UserDTO> filterUserInfoByAge(int small, int big);
}
