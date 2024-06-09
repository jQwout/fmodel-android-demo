package fraktal.io.android.demo.shared.repository

interface AdminRepository {

    val adminId: Long
    val adminName: String

    class HardcodedAdminRepository : AdminRepository {
        override val adminId: Long = Long.MIN_VALUE
        override val adminName: String = "Admin"
    }
}