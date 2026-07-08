package com.example.Expense_Split.Repository;

import com.example.Expense_Split.Entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    @Query("SELECT COUNT(m) FROM Group g JOIN g.users m WHERE g.id = :groupId")
    long countMembersByGroupId(@Param("groupId") int groupId);


}
//