package com.tausoft.kidsgarden.data

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.tausoft.kidsgarden.dao.DaysOffDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class DaysOffLocalDataSource @Inject constructor(private val daysOffDao: DaysOffDao): DaysOffDataSource {

    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)

    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun addDayOff(day: DayOff) {
        executorService.execute {
            daysOffDao.insert(day)
        }
    }

    override fun getDaysOff(dateFrom: Int, dateTo: Int): LiveData<List<DayOff>> =
        daysOffDao.getDaysOff( dateFrom, dateTo)

    override fun getDaysOffCount(dateFrom: Int, dateTo: Int, callback: (Int) -> Unit) {
        executorService.execute {
            val daysOffCount = daysOffDao.getDaysOffCount( dateFrom, dateTo)
            mainThreadHandler.post { callback( daysOffCount ) }
        }
    }
}