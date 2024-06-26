package com.balius.coincap.ui.coinDetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.balius.coincap.R
import com.balius.coincap.databinding.FragmentCoinDetailBinding
import com.balius.coincap.model.model.chart.CandleChartData
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class CoinDetailFragment : Fragment() {
    private var _binding: FragmentCoinDetailBinding? = null
    private val binding get() = _binding

    private val viewModel: CoinDetailViewModel by viewModel()

    private lateinit var coinSymbol: String

    private var todayUnix: Long = 0
    private var yesterdayUnix: Long = 0
    private var unix6HAgo: Long = 0
    private var unix12HAgo: Long = 0

    private var isDailyChartSelected = true

    private var is6HChartSelected = false
    private var is12HChartSelected = false
    private var isCandleChartSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoinDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding?.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = CoinDetailFragmentArgs.fromBundle(requireArguments())
        val coinName = args.coin

        todayUnix = getCurrentUnixTime()
        yesterdayUnix = getUnixTimeYesterday()
        unix6HAgo = getUnixTime6HoursAgo()
        unix12HAgo = getUnixTime12HoursAgo()



        viewModel.getDetails(coinName)


        viewModel.detail.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = false
            binding?.txtName?.text = it.name
            binding?.txtSymbol?.text = it.symbol
            binding?.txtRank?.text = it.rank

            coinSymbol = it.symbol.toString()
            //charts
            viewModel.getCandles(coinSymbol, yesterdayUnix, todayUnix, "daily")


            binding?.txtPrice?.text = "$${FormatNumber(it.priceUsd.toString())}"

            val changePrice = it.changePercent24Hr?.toDouble()
            val truncatedNumber = String.format("%.2f", changePrice).toDouble()

            if (changePrice!! > 0) {
                binding?.imgDropup?.visibility = View.VISIBLE
                binding?.imgDropdown?.visibility = View.GONE
                binding?.txtChangePercent?.setTextColor(Color.GREEN)
                binding?.txtChangePercent?.text = "$truncatedNumber%"

            } else {
                binding?.imgDropup?.visibility = View.GONE
                binding?.imgDropdown?.visibility = View.VISIBLE
                binding?.txtChangePercent?.setTextColor(Color.RED)

                val formattedNumber = truncatedNumber * -1
                binding?.txtChangePercent?.text = "$formattedNumber%"

            }

            binding?.txtSupply?.text = FormatStats(it.supply.toString())
            if (it.maxSupply.toString().equals("_")) {
                binding?.txtMaxSupply?.text = "_"
            } else {
                binding?.txtMaxSupply?.text = FormatStats(it.maxSupply.toString())
            }

            binding?.txtVolume?.text = "$${FormatStats(it.volumeUsd24Hr.toString())}"
            binding?.txtMarketcap?.text = "$${FormatStats(it.marketCapUsd.toString())}"

        }


        //calCulate Rsi
        viewModel.prices.observe(viewLifecycleOwner) {

            viewModel.calculateRSI(it, 14)
        }




        val gray = ContextCompat.getColor(requireContext(), R.color.darker_gray)
        viewModel.rsi.observe(viewLifecycleOwner) {rsi->
            binding?.txtRsi?.text = rsi.toString()

            val formattedString = "%.2f".format(rsi)
            binding?.rsiChart?.setSpeed(formattedString.toDouble())


        }

        binding?.imgBack?.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error pls refresh", Toast.LENGTH_SHORT).show()
            binding?.progress?.visibility = View.GONE

        }


        val yellow = ContextCompat.getColor(requireContext(), R.color.yellow)


        //intervals listener
        binding?.lblD1?.setOnClickListener {
            if (!isDailyChartSelected) {
                isDailyChartSelected = true
                is6HChartSelected = false
                is12HChartSelected = false
                updateChart(coinName, yellow, coinSymbol)
            }
        }

        binding?.lblH6?.setOnClickListener {
            if (!is6HChartSelected) {
                isDailyChartSelected = false
                is6HChartSelected = true
                is12HChartSelected = false
                updateChart(coinName, yellow, coinSymbol)
            }
        }

        binding?.lblH12?.setOnClickListener {
            if (!is12HChartSelected) {
                isDailyChartSelected = false
                is6HChartSelected = false
                is12HChartSelected = true
                updateChart(coinName, yellow, coinSymbol)
            }
        }


        //refresh
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            val isScrolledToTop = binding?.scrollView?.canScrollVertically(-1)

            // If the ScrollView is not scrolled to the top, don't trigger the refresh
            if (!isScrolledToTop!!) {

                binding?.swipeRefreshLayout?.postDelayed(
                    {
                        binding?.progress?.visibility = View.VISIBLE
                        viewModel.getDetails(coinName)
                        updateChart(coinName, yellow, coinSymbol)
                        // Stop the refreshing animation
                        binding?.swipeRefreshLayout?.isRefreshing = false
                    },
                    2000
                ) // Simulate a delay of 2000 milliseconds (2 seconds) before stopping the refreshing animation
            } else {
                // If the ScrollView is scrolled to the top, don't trigger the refresh action
                binding?.swipeRefreshLayout?.isRefreshing = false
            }
        }


        //line chart for refresh
        binding?.lineChart?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disable SwipeRefreshLayout when touching the chart
                    binding?.swipeRefreshLayout?.isEnabled = false
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Re-enable SwipeRefreshLayout when touch event finishes
                    binding?.swipeRefreshLayout?.isEnabled = true
                }
            }
            false
        }

        viewModel.candleData.observe(viewLifecycleOwner) {
            viewModel.getPricesList(it)
            binding?.progress?.visibility = View.GONE
            binding?.imgCandle?.isClickable = true
            binding?.imgLine?.isClickable = true
            binding?.lblD1?.isClickable = true
            binding?.lblH12?.isClickable = true
            binding?.lblH6?.isClickable = true

            if (isCandleChartSelected) {
                binding?.lineChart?.visibility = View.GONE
                binding?.candleChart?.visibility = View.VISIBLE
                showCandle(it)
            } else {
                binding?.lineChart?.visibility = View.VISIBLE
                binding?.candleChart?.visibility = View.GONE
                showChart(it, binding!!.lineChart)
            }

        }

        //img candle
        binding?.imgCandle?.setOnClickListener {
            binding?.imgCandle?.setColorFilter(
                resources.getColor(R.color.gray),
                PorterDuff.Mode.SRC_IN
            )
            binding?.imgLine?.setColorFilter(
                resources.getColor(R.color.darker_gray),
                PorterDuff.Mode.SRC_IN
            )
            isCandleChartSelected = true
            updateChart(coinName, yellow, coinSymbol)
            binding?.imgCandle?.isClickable = false
            binding?.imgLine?.isClickable = false

        }

        //img line chart
        binding?.imgLine?.setOnClickListener {
            binding?.imgCandle?.setColorFilter(
                resources.getColor(R.color.darker_gray),
                PorterDuff.Mode.SRC_IN
            )
            binding?.imgLine?.setColorFilter(
                resources.getColor(R.color.gray),
                PorterDuff.Mode.SRC_IN
            )
            binding?.imgCandle?.isClickable = false
            binding?.imgLine?.isClickable = false
            isCandleChartSelected = false
            updateChart(coinName, yellow, coinSymbol)
        }


    }

    private fun updateChart(coinName: String, yellow: Int, symbol: String) {
        if (!isCandleChartSelected) {
            // Update line chart based on selected state
            if (isDailyChartSelected) {
                binding?.lblD1?.setTextColor(yellow)
                binding?.lblH12?.setTextColor(Color.WHITE)
                binding?.lblH6?.setTextColor(Color.WHITE)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, yesterdayUnix, todayUnix, "daily")

            } else if (is6HChartSelected) {

                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(Color.WHITE)
                binding?.lblH6?.setTextColor(yellow)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, unix6HAgo, todayUnix, "6h")

            } else if (is12HChartSelected) {
                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(yellow)
                binding?.lblH6?.setTextColor(Color.WHITE)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, unix12HAgo, todayUnix, "12h")
            }

        } else {
            if (isDailyChartSelected) {
                binding?.lblD1?.setTextColor(yellow)
                binding?.lblH12?.setTextColor(Color.WHITE)
                binding?.lblH6?.setTextColor(Color.WHITE)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, yesterdayUnix, todayUnix, "daily")

            } else if (is6HChartSelected) {

                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(Color.WHITE)
                binding?.lblH6?.setTextColor(yellow)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, unix6HAgo, todayUnix, "6h")

            } else if (is12HChartSelected) {
                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(yellow)
                binding?.lblH6?.setTextColor(Color.WHITE)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, unix12HAgo, todayUnix, "12h")
            }

        }
    }


    fun FormatNumber(price: String): String {
        val coinPrice = price.toDouble()
        val df = DecimalFormat("#,###.##")
        val formattedNumber = df.format(coinPrice)
        return formattedNumber
    }

    fun FormatStats(price: String): String {
        val coinPrice = price.toDouble()
        val df = DecimalFormat("#,###")
        val formattedNumber = df.format(coinPrice)
        return formattedNumber
    }


    fun showChart(chartDataList: List<CandleChartData>, lineChart: LineChart) {
        // Prepare data entries for the chart
        val entries = ArrayList<Entry>()
        for ((index, data) in chartDataList.withIndex()) {
            // Here, we're using the closing price for the chart
            val closePrice = data.close
            entries.add(Entry(index.toFloat(), closePrice))
        }

        val orange = ContextCompat.getColor(lineChart.context, R.color.orange)
        val deepOrange = ContextCompat.getColor(lineChart.context, R.color.deep_orange)

        // Create a dataset from the entries
        val dataSet = LineDataSet(entries, "Closing Price")
        dataSet.color = orange
        dataSet.setDrawCircles(false)
        dataSet.setCircleColor(deepOrange)
        dataSet.valueTextColor = Color.BLACK

        // Create a LineData object from the dataset
        val lineData = LineData(dataSet)

        // Set data to the chart
        lineChart.data = lineData

        // Customize the appearance of the chart
        lineChart.description.isEnabled = false
        lineChart.setDrawGridBackground(false)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        // If you want to format X-axis labels differently, you can set a custom value formatter here
        // xAxis.valueFormatter = CustomXAxisValueFormatter()

        val yAxisRight = lineChart.axisRight
        yAxisRight.isEnabled = false

        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.textColor = Color.BLACK

        // Invalidate the chart to refresh
        lineChart.invalidate()
    }


    fun showCandle(data: List<CandleChartData>) {
        val candleStickChart: CandleStickChart? = binding?.candleChart

        // Sample data
        val candleEntries = mutableListOf<CandleEntry>()

        data.forEachIndexed { index, candleChartData ->
            val candleEntry = CandleEntry(
                index.toFloat(),
                candleChartData.high,
                candleChartData.low,
                candleChartData.open,
                candleChartData.close
            )
            candleEntries.add(candleEntry)
        }

        val dataSet = CandleDataSet(candleEntries, "Candle Data Set")
        dataSet.color = Color.rgb(80, 80, 80)
        dataSet.shadowColor = Color.DKGRAY
        dataSet.shadowWidth = 0.7f
        dataSet.decreasingColor = Color.RED
        dataSet.decreasingPaintStyle = Paint.Style.FILL
        dataSet.increasingColor = Color.rgb(122, 242, 84)
        dataSet.increasingPaintStyle = Paint.Style.FILL
        dataSet.neutralColor = Color.BLUE

        val candleData = CandleData(dataSet)
        candleStickChart?.data = candleData

        // Customize x-axis
        candleStickChart?.xAxis?.position = XAxis.XAxisPosition.BOTTOM
        candleStickChart?.xAxis?.setDrawGridLines(false)

        val yAxis = candleStickChart?.axisLeft
        yAxis?.setDrawGridLines(true) // Set true to show grid lines
        //yAxis?.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yAxis?.setDrawLabels(true) // Set true to show labels
        yAxis?.setLabelCount(5, true) // Set label count as per your requirement

        // Hide right Y-axis
        candleStickChart?.axisRight?.isEnabled = false

        // Disable zoom
        candleStickChart?.setScaleEnabled(true)

        candleStickChart?.invalidate()
    }

    fun getCurrentUnixTime(): Long {
        val unixTimeToday = Instant.now().epochSecond
        return unixTimeToday
    }


    fun getUnixTimeYesterday(): Long {
        val yesterday = LocalDateTime.now().minusDays(1)
        val unixTime = yesterday.toEpochSecond(ZoneOffset.UTC)
        return unixTime
    }

    fun getUnixTime6HoursAgo(): Long {
        val sixHoursAgo = LocalDateTime.now().minusHours(6)
        val unixTime = sixHoursAgo.toEpochSecond(ZoneOffset.UTC)
        return unixTime
    }

    fun getUnixTime12HoursAgo(): Long {
        val twelveHoursAgo = LocalDateTime.now().minusHours(12)
        val unixTime = twelveHoursAgo.toEpochSecond(ZoneOffset.UTC)
        return unixTime
    }


}

