package com.sagrishin

import com.sagrishin.ptsadmapp.core.database.Patient
import com.sagrishin.ptsadmapp.domain.models.DomainPatient
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun <ID: Comparable<ID>, T: Entity<ID>> EntityClass<ID, T>.new(db: Database, init: T.() -> Unit): T =
    transaction(db) { new(init) }

fun <T, F> Pair<Iterable<T>, Iterable<F>>.iterator(): Iterator<Pair<T, F>> {
    val firstIterator = first.iterator()
    val secondIterator = second.iterator()
    return object : Iterator<Pair<T, F>> {
        override fun hasNext(): Boolean = firstIterator.hasNext() && secondIterator.hasNext()
        override fun next(): Pair<T, F> = firstIterator.next() to secondIterator.next()
    }
}

fun <T, F> zip(l1: Iterable<T>, l2: Iterable<F>): Iterator<Pair<T, F>> = (l1 to l2).iterator()

fun assertContains(actual: String, entry: String, ignoreCase: Boolean = false) {
    assert(actual.contains(entry, ignoreCase))
}

fun toDomainModel(it: TestPatient): DomainPatient {
    return DomainPatient(
        id = 0,
        name = it.firstName,
        surname = it.lastName,
        phoneNumber = it.phoneNumber,
        appointments = listOf()
    )
}

fun toDomainModel(it: Patient): DomainPatient {
    return DomainPatient(
        id = it.id.value,
        name = it.name,
        surname = it.surname,
        phoneNumber = it.phoneNumber,
        appointments = listOf()
    )
}
