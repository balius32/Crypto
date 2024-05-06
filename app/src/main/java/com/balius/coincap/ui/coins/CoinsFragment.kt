package com.balius.coincap.ui.coins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balius.coincap.databinding.FragmentCoinsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoinsFragment : Fragment(), CoinsAdapter.CoinActionListener {
    private var _binding: FragmentCoinsBinding? = null
    private val binding get() = _binding

    private val viewModel: CoinsViewModel by viewModel()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoinsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCoins()

        viewModel.isError.observe(viewLifecycleOwner) {
            if (it) {
                binding?.swipeRefreshLayout?.isRefreshing = false
                binding?.progress?.visibility = View.GONE
                Toast.makeText(requireContext(), "Error pls refresh", Toast.LENGTH_LONG).show()
            }
        }



        viewModel.coins.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = false

            binding?.progress?.visibility = View.GONE

            val adapter = CoinsAdapter(requireActivity(), it, this)
            binding?.recycleCoins?.adapter = adapter
            binding?.recycleCoins?.layoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            binding?.swipeRefreshLayout?.isRefreshing = false

        }

        binding?.swipeRefreshLayout?.setOnRefreshListener {

            if (binding!!.recycleCoins.layoutManager == null ) {
                // RecyclerView is empty, so refresh
                viewModel.getCoins()
            }
            else {
                // RecyclerView is not empty, scroll to the top first
                if ((binding!!.recycleCoins.layoutManager as LinearLayoutManager)
                        .findFirstCompletelyVisibleItemPosition() == 0
                ) {
                    // Call your refresh function
                    viewModel.getCoins()

                } else {
                    // Scroll RecyclerView to the top first
                    binding?.recycleCoins?.smoothScrollToPosition(0)
                }
            }


        }


    }


    override fun onCoinSelect(coinId: String) {

        val action = CoinsFragmentDirections.actionCoinsFragmentToCoinDetailFragment(coinId)
        findNavController().navigate(action)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}