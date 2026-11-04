package com.example.turnofacil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.turnofacil.data.local.entity.AppointmentEntity
import com.example.turnofacil.domain.model.EstadoCita
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

/**
 * Acceso a citas.
 *
 * Reglas de negocio implementadas aquí:
 * - una cita nueva nace [EstadoCita.PENDIENTE] (valor por defecto de la entidad);
 * - solo las citas Pendiente/Confirmada ocupan un horario;
 * - una cita Cancelada libera el horario.
 */
@Dao
interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: AppointmentEntity): Long

    @Query(
        """
        UPDATE appointment
        SET status = :status,
            sync_state = 'PENDIENTE',
            updated_at = :updatedAt
        WHERE id = :id
        """,
    )
    suspend fun updateStatus(id: Long, status: EstadoCita, updatedAt: Long): Int

    /** Reprograma fecha y hora; el repositorio valida antes el nuevo horario. */
    @Query(
        """
        UPDATE appointment
        SET date = :date,
            time = :time,
            sync_state = 'PENDIENTE',
            updated_at = :updatedAt
        WHERE id = :id
        """,
    )
    suspend fun reschedule(
        id: Long,
        date: LocalDate,
        time: LocalTime,
        updatedAt: Long,
    ): Int

    @Query("SELECT * FROM appointment WHERE id = :id")
    suspend fun getById(id: Long): AppointmentEntity?

    /** Cancelación: la cita sigue existiendo y su horario queda libre. */
    @Query(
        """
        UPDATE appointment
        SET status = 'CANCELADA',
            sync_state = 'PENDIENTE',
            updated_at = :updatedAt
        WHERE id = :id
        """,
    )
    suspend fun cancel(id: Long, updatedAt: Long): Int

    /** Agenda del día, ordenada por hora (CU3). */
    @Query("SELECT * FROM appointment WHERE date = :date ORDER BY time")
    fun observeByDate(date: LocalDate): Flow<List<AppointmentEntity>>

    /**
     * ¿Ya hay una cita Pendiente o Confirmada en esa fecha y hora?
     *
     * Las citas Canceladas no cuentan, por lo que liberan el horario.
     */
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM appointment
            WHERE date = :date
              AND time = :time
              AND status IN ('PENDIENTE', 'CONFIRMADA')
        )
        """,
    )
    suspend fun isSlotBusy(date: LocalDate, time: LocalTime): Boolean

    /**
     * Igual que [isSlotBusy], pero ignora la cita que se está reprogramando.
     */
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM appointment
            WHERE date = :date
              AND time = :time
              AND status IN ('PENDIENTE', 'CONFIRMADA')
              AND id != :excludeId
        )
        """,
    )
    suspend fun isSlotBusyExcluding(
        date: LocalDate,
        time: LocalTime,
        excludeId: Long,
    ): Boolean
}
