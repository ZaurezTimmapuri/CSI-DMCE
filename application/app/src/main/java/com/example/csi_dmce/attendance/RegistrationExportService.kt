package com.example.csi_dmce.attendance

import android.content.Context
import android.net.Uri
import com.example.csi_dmce.database.Event
import com.example.csi_dmce.database.EventWrapper
import com.example.csi_dmce.database.Student
import com.example.csi_dmce.database.StudentWrapper
import com.example.csi_dmce.utils.CsvWriter
import kotlinx.coroutines.runBlocking
import java.util.*

class RegistrationExportService {
    companion object {
        fun writeRegistrantsData(ctx: Context, eventId: String): Uri {
            // The data that will be written to the CSV file.
            val csvData: MutableList<List<String>> = mutableListOf()

            val eventObject: Event = runBlocking { EventWrapper.getEvent(eventId)!! }
            val registrants: MutableList<String>  = eventObject.registrants!!

            for (registrant in registrants) {
                val registrantObject: Student = runBlocking { StudentWrapper.getStudent(registrant)!! }
                val registrantData: List<String> = listOf(
                    registrantObject.name!!,
                    registrantObject.academic_year!!,
                    registrantObject.department!!,
                    registrantObject.division!!,
                    registrantObject.roll_number.toString()
                )

                csvData.add(registrantData)
            }

            val csvHeader: List<String> = listOf(
                "Name",
                "Academic Year",
                "Department",
                "Division",
                "Roll number"
            )

            return CsvWriter.writeCsv(ctx ,csvHeader, Collections.unmodifiableList(csvData))
        }
    }
}