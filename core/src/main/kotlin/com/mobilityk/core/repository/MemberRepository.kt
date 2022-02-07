package com.mobilityk.core.repository

import com.mobilityk.core.domain.Member
import com.mobilityk.core.domain.QMember.member
import com.mobilityk.core.dto.MemberDTO
import com.mobilityk.core.dto.mapper.MemberMapper
import com.mobilityk.core.enumuration.ROLE
import com.mobilityk.core.repository.search.MemberSearchOption
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface MemberRepository: JpaRepository<Member, Long>, MemberRepositoryCustom {
    fun findByEmailAddress(email: String): Optional<Member>
    fun findAllByRolesIn(roles: List<ROLE>): List<Member>
    fun findAllByRolesInAndBranch(roles: List<ROLE>, branch: String): List<Member>
    fun findByPhone(phone: String): Optional<Member>
    fun findByRefreshToken(refreshToken: String): Optional<Member>
}

interface MemberRepositoryCustom {
    fun findAllBySearch(searchOption: MemberSearchOption, pageable: Pageable): Page<Member>
    fun findAllBySearch(searchOption: MemberSearchOption): List<Member>
}

class MemberRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val memberMapper: MemberMapper
): MemberRepositoryCustom {

    override fun findAllBySearch(searchOption: MemberSearchOption, pageable: Pageable): Page<Member> {
        val fetchResults = jpaQueryFactory
            .selectFrom(member)
            .where(
                search(searchOption)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable.sort).toTypedArray())
            .fetchResults()

        val content = fetchResults.results
        val totalCount = fetchResults.total

        return PageImpl<Member>(
            content.toMutableList(),
            pageable, totalCount
        )
    }

    override fun findAllBySearch(searchOption: MemberSearchOption): List<Member> {
        return jpaQueryFactory
            .selectFrom(member)
            .where(
                search(searchOption)
            )
            .fetch()
    }

    private fun search(searchOption: MemberSearchOption): BooleanBuilder? {
        val builder = BooleanBuilder()
        searchOption.search?.let { search ->
            builder.and(member.emailAddress.contains(search)).or(member.name.contains(search))
                .or(member.id.like(search)).or(member.phone.contains(search))
        }
        searchOption.accessYn?.let { accessYn ->
            builder.and(member.accessYn.eq(accessYn))
        }
        searchOption.activated?.let { activated ->
            builder.and(member.activated.eq(activated))
        }
        searchOption.roles?.let { roles ->
            roles.forEach { role ->
                builder.and(member.roles.contains(role))
            }
        }
        searchOption.notInRole?.let { role ->
            builder.and(!member.roles.contains(role))
        }
        return builder
    }

    private fun emailLike(searchOption: MemberSearchOption) : BooleanExpression? {
        return searchOption.search?.let { search ->
            member.emailAddress.like(search)
        }
    }

    private fun nameLike(searchOption: MemberSearchOption) : BooleanExpression? {
        return searchOption.search?.let { search ->
            member.name.like(search)
        }
    }

    /*


    private fun empNameLike(empName: String?) : BooleanExpression? {
        return if (!empName.isNullOrEmpty()) callHistory.empName.contains(empName) else null
    }

    private fun carNameLike(carName: String?) : BooleanExpression? {
        return if (!carName.isNullOrEmpty()) callHistory.carName.contains(carName) else null
    }

    private fun typeEq(type: String?) : BooleanExpression? {
        return if (!type.isNullOrEmpty()) callHistory.type.eq(type) else null
    }

    private fun memoLike(memo: String?) : BooleanExpression? {
        return if (!memo.isNullOrEmpty()) callHistory.type.contains(memo) else null
    }

    private fun callContentLike(callContent: String?) : BooleanExpression? {
        return if (!callContent.isNullOrEmpty()) callHistory.callContent.contains(callContent) else null
    }

    private fun inOutIn(callTypes: List<InOut>?) : BooleanExpression? {
        return if (!callTypes.isNullOrEmpty()) {
            callHistory.inOut.`in`(callTypes)
        } else {
            null
        }
    }

    private fun customerCellNumberLike(customerCellNo: String?) : BooleanExpression? {
        return if (!customerCellNo.isNullOrEmpty()) callHistory.customer.cellNumber.contains(customerCellNo) else null
    }

     */
    private fun getOrderSpecifier(sort: Sort): MutableList<OrderSpecifier<*>> {
        var orders = mutableListOf<OrderSpecifier<*>>()
        sort.forEach { order ->
            var direction = Order.DESC
            if(order.isAscending) direction = Order.ASC
            orders.add(OrderSpecifier(direction , Expressions.path(Member::class.java , member , order.property)))
        }
        return orders
    }

}