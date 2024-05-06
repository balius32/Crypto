package com.balius.coincap.ui.coinDetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
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
import com.balius.coincap.model.model.chart.candle.CandleChartData
import com.balius.coincap.model.model.chart.line.ChartData
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.System.currentTimeMillis
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

class CoinDetailFragment : Fragment() {
    private var _binding: FragmentCoinDetailBinding? = null
    private val binding get() = _binding

    private val viewModel: CoinDetailViewModel by viewModel()
    private lateinit var coinSymbol: String
    private var todayUnix: Long = 0
    private var weekAgoUnix: Long = 0
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

        todayUnix =  getCurrentUnixTime()
        weekAgoUnix = getUnixTimeOneWeekAgo()


        viewModel.getDetails(coinName)
        viewModel.getChartDetail(coinName, "d1")


        viewModel.detail.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = false
            binding?.txtName?.text = it.name
            binding?.txtSymbol?.text = it.symbol
            binding?.txtRank?.text = it.rank

            coinSymbol = it.symbol.toString()


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
            if (it.maxSupply.toString().equals("_")){
                binding?.txtMaxSupply?.text = "_"
            }else{
                binding?.txtMaxSupply?.text = FormatStats(it.maxSupply.toString())
            }

            binding?.txtVolume?.text = "$${FormatStats(it.volumeUsd24Hr.toString())}"
            binding?.txtMarketcap?.text = "$${FormatStats(it.marketCapUsd.toString())}"

        }

        viewModel.chartData.observe(viewLifecycleOwner) {
            viewModel.getPricesList(it)
            binding?.swipeRefreshLayout?.isRefreshing = false
            binding?.lineChart?.visibility = View.VISIBLE
            binding?.candleChart?.visibility = View.GONE
            binding?.lblD1?.isClickable = true
            binding?.lblH12?.isClickable = true
            binding?.lblH6?.isClickable = true
            binding?.progress?.visibility = View.GONE
            binding?.imgCandle?.isClickable = true
            binding?.imgLine?.isClickable = true
            showChart(it, binding!!.lineChart)
        }

        viewModel.prices.observe(viewLifecycleOwner) {
            Log.e("prices ", it.toString())
            viewModel.calculateRSI(it, 14)
        }
        viewModel.rsi.observe(viewLifecycleOwner) {
            binding?.txtRsi?.text = it.toString()
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
            binding?.progress?.visibility = View.GONE
            binding?.imgCandle?.isClickable = true
            binding?.imgLine?.isClickable = true
            binding?.lblD1?.isClickable = true
            binding?.lblH12?.isClickable = true
            binding?.lblH6?.isClickable = true
            Log.e("chart data ", it.toString())
            binding?.lineChart?.visibility = View.GONE
            binding?.candleChart?.visibility = View.VISIBLE
            showCandle(it)
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
                viewModel.getChartDetail(coinName, "d1")

            } else if (is6HChartSelected) {

                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(Color.WHITE)
                binding?.lblH6?.setTextColor(yellow)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getChartDetail(coinName, "h6")

            } else if (is12HChartSelected) {
                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(yellow)
                binding?.lblH6?.setTextColor(Color.WHITE)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getChartDetail(coinName, "h12")
            }

        } else {
            if (isDailyChartSelected) {
                binding?.lblD1?.setTextColor(yellow)
                binding?.lblH12?.setTextColor(Color.WHITE)
                binding?.lblH6?.setTextColor(Color.WHITE)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, weekAgoUnix,todayUnix, "D")

            } else if (is6HChartSelected) {

                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(Color.WHITE)
                binding?.lblH6?.setTextColor(yellow)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol,  weekAgoUnix,todayUnix, "360")

            } else if (is12HChartSelected) {
                binding?.lblD1?.setTextColor(Color.WHITE)
                binding?.lblH12?.setTextColor(yellow)
                binding?.lblH6?.setTextColor(Color.WHITE)
                binding?.lblD1?.isClickable = false
                binding?.lblH12?.isClickable = false
                binding?.lblH6?.isClickable = false
                viewModel.getCandles(coinSymbol, weekAgoUnix,todayUnix, "720")
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


    fun showChart(chartDataList: List<ChartData>, lineChart: LineChart) {
        // Prepare data entries for the chart
        val entries = ArrayList<Entry>()
        for (data in chartDataList) {
            data.time?.let { time ->
                data.priceUsd?.toFloat()?.let { price ->
                    entries.add(Entry(time.toFloat(), price))
                }
            }
        }


        val orange = ContextCompat.getColor(requireContext(), R.color.orange)
        val deepOrange = ContextCompat.getColor(requireContext(), R.color.deep_orange)
        // Create a dataset from the entries
        val dataSet = LineDataSet(entries, "Price (USD)")
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
        xAxis.valueFormatter = DayMonthValueFormatter()

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

        // Disable zoom
        candleStickChart?.setScaleEnabled(true)

        candleStickChart?.invalidate()
    }

    fun getCurrentUnixTime(): Long {
        val unixTimeToday = Instant.now().epochSecond
        return unixTimeToday
    }

    fun getUnixTimeOneWeekAgo(): Long {
        val unixTimeToday = Instant.now().epochSecond


        val unixTimeOneWeekAgo = unixTimeToday - (7 * 24 * 60 * 60)

        return unixTimeOneWeekAgo
    }


}

class DayMonthValueFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        // Convert timestamp to formatted date string
        val date = Date(value.toLong())
        return dateFormat.format(date)
    }
}