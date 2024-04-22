package com.dicoding.ticketingapps

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat

// membuat Custom View
class SeatsView  : View {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    // data class untuk menggambarkan sebuah kursi dari aplikasi tiket
    data class Seat(
        val id: Int,
        var x: Float? = 0F,
        var y: Float? = 0F,
        var name: String,
        var isBooked: Boolean
    )

   // menyediakan data untuk mendeklarasikan masing-masing kursi(menyimpan kumpulan informasi kursi yang ada)
    private val seats: ArrayList<Seat> = arrayListOf(
        Seat(id = 1, name = "A1", isBooked = false),
        Seat(id = 2, name = "A2", isBooked = false),
        Seat(id = 3, name = "B1", isBooked = false),
        Seat(id = 4, name = "A4", isBooked = false),
        Seat(id = 5, name = "C1", isBooked = false),
        Seat(id = 6, name = "C2", isBooked = false),
        Seat(id = 7, name = "D1", isBooked = false),
        Seat(id = 8, name = "D2", isBooked = false),
    )

    // agar posisi x dan y bisa ditentukan, panggil metode onMeasure dan deklarasikanlah nilai x dan y.
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        val halfOfHeight = height / 2
        val halfOfWidth = width / 2
        var value = -600F

        for (i in 0..7) {
            if (i.mod(2) == 0) {
                seats[i].apply {
                    x = halfOfWidth - 300F
                    y = halfOfHeight + value
                }
            } else {
                seats[i].apply {
                    x = halfOfWidth + 100F
                    y = halfOfHeight + value
                }
                value += 300F
            }
        }
    }


    var seat: Seat? = null

    private val backgroundPaint = Paint()
    private val armrestPaint = Paint()
    private val bottomSeatPaint = Paint()
    private val mBounds = Rect()
    private val numberSeatPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
    private val titlePaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)


    // menggambar object ke dalam Canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (seat in seats) {
            drawSeat(canvas, seat)
        }

        val text = "Silakan Pilih Kursi"

        titlePaint.apply {
            textSize = 40F
        }

        canvas.drawText(text, (width / 2F) - 197F, 100F, titlePaint)

    }

        private fun drawSeat(canvas: Canvas?, seat: Seat) {
            // Mengatur Warna ketika Sudah Dibooking
            if (seat.isBooked) {
                backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
                armrestPaint.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
                bottomSeatPaint.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
                numberSeatPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
            } else {
                backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.blue_500, null)
                armrestPaint.color = ResourcesCompat.getColor(resources, R.color.blue_700, null)
                bottomSeatPaint.color = ResourcesCompat.getColor(resources, R.color.blue_200, null)
                numberSeatPaint.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
            }

            // Menyimpan State
            canvas?.save()

            // Background
            canvas?.translate(seat.x as Float, seat.y as Float)

            val backgroundPath = Path()
            backgroundPath.addRect(0F, 0F, 200F, 200F, Path.Direction.CCW)
            backgroundPath.addCircle(100F, 50F, 75F, Path.Direction.CCW)
            canvas?.drawPath(backgroundPath, backgroundPaint)

            // Sandaran Tangan
            val armrestPath = Path()
            armrestPath.addRect(0F, 0F, 50F, 200F, Path.Direction.CCW)
            canvas?.drawPath(armrestPath, armrestPaint)
            canvas?.translate(150F, 0F)
            armrestPath.addRect(0F, 0F, 50F, 200F, Path.Direction.CCW)
            canvas?.drawPath(armrestPath, armrestPaint)

            // Bagian Bawah Kursi
            canvas?.translate(-150F, 175F)
            val bottomSeatPath = Path()
            bottomSeatPath.addRect(0F, 0F, 200F, 25F, Path.Direction.CCW)
            canvas?.drawPath(bottomSeatPath, bottomSeatPaint)

            // Menulis Nomor Kursi
            canvas?.translate(0F, -175F)
            numberSeatPaint.apply {
                textSize = 50F
                numberSeatPaint.getTextBounds(seat.name, 0, seat.name.length, mBounds)
            }
            canvas?.drawText(seat.name, 100F - mBounds.centerX(), 100F, numberSeatPaint)

            // Mengembalikan ke pengaturan sebelumnya
            canvas?.restore()
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val halfOfHeight = height / 2
        val halfOfWidth = width / 2

        val widthColumnOne = (halfOfWidth - 300F)..(halfOfWidth - 100F)
        val widthColumnTwo = (halfOfWidth + 100F)..(halfOfWidth + 300F)

        val heightRowOne = (halfOfHeight - 600F)..(halfOfHeight - 400F)
        val heightRowTwo = (halfOfHeight - 300F)..(halfOfHeight - 100F)
        val heightRowTree = (halfOfHeight + 0F)..(halfOfHeight + 200F)
        val heightRowFour =(halfOfHeight + 300F)..(halfOfHeight + 500F)

        if (event?.action == ACTION_DOWN) {
            when {
                    event.x in widthColumnOne && event.y in heightRowOne -> booking(0)
                    event.x in widthColumnTwo && event.y in heightRowOne -> booking(1)
                    event.x in widthColumnOne && event.y in heightRowTwo -> booking(2)
                    event.x in widthColumnTwo && event.y in heightRowTwo -> booking(3)
                    event.x in widthColumnOne && event.y in heightRowTree -> booking(4)
                    event.x in widthColumnTwo && event.y in heightRowTree -> booking(5)
                    event.x in widthColumnOne && event.y in heightRowFour -> booking(6)
                    event.x in widthColumnTwo && event.y in heightRowFour -> booking(7)
                }
        }
        return true
    }

    // menyimpan informasi status booking
    private fun booking(position: Int) {
        for (seat in seats) {
            seat.isBooked = false
        }
        seats[position].apply {
            seat = this
            isBooked = true
        }
        invalidate()
    }
}