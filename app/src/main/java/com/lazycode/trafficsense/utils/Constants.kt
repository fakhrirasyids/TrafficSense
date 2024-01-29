package com.lazycode.trafficsense.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale

object Constants {
    val INDONESIA_LATLNG = LatLng(-6.200000, 106.816666)

    fun alertDialogMessage(context: Context, message: String, title: String? = null) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)

        with(builder)
        {
            if (title != null) {
                setTitle(title)
            }
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    fun parseAddress(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String {
        val geoLocation = Geocoder(context).getFromLocation(latitude, longitude, 1)

        return if (geoLocation!!.size > 0) {
            val address = geoLocation[0].getAddressLine(0)
            StringBuilder("📍 $address").toString()
        } else {
            StringBuilder("Location Unknown").toString()
        }
    }

    fun getOverlayBitmap(text: String, color: Int): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = 40f
        paint.color = color
        paint.typeface = Typeface.DEFAULT_BOLD

        val textWidth = paint.measureText(text)
        val textHeight = paint.descent() - paint.ascent()

        val bitmap =
            Bitmap.createBitmap(textWidth.toInt(), textHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawText(text, 0f, -paint.ascent(), paint)

        return bitmap
    }

    fun BottomSheetDialogFragment.setTransparentBackground() {
        dialog?.apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val tempFile = File.createTempFile(
            SimpleDateFormat(
                "dd-MMM-yyyy",
                Locale.US
            ).format(System.currentTimeMillis()),
            ".jpg",
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        val inputStream = context.contentResolver.openInputStream(uri) as InputStream
        val outputStream: OutputStream = FileOutputStream(tempFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return tempFile
    }

    fun openWhatsappHelper(
        activity: Activity,
        context: Context,
        driverName: String,
        contact: String
    ) {
        val newContact = if (contact[0] == '0') {
            contact.replaceFirst("0", "62")
        } else {
            contact
        }

        Log.e("KONTOL", "openWhatsappHelper: $newContact")

        val url =
            "https://api.whatsapp.com/send?phone=$newContact&text=Halo%20$driverName,%20Saya%20ingin%20berbincang%20mengenai%20tawaran%20carpool%20anda%20di%20aplikasi%20TrafficSense!"

        try {
            val pm = context.packageManager
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            activity.startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            alertDialogMessage(
                context,
                "Whatsapp tidak terinstall di aplikasi anda!",
                "Peringatan"
            )
            e.printStackTrace()
        }
    }

    fun milesToKilometers(miles: Double): Double {
        val conversionFactor = 1.60934
        val bd = BigDecimal(miles * conversionFactor).setScale(2, RoundingMode.HALF_UP)
        return BigDecimal((bd.toDouble() / 1000)).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    enum class Location {
        isPicked, Longitude, Latitude
    }
}