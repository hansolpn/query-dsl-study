package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.example.study.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.study.entity.QMember.member;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class IMemberRepositoryTest {

    @Autowired
    IMemberRepository IMemberRepository;

    @Autowired
    ITeamRepository teamRepository;
    
    @Autowired
    EntityManager em; // JPA관리 핵심 객체
    
    //QueryDSL로 쿼리문을 작성하기 위한 핵심 객체
    JPAQueryFactory factory;

    @BeforeEach
    void settingObject() {
        factory = new JPAQueryFactory(em);

    }

    void testInsertData() {
        Team teamA = Team.builder()
                .name("teamA")
                .build();
        Team teamB = Team.builder()
                .name("teamB")
                .build();

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .team(teamA)
                .build();
        Member member2 = Member.builder()
                .userName("member2")
                .age(20)
                .team(teamA)
                .build();
        Member member3 = Member.builder()
                .userName("member3")
                .age(30)
                .team(teamB)
                .build();
        Member member4 = Member.builder()
                .userName("member4")
                .age(40)
                .team(teamB)
                .build();

        IMemberRepository.save(member1);
        IMemberRepository.save(member2);
        IMemberRepository.save(member3);
        IMemberRepository.save(member4);
    }

    @Test
    @DisplayName("testJPA")
    void testJPA() {
        List<Member> members = IMemberRepository.findAll();

        members.forEach(System.out::println);
    }

    @Test
    @DisplayName("testJPQL")
    void testJPQL() {
        //given
        String jpalQuery = "SELECT m FROM Member m WHERE m.userName = :userName";

        //when
        // EntityManager를 활용하여 직접 jpql을 작성하고, 파라미터를 설정할 수 있음
        Member foundMember = em.createQuery(jpalQuery, Member.class)
                .setParameter("userName", "member2")
                .getSingleResult();

        //then
        assertEquals("teamA", foundMember.getTeam().getName());
        System.out.println("\n\n\n");
        System.out.println("foundMember = " + foundMember);
        System.out.println("foundMember.getTeam() = " + foundMember.getTeam());
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("testQueryDSL")
    void testQueryDSL() {
        //given
        QMember m = member;

        //when
        Member foundMember = factory
                .select(m)
                .from(m)
                .where(m.userName.eq("member1"))
                .fetchOne();

        //then
        assertEquals("member1", foundMember.getUserName());
    }

    @Test
    @DisplayName("search")
    void search() {
        //given
//        QMember m = QMember.member;
        String searchName = "member2";
        int seachAge = 20;

        //when
        Member foundMember = factory
                .selectFrom(member)
                .where(member.userName.eq(searchName)
                        , member.age.eq(seachAge)
                        /* .and(member.age.eq(seachAge)) */)
                .fetchOne();

        //then
        assertNotNull(foundMember);
        assertEquals("teamA", foundMember.getTeam().getName());

        /*
         JPAQueryFactory를 이용해서 쿼리문을 조립한 후 반환 인자를 결정합니다.
         - fetchOne(): 단일 건 조회. 여러 건 조회시 예외 발생.
         - fetchFirst(): 단일 건 조회. 여러 개가 조회돼도 첫 번째 값만 반환
         - fetch(): List 형태로 반환
         * JPQL이 제공하는 모든 검색 조건을 queryDsl에서도 사용 가능
         *
         * member.userName.eq("member1") // userName = 'member1'
         * member.userName.ne("member1") // userName != 'member1'
         * member.userName.eq("member1").not() // userName != 'member1'
         * member.userName.isNotNull() // 이름이 is not null
         * member.age.in(10, 20) // age in (10,20)
         * member.age.notIn(10, 20) // age not in (10,20)
         * member.age.between(10, 30) // age between 10, 30
         * member.age.goe(30) // age >= 30
         * member.age.gt(30) // age > 30
         * member.age.loe(30) // age <= 30
         * member.age.lt(30) // age < 30
         * member.userName.like("_김%") // userName LIKE '_김%'
         * member.userName.contains("김") // userName LIKE '%김%'
         * member.userName.startsWith("김") // userName LIKE '김%'
         * member.userName.endsWith("김") // userName LIKE '%김'
         */
    }

    @Test
    @DisplayName("결과 반환하기")
    void testFetchResult() {
        //fetch
        List<Member> fetch1 = factory.selectFrom(member).fetch();

        System.out.println("\n\n===== fetch =====");
        fetch1.forEach(System.out::println);

        // fetchOne
        Member fetch2 = factory.selectFrom(member)
                .where(member.id.eq(3L))
                .fetchOne();

        System.out.println("\n\n===== fetch2 =====");
        System.out.println("fetch2 = " + fetch2);

        // fetchFirst
        Member fetch3 = factory.selectFrom(member)
                .fetchFirst();

        System.out.println("\n\n===== fetch3 =====");
        System.out.println("fetch3 = " + fetch3);

    }

    @Test
    @DisplayName("QueryDSL custom 설정 확인")
    void queryDslCustom() {
        //given
        String name = "member4";

        //when
        List<Member> result = IMemberRepository.findByName(name);

        //then
        assertEquals(1, result.size());
        assertEquals("teamB", result.get(0).getTeam().getName());
    }
}