package com.balius.coincap.ui.coinDetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.balius.coincap.R
import com.balius.coincap.databinding.FragmentCoinDetailBinding
import com.balius.coincap.model.model.chart.ChartData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CoinDetailFragment : Fragment() {
    private var _binding: FragmentCoinDetailBinding? = null
    private val binding get() = _binding

    private val viewModel: CoinDetailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoinDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = CoinDetailFragmentArgs.fromBundle(requireArguments())
        val coinName = args.coin


        viewModel.getDetails(coinName)
        viewModel.getChartDetail(coinName, "d1")

        viewModel.detail.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = false
            binding?.txtName?.text = it.name
            binding?.txtSymbol?.text = it.symbol
            binding?.txtRank?.text = it.rank


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
            binding?.txtMaxSupply?.text = FormatStats(it.maxSupply.toString())
            binding?.txtVolume?.text = "$${FormatStats(it.volumeUsd24Hr.toString())}"
            binding?.txtMarketcap?.text = "$${FormatStats(it.marketCapUsd.toString())}"


        }

        viewModel.chartData.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = false
            binding?.progress?.visibility = View.GONE
            showChart(it, binding!!.lineChart)
        }

        binding?.imgBack?.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.isError.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),"Error pls refresh",Toast.LENGTH_SHORT).show()
        }




        binding?.swipeRefreshLayout?.setOnRefreshListener {
            val isScrolledToTop = binding?.scrollView?.canScrollVertically(-1)

            // If the ScrollView is not scrolled to the top, don't trigger the refresh
            if (!isScrolledToTop!!) {

                binding?.swipeRefreshLayout?.postDelayed(
                    {
                        viewModel.getDetails(coinName)
                        viewModel.getChartDetail(coinName, "d1")
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

    class DayMonthValueFormatter : ValueFormatter() {
        private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

        override fun getFormattedValue(value: Float): String {
            // Convert timestamp to formatted date string
            val date = Date(value.toLong())
            return dateFormat.format(date)
        }
    }

}