package com.forpet.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.forpet.app.core.database.dao.PetDao
import com.forpet.app.core.database.dao.ScheduleDao
import com.forpet.app.core.database.dao.WalkPointDao
import com.forpet.app.core.database.dao.WalkSessionDao
import com.forpet.app.core.database.model.PetEntity
import com.forpet.app.core.database.model.ScheduleEntity
import com.forpet.app.core.database.model.WalkPointEntity
import com.forpet.app.core.database.model.WalkSessionEntity

@Database(
    entities = [
        ScheduleEntity::class,
        WalkSessionEntity::class,
        WalkPointEntity::class,
        PetEntity::class,
    ],
    version = 6,
    exportSchema = true,
)
abstract class ForPetDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun walkSessionDao(): WalkSessionDao
    abstract fun walkPointDao(): WalkPointDao
    abstract fun petDao(): PetDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE schedules ADD COLUMN excludedDates TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `walk_sessions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `startedAt` INTEGER NOT NULL,
                        `endedAt` INTEGER,
                        `distanceMeters` REAL NOT NULL DEFAULT 0.0,
                        `avgSpeedKmh` REAL NOT NULL DEFAULT 0.0,
                        `durationSeconds` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `walk_points` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `sessionId` INTEGER NOT NULL,
                        `latitude` REAL NOT NULL,
                        `longitude` REAL NOT NULL,
                        `recordedAt` INTEGER NOT NULL,
                        FOREIGN KEY(`sessionId`) REFERENCES `walk_sessions`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_walk_points_sessionId` ON `walk_points` (`sessionId`)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `pets` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `species` TEXT NOT NULL DEFAULT '',
                        `breed` TEXT NOT NULL DEFAULT '',
                        `gender` TEXT,
                        `birthdayEpochDay` INTEGER,
                        `firstMetDayEpochDay` INTEGER,
                        `weightKg` REAL,
                        `photoUri` TEXT
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pets ADD COLUMN walkGoalKm INTEGER NOT NULL DEFAULT 3")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE `pets_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `birthdayEpochDay` INTEGER,
                        `firstMetDayEpochDay` INTEGER,
                        `photoUri` TEXT,
                        `walkGoalKm` INTEGER NOT NULL DEFAULT 3
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `pets_new` (`id`, `name`, `birthdayEpochDay`, `firstMetDayEpochDay`, `photoUri`, `walkGoalKm`)
                    SELECT `id`, `name`, `birthdayEpochDay`, `firstMetDayEpochDay`, `photoUri`, `walkGoalKm` FROM `pets`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `pets`")
                db.execSQL("ALTER TABLE `pets_new` RENAME TO `pets`")
            }
        }
    }
}
